/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package retoLogin;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import retoLogin.control.DataAccessFactory;
import retoLogin.exceptions.*;

/**
 *
 * @author Unai Pérez Sánchez, Jon Calvo Gaminde
 */
public class Server {
    private static int port;
    private static int maxNumThrd;
    private static int numThrdAct = 0;
    private static Logger LOGGER =  Logger.getLogger("retoLogin.Server");
    
    
    public static void main(String[] args) {
        //We load the properties file and save it on a variable
        ResourceBundle properties = ResourceBundle.getBundle("retoLogin.dbserver");
        //Set the database connection URL
        String url = "jdbc:mysql://" + properties.getString("dbHost") + ":" 
                + properties.getString("dbPort") + "/" + properties.getString("dbName") 
                + "?serverTimezone=Europe/Madrid";
        //We set the user and the password for the database
        DataAccessFactory.getDataAccessPool(url, properties.getString("dbUser"), properties.getString("dbPassword"));
        port = Integer.parseInt(properties.getString("serverPort"));
        maxNumThrd = Integer.parseInt(properties.getString("maxConnections"));

        ServerSocket serverSocket;
        LOGGER.info("The server is starting...");
        try {
            //Started the server
            serverSocket = new ServerSocket(port);
            LOGGER.info("Server started on the port "+ port);
            //The server will be always listening
            while(true){
                Socket socket;
                //We accept the new connection
                socket = serverSocket.accept();
                //We are going to check if there are threads left
                Server.setActiveThread(socket);
            }
        } catch (Exception e){
            //Some unexcepted exception can happend
            LOGGER.severe("Error: "+e.getLocalizedMessage());
        }
    }
    /**
     * This method checks first if there is any thread available and acts in consequence
     * @param socket An object of type Socket
     */
    public static synchronized void setActiveThread(Socket socket) {
        //New active thread (we increment the counter by 1)
        numThrdAct++;
        if(numThrdAct>maxNumThrd){
            //If there are not more threads available, operation will fail
            LOGGER.severe("Error: no more threads available");
            //We send the message to the client by an extra thread
            ((ServerThread) new ServerThread(socket,1)).start();
            //When we finish we rest to the counter one
            numThrdAct--;
        }else{
            //If there are threads available, the operation will continue
            LOGGER.info("Threads Active: "+numThrdAct);
            LOGGER.warning("New connection inbound: "+socket);
            ((ServerThread) new ServerThread(socket,0)).start();
        }
    }
    /**
     * This method rest to the counter if any of active threads disconnected
     */
    public static synchronized void threadDisconnected(){
        numThrdAct--;
        LOGGER.info("Threads active: "+numThrdAct);
    }
}
