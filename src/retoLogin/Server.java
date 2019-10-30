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
 * @author Unai Pérez Sánchez
 */
public class Server {
    private static final int PORT = 5001;
    private static final int MAX_NUM_THRD = 10;
    private static int NUM_THRD_ACT = 0;
    private static Logger LOGGER =  Logger.getLogger("retoLogin.Server");
    
    
    public static void main(String[] args) {
        ResourceBundle properties = ResourceBundle.getBundle("retoLogin.dbserver");//We load the properties file and save it on a variable
        String url = "jdbc:mysql://" + properties.getString("dbHost") + "/" + properties.getString("dbName") + "?serverTimezone=Europe/Madrid";//Set the database connection URL
        DataAccessFactory.getDataAccessPool(url, properties.getString("dbUser"), properties.getString("dbPassword"));//We set the user and the password for the database
        ServerSocket serverSocket;
        LOGGER.info("The server is starting...");
        try {
            serverSocket = new ServerSocket(PORT);//Started the server
            LOGGER.info("Server started on the port "+PORT);
            while(true){//The server will be always listening
                Socket socket;
                socket = serverSocket.accept();//We accept the new connection
                Server.setActiveThread(socket);//We are going to check if there are threads left
            }
        } catch (Exception e){//Some unexcepted exception can happend
            LOGGER.severe("Error: "+e.getLocalizedMessage());
        }
    }
    /**
     * This method checks first if there is any thread available and acts in consequence
     * @param socket An object of type Socket
     */
    public static synchronized void setActiveThread(Socket socket) {
        NUM_THRD_ACT++;//New active thread (we increment the counter by 1)
        if(NUM_THRD_ACT>MAX_NUM_THRD){//If there are not more threads available
            try {
                throw new NoThreadAvailableException("No more threads available");//We throw the exception
            } catch (NoThreadAvailableException e) {//And we catch it
                LOGGER.severe("Error: no more threads available");
                ((ServerThread) new ServerThread(socket,1)).start();//We send the message to the client by an extra thread
                NUM_THRD_ACT--;//When we finish we rest to the counter one
            }
        }else{//If there are threads available, the operation will continue
            LOGGER.info("Threads Active: "+NUM_THRD_ACT);
            LOGGER.warning("New connection inbound: "+socket);
            ((ServerThread) new ServerThread(socket,0)).start();
        }
    }
    /**
     * This method rest to the counter if any of active threads disconnected
     */
    public static synchronized void threadDisconnected(){
        NUM_THRD_ACT--;
        LOGGER.info("Threads active: "+NUM_THRD_ACT);
    }
}
