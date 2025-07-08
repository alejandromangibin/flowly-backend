package com.flowly.config.multitenancy;

import org.springframework.stereotype.Component;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

@Component
public class SchemaMultiTenantConnectionProvider implements MultiTenantConnectionProvider<String>,  HibernatePropertiesCustomizer {

    @Autowired
    @Qualifier("dataSource")
    private final DataSource dataSource;

    public SchemaMultiTenantConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    // @Override
    // public Connection getConnection(String tenantIdentifier) throws SQLException {
    //     Connection connection = getAnyConnection();
    //     try (Statement statement = connection.createStatement()) {
    //         statement.execute("SET search_path TO " + tenantIdentifier);
    //     }
    //     return connection;
    // }
    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        System.out.println("Getting connection for tenant: " + tenantIdentifier);
        Connection connection = dataSource.getConnection();
        
        try (Statement statement = connection.createStatement()) {
            // Set the search_path
            statement.execute("SET search_path TO " + tenantIdentifier);
            System.out.println("Set search_path to: " + tenantIdentifier);
            
            // VERIFY the search_path was actually set
            try (var rs = statement.executeQuery("SHOW search_path")) {
                if (rs.next()) {
                    System.out.println("Current search_path after setting: " + rs.getString(1));
                }
            }
        }
        return connection;
    }


    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class<?> unwrapType) {
        return unwrapType.isAssignableFrom(SchemaMultiTenantConnectionProvider.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        if (isUnwrappableAs(unwrapType)) {
            return (T) this;
        }
        throw new IllegalArgumentException("Unknown unwrap type: " + unwrapType);
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, this);
    }

}
