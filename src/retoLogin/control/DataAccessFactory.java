/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package retoLogin.control;

/**
 * The factory of the Data Access and the Data Access Pool.
 * @author Jon Calvo Gaminde
 */
public class DataAccessFactory {
    private static DataAccessPool dataAccessPool;
    
    /**
     * Creates a new DataAccessPool, unless it already exists, and returns it.
     * @param url The URL of the Database
     * @param user The user of the Database
     * @param password The password of the user
     * @return The DataAccessPool object.
     */
    public static DataAccessPool getDataAccessPool(String url, String user, String password) {
        if (dataAccessPool == null) {
            dataAccessPool = new DataAccessPool(url, user, password);
        }
        return dataAccessPool;
    }
    
    /**
     * Creates a new DataAccess Object and returns it.
     * @return The new DataAccess object.
     */
    public static DataAccess getDataAccess() {
        return new DataAccessImplementation(dataAccessPool);
    }
}
