// src/main/java/com/example/airbnbproject/config/WebConfig.java
package com.example.airbnbproject.config;

import com.example.airbnbproject.interceptor.LoginCheckInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LoginCheckInterceptor loginCheckInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginCheckInterceptor)
                // 1) 기본은 전부 보호
                .addPathPatterns("/**")
                // 2) 공개 예외 (로그인 불필요)
                .excludePathPatterns(
                        "/", "/login", "/join",
                        "/error", "/error/**",
                        "/payment/success", "/payment/cancel", "/payment/fail",
                        "/reservation/accommodation/*/booked",
                        "/css/**", "/js/**", "/images/**", "/favicon.ico"
                );
    }

}
