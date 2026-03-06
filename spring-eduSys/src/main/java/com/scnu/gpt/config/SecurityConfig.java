package com.scnu.gpt.config;
import com.scnu.gpt.util.JwtAuthFilter;
import com.scnu.gpt.util.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    //AuthenticationManager验证登录信息时所用的加密、解密算法
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    // 载入JwtAuthFilter安全令牌验证，每次访问接口前都自动调用
    @Bean
    public JwtAuthFilter jwtAuthFilter(
            JwtUtils jwtUtils,
            UserDetailsService userDetailsService
    ) {
        return new JwtAuthFilter(jwtUtils, userDetailsService);
    }
    // 新增：CORS 配置源
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*")); // 允许所有源
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 关键包含OPTIONS
        configuration.setAllowedHeaders(Arrays.asList("*")); // 允许所有头
        configuration.setAllowCredentials(true); // 允许凭证

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 应用到所有路径
        return source;
    }


    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            JwtAuthFilter jwtAuthFilter
    ) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 启用CORS配置
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // 关键：放行所有OPTIONS请求
                        .requestMatchers(HttpMethod.POST,"/user/login").permitAll()     // 允许匿名访问登录接口
                        .requestMatchers("/v3/api-docs/**").permitAll() //接口文档放行
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/gpt/**").permitAll() // 放行所有gpt访问路径
                        .requestMatchers("/LrnAst/**").permitAll()
                        .anyRequest().authenticated()       //其它接口都要验证
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);    //添加过滤器

        return http.build();
    }
    //使用自带的AuthenticationManager，自动装配passwordEncoder和UserDeatails的Bean
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}