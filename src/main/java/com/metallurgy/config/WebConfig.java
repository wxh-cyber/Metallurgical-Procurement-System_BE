package com.metallurgy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final JwtInterceptor jwtInterceptor;
    public WebConfig(JwtInterceptor jwtInterceptor) { this.jwtInterceptor = jwtInterceptor; }
    @Override public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**").allowedOriginPatterns("http://localhost:*").allowedMethods("*").allowedHeaders("*").allowCredentials(true);
    }
    @Override public void addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor).addPathPatterns("/api/**").excludePathPatterns("/api/auth/login", "/api/auth/register");
    }
}
