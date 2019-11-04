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
    /**
     * This method is the constructor for the thread
     * @param socket An object of type Socket
     * @param threadStatus integer if is set to 0 means that the server is not bussy if it is 1 the server is bussy and is not going to do anything
     */
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
    /**
     * This method kills the thread
     */
    public void disconnect(){
        try {
            socket.close();
        } catch (IOException e) {
            LOGGER.severe("Error: "+e.getLocalizedMessage());
        }
    }
    /**
     * In this method is going to be the main code of the thread
     */
    @Override
    public void run() {
        Message message;
        User user;
        int type;
        if(threadStatus==0){//If there are threads available
            try {
                message = (Message) input.readObject();
                type = message.getType();
                user = message.getUser();

                switch(type){
                    case 1://Is going to do the login
                        LOGGER.info("Thread is going to check the login info");
                        userToSend = dao.validateUser(user);//Stores the user in a User object if something go wrong is going to be empty
                        LOGGER.info("The database opperation has been completed");
                        if(!userToSend.equals(null)){
                            LOGGER.info("Database User: "+userToSend.getEmail()+" "+userToSend.getFullName());
                        }
                        messageToSend.setUser(userToSend);//We set in the message we are going to send to the client side the user we stored previusly
                        break;
                    case 2://Is going to register a user
                        LOGGER.info("Thread is going to insert into the database the new user");
                        dao.insertUser(user);//We try to insert into the database the new user that tries to register
                        LOGGER.info("The database opperation has been completed");
                        break;
                }
                typeToSend = 0;
            } catch (BadLoginException | AlreadyExistsException e){//If the login doesn't exits, or the user that is trying to register already exits on the database
            LOGGER.severe("Error: "+e.getLocalizedMessage());
            typeToSend = 3;
            } catch (BadPasswordException e){//When the user tryes to login but the password is incorrect
            LOGGER.severe("Error: "+e.getLocalizedMessage());
            typeToSend = 4;
            } catch (Exception e){//Other exceptions that may occur
            LOGGER.severe("Error: "+e.getLocalizedMessage());
            typeToSend = 1;  
            } finally {
                try {

                    messageToSend.setType(typeToSend);//We set the type depending on the result of the operation
                    output.writeObject(messageToSend);//We send the message to the client side
                } catch (IOException e) {//When we send the message may occur an input output exception
                    LOGGER.severe("Socket error: "+e.getLocalizedMessage());
                } finally {
                    disconnect();//We kill the connection with the client
                    Server.threadDisconnected();//Liberate the thread
                    LOGGER.info("Connection finnished");

                }
            }
        }else{//Are not threads available
            try {
                input.readObject();//We read to liberate the pipe
                messageToSend.setType(2);
                output.writeObject(messageToSend);//We send the message to the client side with the code 2 that means that the server is bussy
                LOGGER.info("Thread is sending the exception");
            } catch (Exception e) {//Some exception unexpected may occur
                LOGGER.severe("Error: "+e.getLocalizedMessage());
            }finally{
                disconnect();//Kill the connection
            }
        }
        
    }
}
