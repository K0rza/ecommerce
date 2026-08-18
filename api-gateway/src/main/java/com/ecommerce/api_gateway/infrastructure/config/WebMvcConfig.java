package com.ecommerce.api_gateway.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ecommerce.api_gateway.infrastructure.requests.RequestHandler;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final RequestHandler hanler;

    public WebMvcConfig(RequestHandler hanler) {
        this.hanler = hanler;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(hanler).addPathPatterns("/api/**");
    }
}
