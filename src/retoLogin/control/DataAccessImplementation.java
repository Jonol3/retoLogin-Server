/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package retoLogin.control;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void insertUser(User signupData) throws ClassNotFoundException, SQLException, IOException, AlreadyExistsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
