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
import java.util.logging.Logger;
import retoLogin.control.DataAccess;
import retoLogin.control.DataAccessFactory;
import retoLogin.exceptions.AlreadyExistsException;
import retoLogin.exceptions.BadLoginException;
import retoLogin.exceptions.BadPasswordException;

/**
 *
 * @author Unai Pérez Sánchez
 */
public class ServerThread extends Thread{
    private Logger LOGGER = Logger.getLogger("retoLogin.ServerThread");
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private DataAccess dao = DataAccessFactory.getDataAccess();
    private Message messageToSend = new Message();
    private User userToSend;
    private int typeToSend;
    private int threadStatus;

    public ServerThread(Socket socket, int threadStatus) {
        this.socket = socket;
        this.threadStatus = threadStatus;
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
        if(threadStatus==0){
            try {
                message = (Message) input.readObject();
                type = message.getType();
                user = message.getUser();

                switch(type){
                    case 1:
                        LOGGER.info("Thread is going to check the login info");
                        userToSend = dao.validateUser(user);
                        LOGGER.info("The database opperation has been completed");
                        LOGGER.info("Database User: "+userToSend.getEmail()+" "+userToSend.getFullName());
                        messageToSend.setUser(userToSend);
                        break;
                    case 2:
                        LOGGER.info("Thread is going to insert into the database the new user");
                        dao.insertUser(user);
                        LOGGER.info("The database opperation has been completed");
                        break;
                    case 3:
                        Thread.sleep(5000000);
                }
                typeToSend = 0;
            } catch (BadLoginException | AlreadyExistsException e){
            LOGGER.severe("Error: "+e.getLocalizedMessage());
            typeToSend = 3;
            } catch (BadPasswordException e){
            LOGGER.severe("Error: "+e.getLocalizedMessage());
            typeToSend = 4;
            } catch (Exception e){
            LOGGER.severe("Error: "+e.getLocalizedMessage());
            typeToSend = 1;  
            } finally {
                try {

                    messageToSend.setType(typeToSend);
                    output.writeObject(messageToSend);
                } catch (IOException e) {
                    LOGGER.severe("Socket error: "+e.getLocalizedMessage());
                } finally {
                    disconnect();
                    Server.threadDisconnected();
                    LOGGER.info("Connection finnished");

                }
            }
        }else{
            try {
                input.readObject();
                messageToSend.setType(2);
                output.writeObject(messageToSend);
                LOGGER.info("Thread is sending the exception");
            } catch (Exception e) {
                LOGGER.severe("Error: "+e.getLocalizedMessage());
            }finally{
                disconnect();
            }
        }
        
    }
}
