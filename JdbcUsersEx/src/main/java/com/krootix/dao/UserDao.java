package com.krootix.dao;

import com.krootix.entity.User;
import com.krootix.utils.DBSystemException;

import java.sql.SQLException;

public interface UserDao {
    public User findById(int id) throws DBSystemException, SQLException, DBSystemException;
    public int updateSurname(int id, String surname) throws DBSystemException, SQLException;
//    public void updateSurname(int id, String surname) throws DBSystemException, SQLException;
}
