/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package retoLogin;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import retoLogin.control.DataAccessFactory;
import retoLogin.control.DataAccessPool;

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
        ResourceBundle properties = ResourceBundle.getBundle("retoLogin.dbserver");
        String url = "jdbc:mysql://" + "10.22.82.145:3306" + "/" + "reto1" + "?serverTimezone=Europe/Madrid";
        DataAccessFactory.getDataAccessPool(url, "root", "abcd*1234");
        ServerSocket serverSocket;
        LOGGER.info("The server is starting...");
        try {
            serverSocket = new ServerSocket(PORT);
            LOGGER.info("Server started on the port "+PORT);
            int sessionID = 0;
            while(true){
                Socket socket;
                socket = serverSocket.accept();
                LOGGER.warning("New connection inbound: "+socket);
                ((ServerThread) new ServerThread(socket, sessionID)).start();
                sessionID++;
            }
        } catch (IOException e) {
            LOGGER.severe("Error: "+e.getLocalizedMessage());
        }
    }
    
}
