/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package retoLogin.control;

import java.io.IOException;
import java.sql.SQLException;
import retoLogin.User;
import retoLogin.exceptions.*;

/**
 * This is the interface of the Data Access methods.
 * @author Jon Calvo Gaminde
 */

public interface DataAccess {
    
    /**
     * Gets a connection from the pool.
     * @throws SQLException DB Exception
     */
    public void connect() throws SQLException;
    
    /**
     * Releases the connection, to be added in the pool.
     * @throws SQLException DB Exception
     */
    public void disconnect () throws SQLException;
    
    /**
     * Checks if there is a user with that login and password, and returns the User data.
     * @param loginData A User with the login and password.
     * @return A User with all the data.
     * @throws ClassNotFoundException DB Exception
     * @throws SQLException DB Exception
     * @throws IOException DB Exception
     * @throws BadLoginException The user with that login is not in the DB.
     * @throws BadPasswordException That password is not correct for that login.
     */
    public User validateUser (User loginData) throws ClassNotFoundException, SQLException, IOException, BadLoginException, BadPasswordException;
    
    /**
     * Inserts a new user in the DB.
     * @param signupData A User with the data of the new user.
     * @throws ClassNotFoundException DB Exception
     * @throws SQLException DB Exception
     * @throws IOException DB Exception
     * @throws AlreadyExistsException The login of the new user already exists in the DB.
     */
    public void insertUser (User signupData) throws ClassNotFoundException, SQLException, IOException, AlreadyExistsException;
}
