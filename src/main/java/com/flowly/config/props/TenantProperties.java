package com.flowly.config.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "flowly.tenants")
public class TenantProperties {
    private String defaultSchema;
}