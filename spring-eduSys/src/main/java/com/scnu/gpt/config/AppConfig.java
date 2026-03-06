package com.scnu.gpt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Value("${api.fastgpt.url}")
    private String fastgptUrl;

    @Value("${api.fastgpt.key}")
    private String fastgptKey;
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // 暴露配置参数供其他组件使用
    @Bean
    public GptConfig fastGptConfig() {
        return new GptConfig(fastgptUrl, fastgptKey);
    }
    // 配置持有类
    public record GptConfig(String url, String key) {}
}