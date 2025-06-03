// FlowlySecurityProperties.java
package com.flowly.config;

import com.flowly.config.props.CorsProperties;
import com.flowly.config.props.JwtProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "flowly.security")
public record FlowlySecurityProperties(
    List<String> publicPaths,
    JwtProperties jwt,
    CorsProperties cors
) {}
