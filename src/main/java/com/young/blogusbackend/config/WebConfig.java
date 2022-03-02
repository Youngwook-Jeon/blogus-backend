package com.young.blogusbackend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final Environment env;

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
                .allowedOrigins(env.getProperty("blogus.client"))
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE", "PATCH", "HEAD")
                .maxAge(3600L)
                .allowedHeaders("*")
                .exposedHeaders("Origin", "Authorization", "Set-Cookie", "Content-Type", "Accept",
                        "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials")
                .allowCredentials(true);
    }
}
