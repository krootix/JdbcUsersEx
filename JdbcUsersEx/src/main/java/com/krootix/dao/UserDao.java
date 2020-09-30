package com.krootix.dao;

import com.krootix.entity.User;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

public interface UserDao {

    boolean deleteUserById(long id) throws SQLException;

    void createDbUser() throws SQLException, IOException, URISyntaxException;

    List<User> selectAll() throws SQLException;

    User findUserById(long id) throws SQLException;

    int updateUser(long id, User user) throws SQLException;

}