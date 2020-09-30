package com.krootix.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactoryJdbc implements ConnectionFactory {
    private static final String HOST = "db.host";
    private static final String LOGIN = "db.login";
    private static final String PASSWORD = "db.password";

    private static final Logger logger = LoggerFactory.getLogger(ConnectionFactoryJdbc.class);

    Connection sqlConnection = null;

    private final Properties properties;

    public ConnectionFactoryJdbc(Properties properties) {
        this.properties = properties;
    }

    @Override
    public Connection newConnection() throws SQLException {
        sqlConnection = DriverManager.getConnection(properties.getProperty(HOST), properties.getProperty(LOGIN), properties.getProperty(PASSWORD));
        logger.debug("connection {}: opened", sqlConnection);
        return sqlConnection;
    }

    @Override
    public void close() throws SQLException {
        // with try-with-resources it closes automatically
        // without call this method
        logger.debug("connection {}: closed", sqlConnection);
        sqlConnection.close();
    }
}