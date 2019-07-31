package com.krootix.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class JdbcUtils {
    public static Logger LOGGER = LoggerFactory.getLogger(JdbcUtils.class);

    private static boolean initialized;

    private JdbcUtils() {
    }

    public void DbCreate() {

    }

    public static synchronized void initDriver(String driverClass) {
        if (!initialized) {
            try {
                Class.forName(driverClass);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException("Can't initialize driver: '" + driverClass.toString() + "'");
            }
            initialized = true;
        }
    }

    public static void rollbackQuietly(Connection conn) throws SQLException {
        if (conn != null) {
            LOGGER.warn("connection " + conn + ": rollback");
            conn.rollback();
        }
    }

    public static void closeQuietly(Connection conn) throws SQLException {
        if (conn != null) {
            LOGGER.debug("connection " + conn + ": closed");
            conn.close();
        }
    }

    public static void closeQuietly(PreparedStatement stmt) throws SQLException {
        if (stmt != null) {
            LOGGER.debug("PreparedStatement " + stmt + ": closed");
            stmt.close();
        }
    }
}
