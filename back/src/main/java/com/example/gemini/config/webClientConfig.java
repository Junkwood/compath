package com.example.gemini.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class webClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        // 여기서 기본 설정을 해서 스프링 빈으로 등록합니다.
        return builder.baseUrl("https://generativelanguage.googleapis.com/v1beta").build();
    }
}
