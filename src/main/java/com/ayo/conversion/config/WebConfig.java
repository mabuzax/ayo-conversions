package com.ayo.conversion.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*") // May need to replace this with a AyoProduction restricted/allowed ip for
                                     // security
                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS")
                .allowedHeaders("Content-Type", "Date", "Total-Count", "loginInfo")
                .exposedHeaders("Content-Type", "Date", "Total-Count", "loginInfo")
                .maxAge(3600);
    }
}
