package com.example.common.config;

import com.example.permission.interceptor.PermissionInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final PermissionInterceptor permissionInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/api/**") // /api로 시작하는 모든 요청을 가로챔!
                .excludePathPatterns("/api/notifications/subscribe/**"); // 혹시 SSE 알림 구독 같은 권한 예외가 필요한 URL이 있다면 여기에 추가
    }
}