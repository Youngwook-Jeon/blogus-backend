package com.young.blogusbackend.infra.constant;

import com.young.blogusbackend.SpringApplicationContext;
import com.young.blogusbackend.infra.config.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SecurityConstant {

    @Autowired
    private AppProperties appProperties;

    public String getTokenSecret() {
        return appProperties.getTokenSecret();
    }
}
