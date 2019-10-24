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
import retoLogin.User;
import retoLogin.exceptions.*;

/**
 * This is the class that implements the Data Access methods.
 * @author Jon
 */
public class DataAccessImplementation implements DataAccess {
    private DataAccessPool dataAccessPool;
    private Connection connection;
    private PreparedStatement stmt;

    public DataAccessImplementation(DataAccessPool dataAccessPool) {
        this.dataAccessPool = dataAccessPool;
    }
    
    @Override
    public void connect() throws SQLException {
        connection = dataAccessPool.getConnection();
    }
    
    @Override
    public void  disconnect() throws SQLException {
        dataAccessPool.liberateConnection(connection);
        if(stmt != null){
            stmt.close();
        }
        if(connection != null){
            connection.close();
        }
    }
    
    @Override
    public User validateUser(User loginData) throws ClassNotFoundException, SQLException, IOException, BadLoginException, BadPasswordException {
        User user = null;
        try {
            this.connect();
            String sql = "SELECT * FROM user WHERE login = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, loginData.getLogin());
            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                if (loginData.getPassword().equals(result.getString("password"))) {
                    user = new User();
                    user.setId(result.getInt("id"));
                    user.setLogin(result.getString("login"));
                    user.setEmail(result.getString("email"));
                    user.setFullName(result.getString("fullName"));
                    user.setStatusString(result.getString("status"));
                    user.setPrivilegeString(result.getString("privilege"));
                    user.setPassword(result.getString("password"));
                    user.setLastAccess(result.getTimestamp("lastAccess"));
                    user.setLastPasswordChange(result.getTimestamp("lastPasswordChange"));
                    sql = "UPDATE user SET lastAccess = ? WHERE login = ?";
                    stmt = connection.prepareStatement(sql);
                    stmt.setTimestamp(1, Timestamp.from(Instant.now()));
                    stmt.setString(2, loginData.getLogin());
                    stmt.executeUpdate();
                } else {
                    throw new BadPasswordException(null);
                }
            } else {
                throw new BadLoginException(null);
            }
                
        } finally {
            this.disconnect();
	}
        return user;
    }

    @Override
    public void insertUser(User signupData) throws ClassNotFoundException, SQLException, IOException, AlreadyExistsException {
        try {
            this.connect();
            String sql = "INSERT INTO user(login, email, fullName, status, privilege, password, lastAccess, lastPasswordChange) values (?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, signupData.getLogin());
            stmt.setString(2, signupData.getEmail());
            stmt.setString(3, signupData.getFullName());
            stmt.setString(4, signupData.getStatusString());
            stmt.setString(5, signupData.getPrivilegeString());
            stmt.setString(6, signupData.getPassword());
            stmt.setTimestamp(7, signupData.getLastAccess());
            stmt.setTimestamp(8, signupData.getLastPasswordChange());
            stmt.executeUpdate();
        } finally {
            this.disconnect();
        }
    }
    }
    
}
