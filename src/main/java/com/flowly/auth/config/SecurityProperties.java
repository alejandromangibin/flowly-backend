package com.flowly.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "flowly.security")
@Getter
@Setter
public class SecurityProperties {
    private List<String> publicPaths;
}
