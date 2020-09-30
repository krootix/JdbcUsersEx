package com.krootix.connection.tx;

import java.sql.Connection;
import java.sql.SQLException;

public class AutoRollback implements AutoCloseable {

    private final Connection sqlConnection;
    private boolean committed;

    public AutoRollback(Connection sqlConnection) {
        this.sqlConnection = sqlConnection;
    }

    public void commit() throws SQLException {
        sqlConnection.commit();
        committed = true;
    }

    @Override
    public void close() throws SQLException {
        if (!committed) {
            sqlConnection.rollback();
        }
    }
}