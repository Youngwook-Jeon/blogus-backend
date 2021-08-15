package com.young.blogusbackend.infra.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "token")
@Data
public class AppProperties {

    private String secret;

    public String getTokenSecret() {
        return secret;
    }
}
