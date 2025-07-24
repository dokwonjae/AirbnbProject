package com.example.airbnbproject.config;

import com.example.airbnbproject.interceptor.LoginCheckInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final LoginCheckInterceptor loginCheckInterceptor;

    @Autowired
    public WebConfig(LoginCheckInterceptor loginCheckInterceptor) {
        this.loginCheckInterceptor = loginCheckInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginCheckInterceptor)
                .addPathPatterns("/info/**", "/reservation/**", "/mypage/**")  // 로그인 필요한 페이지
                .excludePathPatterns("/", "/login", "/join", "/css/**", "/images/**", "/js/**"); // 예외 허용
    }
}
