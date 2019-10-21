/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package retoLogin.control;

/**
 * The factory of the Data Access
 * @author Jon
 */
public class DataAccessFactory {
    private static DataAccess dataAccess;

	public static DataAccess getDataAccess() {
		if (dataAccess == null) {
			dataAccess = new DataAccessImplementation();
		}
		return dataAccess;
	}
}
