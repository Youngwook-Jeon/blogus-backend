package com.young.blogusbackend.infra.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties(prefix = "token")
@Data
public class AppProperties {

//    private final Environment environment;

    private String secret;

    public String getTokenSecret() {
        return secret;
    }
}
