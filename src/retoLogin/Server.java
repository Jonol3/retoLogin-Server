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
    private static int port;
    private static int maxNumThrd;
    private static int numThrdAct = 0;
    private static Logger LOGGER =  Logger.getLogger("retoLogin.Server");
    
    
    public static void main(String[] args) {
        ResourceBundle properties = ResourceBundle.getBundle("retoLogin.dbserver");
        String url = "jdbc:mysql://" + properties.getString("dbHost") + "/" + properties.getString("dbName") + "?serverTimezone=Europe/Madrid";
        DataAccessFactory.getDataAccessPool(url, properties.getString("dbUser"), properties.getString("dbPassword"));
        port = Integer.parseInt(properties.getString("serverPort"));
        maxNumThrd = Integer.parseInt(properties.getString("maxConnections"));
        ServerSocket serverSocket;
        LOGGER.info("The server is starting...");
        try {
            serverSocket = new ServerSocket(port);
            LOGGER.info("Server started on the port "+ port);
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
        numThrdAct++;
        if(numThrdAct>maxNumThrd){
            try {
                throw new NoThreadAvailableException("No more threads available");
            } catch (NoThreadAvailableException e) {
                LOGGER.severe("Error: no more threads available");
//                    ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
//                    input.readObject();
//                    message.setType(2);
//                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
//                    output.writeObject(message);
                ((ServerThread) new ServerThread(socket,1)).start();
                numThrdAct--;
//                    socket.close();
            }
        }else{
            LOGGER.info("Threads Active: "+numThrdAct);
            LOGGER.warning("New connection inbound: "+socket);
            ((ServerThread) new ServerThread(socket,0)).start();
        }
    }
    
    public static synchronized void threadDisconnected(){
        numThrdAct--;
        LOGGER.info("Threads active: "+numThrdAct);
    }
}
