package com.krootix.dao;

import com.krootix.Main;
import com.krootix.entity.User;
import com.krootix.service.UserService;
import com.krootix.utils.ConnectionFactory;
import com.krootix.utils.ConnectionFactoryJdbc;
import com.krootix.utils.DBSystemException;
import com.krootix.utils.JdbcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserDaoJdbcImpl implements UserDao {
    private final ConnectionFactory factory = new ConnectionFactoryJdbc();

    public static final String SELECT_ALL_SQL = "SELECT * FROM Users";
    public static final String FIND_BY_ID_SQL = "SELECT id, name, surname, email FROM Users WHERE id = ?";
    public static final String SELECT_BY_EMAIL = "SELECT id FROM User WHERE email = ?";
    public static final String UPDATE_SURNAME = "UPDATE Users SET surname = ? WHERE id = ?";
    public static final String DROP_TABLE_USER = "DROP TABLE IF EXISTS Users";
    public static final String CREATE_TABLE_USER = "CREATE TABLE Users (id INTEGER PRIMARY KEY NOT NULL, name VARCHAR(50) NOT NULL, surname VARCHAR(50) NOT NULL, email VARCHAR(100) NOT NULL UNIQUE)";
    public static final String INSERT_INTO_TABLE = "insert_into_table_users.sql";

    public static Logger LOGGER = LoggerFactory.getLogger(UserDaoJdbcImpl.class);

    @Override
    public User findById(int id) throws DBSystemException, SQLException {
        Connection conn = getConnection();
        //LOGGER.debug("connection %d: obtained", conn);
        PreparedStatement ps = null;
        ResultSet rs = null;
        //List<User> users = new ArrayList<User>();
        User user = new User();
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(FIND_BY_ID_SQL);
            ps.setLong(1, id);
            rs = ps.executeQuery();
            while (rs.next()) {
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("name"));
                user.setSurname(rs.getString("surname"));
                user.setEmail(rs.getString("email"));
                //users.add(u);
            }
            //conn.commit();
            LOGGER.info("user [" + id + "] has been found");
            //conn.close();
        } catch (SQLException e) {
            JdbcUtils.rollbackQuietly(conn);
            throw new DBSystemException("Can't execute SQL = '%d" + FIND_BY_ID_SQL + "'");
            //e.printStackTrace();
        } finally {
            JdbcUtils.closeQuietly(ps);
            JdbcUtils.closeQuietly(conn);
        }
        return user;
    }

    public int updateSurname(int id, String surname) throws DBSystemException, SQLException {
        Connection conn = getConnection();
        if (surname == "") surname = null;
        PreparedStatement ps = null;
        //ResultSet rs = null;
        //List<User> users = new ArrayList<User>();
        User user = new User();
        int isSucess = 0;
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(UPDATE_SURNAME);
            ps.setString(1, surname);
            ps.setLong(2, id);
            isSucess = ps.executeUpdate();
            //ps.close();
            conn.commit();
            if (isSucess == 1)
                LOGGER.info("User[" + id + "] has been updated.");
            else
                LOGGER.info("User[" + id + "] has not been updated.");
            //conn.close();
        } catch (SQLException e) {
            JdbcUtils.rollbackQuietly(conn);
            throw new DBSystemException("Can't execute SQL = '" + UPDATE_SURNAME + "'");
            //e.printStackTrace();
        } finally {
            JdbcUtils.closeQuietly(ps);
            JdbcUtils.closeQuietly(conn);
            return isSucess;
        }
        //return isSucess;
    }

    public void createDbUser() throws DBSystemException, SQLException, IOException, URISyntaxException {
        //System.out.println("sucess");
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(DROP_TABLE_USER);
            ps.execute();
            ps = conn.prepareStatement(CREATE_TABLE_USER);
            ps.execute();
            String data = readSQL(INSERT_INTO_TABLE);
            for (String retval : data.split(";")) {
                ps.execute(retval + ";");
            }
            //ps = conn.prepareStatement(readSQL(INSERT_INTO_TABLE));
            //ps.executeUpdate();
            conn.commit();
            LOGGER.info("Table of users has been created");
            //conn.close();
        } catch (SQLException e) {
            JdbcUtils.rollbackQuietly(conn);
            throw new DBSystemException("Can't create a table");
            //e.printStackTrace();
        } finally {
            JdbcUtils.closeQuietly(ps);
            JdbcUtils.closeQuietly(conn);
        }
        //return true;
    }

    private String readSQL(String file) throws URISyntaxException, IOException {
        Class clazz = Main.class;
        Path path = Paths.get(clazz.getClassLoader()
                .getResource(file).toURI());

        Stream<String> lines = Files.lines(path);
        String data = lines.collect(Collectors.joining("\n"));
        lines.close();
        return data;
    }

    public List<User> selectAll() throws DBSystemException, SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<User> users = new ArrayList<User>();
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(SELECT_ALL_SQL);
            rs = ps.executeQuery();
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getLong("id"));
                u.setName(rs.getString("name"));
                u.setSurname(rs.getString("surname"));
                u.setEmail(rs.getString("email"));
                users.add(u);
            }
            //conn.close();
        } catch (SQLException e) {
            JdbcUtils.rollbackQuietly(conn);
            throw new DBSystemException("Can't execute SQL = '" + SELECT_ALL_SQL + "'");
            //e.printStackTrace();
        } finally {
            JdbcUtils.closeQuietly(ps);
            JdbcUtils.closeQuietly(conn);
        }
        return users;
    }

    private Connection getConnection() {
        try {
            return factory.newConnection();
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    private boolean existWithEmail(Connection conn, String email) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(SELECT_BY_EMAIL);
        ps.setString(1, email);
        ResultSet resultSet = ps.executeQuery();
        return resultSet.next();
    }
}
