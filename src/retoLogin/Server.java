/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package retoLogin;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import retoLogin.control.DataAccessFactory;
import retoLogin.exceptions.*;

/**
 *
 * @author Unai Pérez Sánchez
 */
public class Server {
    private static final int PORT = 5001;
    private static final int MAX_NUM_THRD = 3;
    private static int NUM_THRD_ACT = 0;
    private static Logger LOGGER =  Logger.getLogger("retoLogin.Server");
    
    
    public static void main(String[] args) {
        ResourceBundle properties = ResourceBundle.getBundle("retoLogin.dbserver");
        String url = "jdbc:mysql://" + properties.getString("dbHost") + "/" + properties.getString("dbName") + "?serverTimezone=Europe/Madrid";
        DataAccessFactory.getDataAccessPool(url, properties.getString("dbUser"), properties.getString("dbPassword"));
        ServerSocket serverSocket;
        LOGGER.info("The server is starting...");
        try {
            serverSocket = new ServerSocket(PORT);
            LOGGER.info("Server started on the port "+PORT);
            while(true){
                Socket socket;
                socket = serverSocket.accept();
                Server.setActiveThread(socket);
            }
        } catch (IOException e) {
            LOGGER.severe("Error: "+e.getLocalizedMessage());
        } catch (Exception e){
            LOGGER.severe("Error: "+e.getLocalizedMessage());
        }
    }
    
    public static synchronized void setActiveThread(Socket socket) {
        NUM_THRD_ACT++;
        Message message = new Message();
        if(NUM_THRD_ACT>MAX_NUM_THRD){
            try {
                throw new NoThreadAvailableException("No more threads available");
            } catch (NoThreadAvailableException e) {
                LOGGER.severe("Error: no more threads available");
                try {
                    ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                    input.readObject();
                    message.setType(2);
                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                    output.writeObject(message);
                    NUM_THRD_ACT--;
                    socket.close();
                } catch (IOException | ClassNotFoundException ex1) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }else{
            LOGGER.info("Threads Active: "+NUM_THRD_ACT);
            LOGGER.warning("New connection inbound: "+socket);
            ((ServerThread) new ServerThread(socket)).start();
        }
    }
    
    public static synchronized void threadDisconnected(){
        NUM_THRD_ACT--;
        LOGGER.info("Threads active: "+NUM_THRD_ACT);
    }
}
