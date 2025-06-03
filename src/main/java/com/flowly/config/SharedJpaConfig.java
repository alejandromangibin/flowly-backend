package com.flowly.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.*;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = {
        "com.flowly.shared.repository",
        "com.flowly.admin.repository"  // Admin repositories can also use shared entities
    },
    entityManagerFactoryRef = "sharedEntityManagerFactory",
    transactionManagerRef = "sharedTransactionManager"
)
public class SharedJpaConfig {

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean sharedEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("dataSource") DataSource dataSource
    ) {
        return builder
                .dataSource(dataSource)
                .packages("com.flowly.shared.model") // your shared entities
                .persistenceUnit("shared")
                .build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager sharedTransactionManager(
            @Qualifier("sharedEntityManagerFactory") EntityManagerFactory emf
    ) {
        return new JpaTransactionManager(emf);
    }
}
