/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package retoLogin;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import retoLogin.control.DataAccess;
import retoLogin.control.DataAccessFactory;

/**
 *
 * @author Unai Pérez Sánchez
 */
public class ServerThread extends Thread{
    private Logger LOGGER = Logger.getLogger("retoLogin.ServerThread");
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private int sessionID;
    private DataAccess dao = DataAccessFactory.getDataAccess();
    private Message messageToSend = new Message();
    private User userToSend;
    private int typeToSend;

    public ServerThread(Socket socket, int sessionID) {
        this.socket = socket;
        this.sessionID = sessionID;
        try {
            input = new ObjectInputStream(socket.getInputStream());
            output = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            LOGGER.severe("Error: "+e.getLocalizedMessage());
        }
    }
    
    public void disconnect(){
        try {
            socket.close();
        } catch (IOException e) {
            LOGGER.severe("Error: "+e.getLocalizedMessage());
        }
    }

    @Override
    public void run() {
        Message message;
        User user;
        int type;
        
        try {
            message = (Message) input.readObject();
            type = message.getType();
            user = message.getUser();
            
            switch(type){
                case 1:
                    LOGGER.info("Thread is going to check the login info");
                    userToSend = dao.validateUser(user);
                    LOGGER.info("Se ha terminado la operacion en la base de datos");
                    LOGGER.info("Database User: "+userToSend.getEmail()+" "+userToSend.getFullName());
                    typeToSend = 0;
                    messageToSend.setType(typeToSend);
                    messageToSend.setUser(userToSend);
                    output.writeObject(messageToSend);
                    break;
                case 2:
                    LOGGER.info("Thread is going to insert into the database the new user");
                    break;
            }
        } catch (IOException e) {
            LOGGER.severe("Error: "+e.getLocalizedMessage());
        } catch (ClassNotFoundException e) {
            LOGGER.severe("Error: "+e.getLocalizedMessage());
        }catch (Exception e){
            LOGGER.severe("Error: "+e.getLocalizedMessage());
        }finally{
            disconnect();
            LOGGER.info("Connection finnished");
        }
        
    }
    
    
    
}
