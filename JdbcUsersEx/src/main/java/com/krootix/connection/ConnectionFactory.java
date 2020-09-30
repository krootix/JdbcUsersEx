package com.krootix.connection;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionFactory {

    Connection newConnection() throws SQLException;

    void close() throws SQLException;

}