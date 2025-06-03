package com.flowly.config.props;

import lombok.Data;

@Data
//@ConfigurationProperties(prefix = "flowly.security.jwt")
public class JwtProperties {
    private String secret;
    private long expirationMs;
}