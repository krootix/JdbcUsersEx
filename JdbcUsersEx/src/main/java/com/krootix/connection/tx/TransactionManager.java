package com.krootix.connection.tx;

import java.util.concurrent.Callable;

public interface TransactionManager {

    <T> T doInTransaction(Callable<T> unitOfWork) throws Exception;

}