package com.krootix.service;

import com.krootix.connection.tx.TransactionManager;
import com.krootix.dao.UserDao;
import com.krootix.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserService {

    public static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserDao userDao;
    private final TransactionManager txManager;

    public UserService(TransactionManager txManager, UserDao userDao) {
        this.txManager = txManager;
        this.userDao = userDao;
    }

    public User findUser(int id) throws Exception {
        logger.debug("findUser method invoked.");
        return txManager.doInTransaction(() ->
                userDao.findUserById(id));
    }

    public void createDataBase() throws Exception {
        txManager.doInTransaction(() -> {
            userDao.createDbUser();
            return true;
        });
        logger.debug("createDataBase method invoked");
    }

    public int updateUser(int id, User user) throws Exception {
        int successState = txManager.doInTransaction(() ->
                userDao.updateUser(id, user));
        logger.debug("updateSurname method invoked. successState is {}", successState);
        return successState;
    }

    public boolean deleteUser(int id) throws Exception {
        logger.info("deleteUser method invoked.");
        boolean isSuccess = txManager.doInTransaction(() ->
                userDao.deleteUserById(id));
        logger.debug("updateSurname method invoked. isSuccess is {}", isSuccess);
        return isSuccess;
    }

    public List<User> SelectAll() throws Exception {
        List<User> users = txManager.doInTransaction(userDao::selectAll);
        logger.info("SelectAll method invoked");
        users.forEach(a -> logger.info(a.toString()));
        return users;
    }
}