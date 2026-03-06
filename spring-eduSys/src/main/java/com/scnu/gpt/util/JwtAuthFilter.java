package com.scnu.gpt.util;

import com.scnu.gpt.service.IUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;


public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final  UserDetailsService userDetailsService;
    public JwtAuthFilter(JwtUtils jwtUtils,  UserDetailsService userDetailsService ) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization"); // 获取认证头
        String username = null;
        String jwtToken = null;

        // 验证Authorization头格式 (Bearer token)
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7); // 提取纯Token (去掉"Bearer "前缀)
            username = jwtUtils.extractUsername(jwtToken); // 从Token中解码用户名
        }

        // 当用户名存在且当前安全上下文未认证时
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username); // 加载用户详情

            // 验证Token有效性（签名+过期时间）
            if (jwtUtils.validateToken(jwtToken, userDetails)) {
                // 创建已认证的令牌对象
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,  // 用户主体
                                null,        // 凭证(密码不需要)
                                userDetails.getAuthorities() // 用户权限集合
                        );

                // 将认证信息设置到安全上下文
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response); // 继续过滤器链执行
    }
}