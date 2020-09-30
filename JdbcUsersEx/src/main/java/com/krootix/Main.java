package com.krootix;

import com.krootix.connection.ConnectionFactory;
import com.krootix.connection.ConnectionFactoryJdbc;
import com.krootix.connection.tx.TransactionManager;
import com.krootix.connection.tx.TransactionManagerImpl;
import com.krootix.dao.UserDao;
import com.krootix.dao.UserDaoJdbcImpl;
import com.krootix.entity.User;
import com.krootix.service.UserService;
import com.krootix.utils.FileReader;
import com.krootix.utils.PropertiesValidator;

import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {


        User user;
        List<User> users;

        FileReader fileReader = new FileReader(PropertiesValidator::new);

        ConnectionFactory connectionFactory = new ConnectionFactoryJdbc(fileReader.readProperties());
        TransactionManager transactionManager = new TransactionManagerImpl(connectionFactory);

        UserDao userDao = new UserDaoJdbcImpl(connectionFactory, fileReader);

        UserService userService = new UserService(transactionManager, userDao);

        userService.createDataBase();
        System.out.println("Database has been created");
        System.out.println("#--------------------------------------------------------------------------#");

        System.out.println("All users:");
        users = userService.SelectAll();
        users.forEach(System.out::println);
        System.out.println("#--------------------------------------------------------------------------#");

        System.out.println("User №1:");

        user = userService.findUser(1);
        System.out.println(user.toString());
        System.out.println("#--------------------------------------------------------------------------#");

        User newUser = new User();
        newUser.setName("Pablo");
        newUser.setSurname("Salvadore");
        newUser.setEmail("Pablo20@mail.ru");

        if (userService.updateUser(1, newUser) == 1)
            System.out.println("User №1 has been updated");
        System.out.println("#--------------------------------------------------------------------------#");

        System.out.println("All users:");
        users = userService.SelectAll();
        users.forEach(System.out::println);
        System.out.println("#--------------------------------------------------------------------------#");

        System.out.println("User №1  has been deleted");
        boolean isSuccess = userService.deleteUser(1);
        System.out.println("isSuccess is: " + isSuccess);
        System.out.println("#--------------------------------------------------------------------------#");

        System.out.println("All users:");
        users = userService.SelectAll();
        users.forEach(System.out::println);
        System.out.println("#--------------------------------------------------------------------------#");
    }
}