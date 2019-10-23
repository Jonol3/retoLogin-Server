/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package retoLogin.control;

/**
 * The factory of the Data Access and the Data Access Pool.
 * @author Jon
 */
public class DataAccessFactory {
    private static DataAccessPool dataAccessPool;

	public static DataAccessPool getDataAccessPool(String url, String user, String password) {
            if (dataAccessPool == null) {
                dataAccessPool = new DataAccessPool(url, user, password);
            }
            return dataAccessPool;
	}
        public static DataAccess getDataAccess() {
            return new DataAccessImplementation(dataAccessPool);
	}
}
