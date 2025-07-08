// src/main/java/com/flowly/config/props/CorsProperties.java
package com.flowly.config.props;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "flowly.security.cors")
public record CorsProperties(
    List<String> allowedOrigins
) {}
