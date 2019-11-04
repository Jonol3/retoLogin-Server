/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package retoLogin.control;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.logging.Logger;
import retoLogin.User;
import retoLogin.exceptions.*;

/**
 * This is the class that implements the Data Access methods.
 * @author Jon Calvo Gaminde
 */
public class DataAccessImplementation implements DataAccess {
    private static final Logger LOGGER = Logger.getLogger("retoLogin.control.DataAccessImplementation");
    private DataAccessPool dataAccessPool;
    private Connection connection;
    private PreparedStatement stmt;

    public DataAccessImplementation(DataAccessPool dataAccessPool) {
        this.dataAccessPool = dataAccessPool;
    }
    
    @Override
    public void connect() throws SQLException {
        LOGGER.info("DAO connected.");
        connection = dataAccessPool.getConnection();
    }
    
    @Override
    public void  disconnect() throws SQLException {
        LOGGER.info("DAO disconnected.");
        dataAccessPool.liberateConnection(connection);
        if(stmt != null){
            stmt.close();
        }
    }
    
    @Override
    public User validateUser(User loginData) throws ClassNotFoundException, SQLException, IOException, BadLoginException, BadPasswordException {
        LOGGER.info("Login started.");
        User user = null;
        try {
            this.connect();
            String sql = "SELECT * FROM user WHERE login = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, loginData.getLogin());
            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                if (loginData.getPassword().equals(result.getString("password"))) {
                    LOGGER.info("User found, getting data...");
                    user = new User();
                    user.setId(result.getInt("id"));
                    user.setLogin(result.getString("login"));
                    user.setEmail(result.getString("email"));
                    user.setFullName(result.getString("fullName"));
                    user.setStatusString(result.getString("status"));
                    user.setPrivilegeString(result.getString("privilege"));
                    user.setPassword(result.getString("password"));
                    user.setLastAccess(Timestamp.from(Instant.now()));
                    user.setLastPasswordChange(result.getTimestamp("lastPasswordChange"));
                    sql = "UPDATE user SET lastAccess = ? WHERE login = ?";
                    stmt = connection.prepareStatement(sql);
                    stmt.setTimestamp(1, Timestamp.from(Instant.now()));
                    stmt.setString(2, loginData.getLogin());
                    stmt.executeUpdate();
                } else {
                    LOGGER.severe("The password is wrong.");
                    throw new BadPasswordException("Wrong password");
                }
            } else {
                LOGGER.severe("The login is wrong.");
                throw new BadLoginException("Wrong login");
            }
                
        } finally {
            this.disconnect();
	}
        LOGGER.severe("User returned.");
        return user;
    }

    @Override
    public void insertUser(User signupData) throws ClassNotFoundException, SQLException, IOException, AlreadyExistsException {
        LOGGER.info("Register started.");
        try {
            this.connect();
            String sql = "SELECT * FROM user WHERE login = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, signupData.getLogin());
            ResultSet result = stmt.executeQuery();
            if(!result.next()) {
                LOGGER.info("Inserting the user...");
                sql = "INSERT INTO user(login, email, fullName, status, privilege, password, lastAccess, lastPasswordChange) values (?, ?, ?, ?, ?, ?, ?, ?)";
                stmt = connection.prepareStatement(sql);
                stmt.setString(1, signupData.getLogin());
                stmt.setString(2, signupData.getEmail());
                stmt.setString(3, signupData.getFullName());
                stmt.setInt(4, 1);
                stmt.setInt(5, 1);
                stmt.setString(6, signupData.getPassword());
                stmt.setTimestamp(7, Timestamp.from(Instant.now()));
                stmt.setTimestamp(8, Timestamp.from(Instant.now()));
                stmt.executeUpdate();
            } else {
                LOGGER.severe("The user already exists in the database.");
                throw new AlreadyExistsException("Already exists");
            }
        } finally {
            this.disconnect();
        }
        LOGGER.info("User inserted.");
    }
}
    
