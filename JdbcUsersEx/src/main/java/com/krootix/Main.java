package com.krootix;

import com.krootix.dao.UserDaoJdbcImpl;
import com.krootix.entity.User;
import com.krootix.service.UserService;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws Exception {
        User user;
        List<User> users;
        UserService userService = new UserService(new UserDaoJdbcImpl());

        //userService.createDataBase();

        users = userService.SelectAll();
        users.stream().forEach(System.out::println);

        user = userService.findUser(1);
        System.out.println(user.toString());


        //String newSurname = "Brown";  // Brown
        String newSurname = "Malone"; // Malone
        userService.updateSurname(1, newSurname);

        users = userService.SelectAll();
        users.stream().forEach(System.out::println);
    }
}
