/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package retoLogin.control;

import java.sql.*;
import java.util.Stack;

/**
 *
 * @author Unai Pérez Sánchez
 */
public class DataAccessPool {
    protected Stack pool;
    protected String connectURL;
    protected String user;
    protected String password;
    /**
     * Creates a pool with the following data to make the connections
     * to the database
     * @param oneConnectURL The database URL we want to connect
     * @param oneUser The user of the database with we are going to connect
     * @param onePassword Password of the user we are going to use to connect to the database
     */
    public DataAccessPool(String oneConnectURL, String oneUser, String onePassword){
        connectURL = oneConnectURL;
        user = oneUser;
        password = onePassword;
        pool = new Stack();
    }
    /**
     * Gets a connection from the pool
     * @return An object Connection to make the connection to the database
     * @throws SQLException If something goes wrong with the operations with the database
     */
    public synchronized Connection getConnection() throws SQLException{
        if(!pool.empty()){
            return (Connection) pool.pop();
        }else{
            return DriverManager.getConnection(connectURL, user, password);
        }
    }
    /**
     * Returns a connection to the pool if who was using it no needs no more
     * @param connection The connection to liberate
     * @throws SQLException If something go wrong this exception will be shown
     */
    public synchronized void liberateConnection(Connection connection) throws SQLException{
        pool.push(connection);
    }
}
