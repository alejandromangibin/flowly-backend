package com.flowly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.flowly.config.FlowlySecurityProperties;
import com.flowly.config.props.MultiTenancyProperties;
import com.flowly.config.props.TenantProperties;

@SpringBootApplication(scanBasePackages = "com.flowly")
@EnableConfigurationProperties({ TenantProperties.class, FlowlySecurityProperties.class, MultiTenancyProperties.class})
public class FlowlyApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlowlyApplication.class, args);
	}

}
