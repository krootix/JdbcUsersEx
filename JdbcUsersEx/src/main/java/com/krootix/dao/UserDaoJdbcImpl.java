package com.krootix.dao;

import com.krootix.connection.ConnectionFactory;
import com.krootix.entity.User;
import com.krootix.utils.FileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJdbcImpl implements UserDao {

    private final ConnectionFactory factory;
    private final FileReader fileReader;

    private static final Logger logger = LoggerFactory.getLogger(UserDaoJdbcImpl.class);

    public static final String SELECT_ALL_SQL = "SELECT * FROM Users";
    public static final String FIND_BY_ID_SQL = "SELECT id, name, surname, email FROM Users WHERE id = ?";
    public static final String UPDATE_USER = "UPDATE Users SET name = ?, surname = ?, email = ? WHERE id = ?";
    public static final String DELETE_USER_BY_ID = "DELETE FROM Users WHERE id = ?";
    public static final String DROP_TABLE_USER = "DROP TABLE IF EXISTS Users";
    public static final String CREATE_TABLE_USER = "CREATE TABLE Users (id INTEGER PRIMARY KEY NOT NULL, name VARCHAR(50) NOT NULL, surname VARCHAR(50) NOT NULL, email VARCHAR(100) NOT NULL UNIQUE)";
    public static final String INSERT_INTO_TABLE = "insert_into_table_users.sql";

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String SURNAME = "surname";
    public static final String EMAIL = "email";

    public UserDaoJdbcImpl(ConnectionFactory factory, FileReader fileReader) {
        this.factory = factory;
        this.fileReader = fileReader;
    }

    @Override
    public User findUserById(long id) throws SQLException {

        User user = new User();

        final PreparedStatement preparedStatementFindById = createStatementFromConnection(FIND_BY_ID_SQL);
        // remember, that you should fill the statement id before you call executeQuery method in try-with-resources
        preparedStatementFindById.setLong(1, id);

        try (Connection sqlConnection = getConnection();
             preparedStatementFindById;
             ResultSet resultSet = preparedStatementFindById.executeQuery()) {
            sqlConnection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            sqlConnection.setAutoCommit(false);
            while (resultSet.next()) {
                user = createUserFromResultSet(resultSet);
            }
            logger.info("user [{}] has been found", id);
        }
        return user;
    }

    @Override
    public boolean deleteUserById(long id) throws SQLException {
        boolean isSuccess;
        final PreparedStatement preparedStatementDeleteById = createStatementFromConnection(DELETE_USER_BY_ID);
        // remember, that you should fill the statement id before you
        // call executeQuery method in try-with-resources
        preparedStatementDeleteById.setLong(1, id);

        try (Connection sqlConnection = getConnection();
             preparedStatementDeleteById) {
            sqlConnection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            sqlConnection.setAutoCommit(false);
            isSuccess = preparedStatementDeleteById.execute();
            logger.info("user [{}] has been deleted", id);
        }
        return isSuccess;
    }

    public int updateUser(long id, User user) throws SQLException {

        int isSuccess = 0;

        final PreparedStatement preparedStatementUpdateUser = createStatementFromConnection(UPDATE_USER);
        preparedStatementUpdateUser.setString(1, user.getName());
        preparedStatementUpdateUser.setString(2, user.getSurname());
        preparedStatementUpdateUser.setString(3, user.getEmail());
        preparedStatementUpdateUser.setLong(4, id);

        try (Connection sqlConnection = getConnection(); preparedStatementUpdateUser) {
            sqlConnection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            sqlConnection.setAutoCommit(false);

            isSuccess = preparedStatementUpdateUser.executeUpdate();
            sqlConnection.commit();
            if (isSuccess == 1)
                logger.info("User[{}] has been updated.", id);
            else
                logger.info("User[{}] has not been updated.", id);
        }
        return isSuccess;
    }

    public void createDbUser() throws SQLException, IOException, URISyntaxException {
        logger.info("Creating table of users");

        final PreparedStatement preparedStatementDropTable = createStatementFromConnection(DROP_TABLE_USER);
        final PreparedStatement preparedStatementCreateTable = createStatementFromConnection(CREATE_TABLE_USER);

        try (Connection sqlConnection = getConnection(); preparedStatementDropTable; preparedStatementCreateTable) {
            sqlConnection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            sqlConnection.setAutoCommit(false);
            preparedStatementDropTable.execute();
            preparedStatementCreateTable.execute();
            String data = fileReader.readSQLFile(INSERT_INTO_TABLE);
            for (String returnValue : data.split(";")) {
                preparedStatementCreateTable.execute(returnValue + ";");
            }
            sqlConnection.commit();
            logger.info("Table of users has been created");
        }
    }

    public List<User> selectAll() throws SQLException {
        List<User> users = new ArrayList<User>();

        final PreparedStatement preparedStatementSelectAll = createStatementFromConnection(SELECT_ALL_SQL);

        try (Connection sqlConnection = getConnection(); preparedStatementSelectAll;
             ResultSet resultSet = preparedStatementSelectAll.executeQuery()) {
            sqlConnection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            sqlConnection.setAutoCommit(false);
            while (resultSet.next())
                users.add(createUserFromResultSet(resultSet));
        }
        return users;
    }

    private User createUserFromResultSet(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong(ID));
        user.setName(resultSet.getString(NAME));
        user.setSurname(resultSet.getString(SURNAME));
        user.setEmail(resultSet.getString(EMAIL));
        return user;
    }

    private Connection getConnection() {
        try {
            return factory.newConnection();
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    private PreparedStatement createStatementFromConnection(String sql) throws SQLException {
        return getConnection().prepareStatement(sql);
    }
}