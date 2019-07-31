package com.krootix.utils.tx;

import com.krootix.utils.ConnectionFactory;
import com.krootix.utils.ConnectionFactoryJdbc;
import com.krootix.utils.JdbcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.concurrent.Callable;

public class TransactionManagerImpl implements TransactionManager {
    public static Logger LOGGER = LoggerFactory.getLogger(TransactionManagerImpl.class);
    private static ThreadLocal<Connection> connectionHolder = new ThreadLocal<Connection>();
    //private static final ConnectionFactory factory = ConnectionFactoryFactory.newConnectionFactory();
    private static final ConnectionFactory factory = new ConnectionFactoryJdbc();

    public <T> T doInTransaction(Callable<T> unitOfWork) throws Exception {
        Connection connection = factory.newConnection();
        connection.setAutoCommit(false);
        connectionHolder.set(connection);
        LOGGER.debug("connectionHolder " + connectionHolder + ": set connection " + connection);
        try {
            T result = unitOfWork.call();
            connection.commit();
            LOGGER.debug("connection " + connection + ": committed");
            return result;
        } catch (Exception e) {
            connection.rollback();
            LOGGER.warn("connection " + connection + ": rollback");
            throw e;
        } finally {
            JdbcUtils.closeQuietly(connection);
            connectionHolder.remove();
        }
    }
}
