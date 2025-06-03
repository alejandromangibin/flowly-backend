package com.flowly.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.flowly.shared.model", // your tenant-specific business model
        entityManagerFactoryRef = "tenantEntityManagerFactory",
        transactionManagerRef = "tenantTransactionManager"
)
public class TenantJpaConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean tenantEntityManagerFactory(   
        EntityManagerFactoryBuilder builder,
        DataSource dataSource,
        MultiTenantConnectionProvider<?> multiTenantConnectionProvider,
        CurrentTenantIdentifierResolver<?> tenantIdentifierResolver
    ) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.multi_tenant_connection_provider", multiTenantConnectionProvider);
        properties.put("hibernate.tenant_identifier_resolver", tenantIdentifierResolver);
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        return builder
            .dataSource(dataSource)
            .packages("com.flowly.shared.model") // adjust to your tenant entity package
            .persistenceUnit("tenant")
            .properties(properties)
            .build();
    }


    @Bean
    public PlatformTransactionManager tenantTransactionManager(
            @Qualifier("tenantEntityManagerFactory") EntityManagerFactory emf
    ) {
        return new JpaTransactionManager(emf);
    }
}
