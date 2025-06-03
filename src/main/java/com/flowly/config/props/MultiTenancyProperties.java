package com.flowly.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

import jakarta.validation.constraints.NotBlank;

@ConfigurationProperties(prefix = "flowly.multitenancy")
public record MultiTenancyProperties(
    @NotBlank String schemaPrefix
) {}
