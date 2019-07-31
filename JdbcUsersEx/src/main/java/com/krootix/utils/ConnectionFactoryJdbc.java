package com.krootix.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactoryJdbc implements ConnectionFactory {
    private static final String JDBC_URL = "jdbc:mysql://127.0.0.1:3306/my_db?serverTimezone=UTC&useSSL=false";
    private static final String LOGIN = "root";
    private static final String PASSWORD = "root";

    public static Logger LOGGER = LoggerFactory.getLogger(ConnectionFactoryJdbc.class);

    Connection connection = null;

    public Connection newConnection() throws SQLException {
        connection = DriverManager.getConnection(JDBC_URL, LOGIN, PASSWORD);
        LOGGER.debug("connection " + connection + ": opened");
        return connection;
        //return DriverManager.getConnection(JDBC_URL,LOGIN,PASSWORD);
    }

    public void close() throws SQLException {
        // nothing to close
        //Connection connection = DriverManager.getConnection(JDBC_URL,LOGIN,PASSWORD);
        LOGGER.debug("connection " + connection + ": closed");
        connection.close();
    }
}
