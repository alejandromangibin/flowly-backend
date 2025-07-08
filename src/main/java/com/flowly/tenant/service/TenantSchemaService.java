package com.flowly.tenant.service;

import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
// import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
@RequiredArgsConstructor
public class TenantSchemaService {

    // @Value("${spring.datasource.url}")
    // private String dbUrl;
    // @Value("${spring.datasource.username}")
    // private String dbUsername;
    // @Value("${spring.datasource.password}")
    // private String dbPassword;


    public void migrateSchema(String schemaName, JdbcTemplate jdbcTemplate) {
        validateSchemaName(schemaName);

        DataSource flywayDataSource = jdbcTemplate.getDataSource();
        
        // DataSourceBuilder.create()
        //     .url(dbUrl)
        //     .username(dbUsername)
        //     .password(dbPassword)
        //     .build();

        Flyway.configure()
              .dataSource(flywayDataSource)
              .schemas(schemaName)
              .defaultSchema(schemaName)
              .load()
              .migrate();
    }

    private void validateSchemaName(String schemaName) {
        if (!schemaName.matches("^[a-zA-Z0-9_]{1,32}$")) {
            throw new IllegalArgumentException("Invalid schema name");
        }
    }
}

