package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableWebMvc
public class CorsConfig implements WebMvcConfigurer {
    public CorsConfig() {
    }

    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("http://localhost:9527").allowCredentials(true).allowedMethods(new String[]{"GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH"}).maxAge(3600L);
    }
}