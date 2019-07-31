package com.krootix.service;

import com.krootix.dao.UserDao;
import com.krootix.dao.UserDaoJdbcImpl;
import com.krootix.entity.User;
import com.krootix.utils.tx.TransactionManager;
import com.krootix.utils.tx.TransactionManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

public class UserService {
    private UserDao userDao = new UserDaoJdbcImpl();
    TransactionManager txManager = new TransactionManagerImpl();
    public static Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User findUser(int id) throws Exception {
        //Logger LOGGER = LoggerFactory.getLogger(UserService.class);
        User user = txManager.doInTransaction(() -> {
            //((UserDaoJdbcImpl) userDao).findById(id);

            return ((UserDaoJdbcImpl) userDao).findById(id);
        });
        LOGGER.info("findUser method invoked.");
        return user;
    }

    public void createDataBase() throws Exception {
        txManager.doInTransaction(() -> {
            ((UserDaoJdbcImpl) userDao).createDbUser();
            return true;
        });
        LOGGER.info("createDataBaseb method invoked");
    }

    public int updateSurname(int id, String surname) throws Exception {
        int isSucess = txManager.doInTransaction(() -> {
            //((UserDaoJdbcImpl) userDao).updateSurname(id, surname);
            return ((UserDaoJdbcImpl) userDao).updateSurname(id, surname);
        });
        LOGGER.info("updateSurname method invoked. isSucess is " + isSucess);
        return isSucess;
    }

    public List<User> SelectAll() throws Exception {
        List<User> users = txManager.doInTransaction(new Callable<List<User>>() {
            public List<User> call() throws Exception {
                /*
                ((UserDaoJdbcImpl) userDao).selectAll();
                */
                return ((UserDaoJdbcImpl) userDao).selectAll();
            }
        });
        LOGGER.info("SelectAll method invoked");
        users.stream().forEach(a -> LOGGER.info(a.toString()));

        //forEach(LOGGER.info::);
        return users;
    }
}
