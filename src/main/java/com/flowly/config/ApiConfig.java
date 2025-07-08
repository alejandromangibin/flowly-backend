package com.flowly.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApiConfig {
    
    @Value("${flowly.api.base-url}")
    private String apiBaseUrl;

    public String getApiBaseUrl() {
        return apiBaseUrl;
    }
}
