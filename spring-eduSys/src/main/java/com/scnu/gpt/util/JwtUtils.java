package com.scnu.gpt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;  // 导入正确的SecretKey接口
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;  // 配置文件中的密钥字符串

    @Value("${jwt.expiration}")
    private long expiration;  // token有效期(毫秒)

    // 生成符合JCA标准的SecretKey (HMAC-SHA算法)
    private SecretKey getSigningKey() {
        // 1. 将字符串密钥转换为字节数组
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);

        // 2. 使用Keys工具类创建符合HMAC-SHA规范的SecretKey
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 生成JWT令牌
    public String generateToken(UserDetails userDetails) {
        // 获取签名密钥
        SecretKey signingKey = getSigningKey();

        return Jwts.builder()  // 创建JWT构建器
                .subject(userDetails.getUsername())  // 设置用户名作为主题
                .issuedAt(new Date())  // 设置签发时间
                .expiration(new Date(System.currentTimeMillis() + expiration))  // 设置过期时间
                .signWith(signingKey, Jwts.SIG.HS256)  // 使用SecretKey进行签名
                .compact();  // 生成并返回JWT字符串
    }

    // 验证令牌有效性
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);  // 提取用户名
        return username.equals(userDetails.getUsername()) &&  // 用户名匹配
                !isTokenExpired(token);  // 且token未过期
    }

    // 检查token是否过期
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());  // 过期时间早于当前时间
    }

    // 提取过期时间
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);  // 使用函数式接口提取声明
    }

    // 提取用户名
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);  // 提取主题(用户名)
    }

    // 通用声明提取方法
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        // 获取验证密钥
        SecretKey verificationKey = getSigningKey();

        Claims claims = Jwts.parser()  // 创建解析器
                .verifyWith(verificationKey)  // 设置验证密钥(SecretKey)
                .build()  // 构建解析器实例
                .parseSignedClaims(token)  // 解析并验证签名
                .getPayload();  // 获取有效载荷(声明部分)

        return claimsResolver.apply(claims);  // 应用解析函数
    }
}