package com.krootix.connection.tx;

import com.krootix.connection.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.concurrent.Callable;

public class TransactionManagerImpl implements TransactionManager {
    private static final Logger logger = LoggerFactory.getLogger(TransactionManagerImpl.class);

    private static final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();
    private final ConnectionFactory factory;

    public TransactionManagerImpl(ConnectionFactory factory) {
        this.factory = factory;
    }

    @Override
    public <T> T doInTransaction(Callable<T> unitOfWork) throws Exception {
        try (Connection sqlConnection = factory.newConnection();
             AutoRollback connectionWithAutoRollback = new AutoRollback(sqlConnection)) {
            sqlConnection.setAutoCommit(false);
            connectionHolder.set(sqlConnection);

            logger.debug("connectionHolder {}: set connection {}", connectionHolder, sqlConnection);

            T result = unitOfWork.call();
            connectionWithAutoRollback.commit();
            logger.debug("connection {}: committed", sqlConnection);
            return result;
        } finally {
            connectionHolder.remove();
        }
    }
}