<<<<<<<< HEAD:back/src/main/java/com/example/config/WebConfig.java
package com.example.config;
========
package com.example.common.config;
>>>>>>>> dev:back/src/main/java/com/example/common/config/WebConfig.java

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173")  // Vue 개발 서버
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
    }
}