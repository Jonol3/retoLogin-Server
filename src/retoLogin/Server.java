/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package retoLogin;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Logger;

/**
 *
 * @author Unai Pérez Sánchez
 */
public class Server {
    private final int PORT = 5001;
    private Logger LOGGER =  Logger.getLogger("retoLogin.Server");
    public void iniciar() throws ClassNotFoundException{
        ServerSocket server = null;
        Socket client = null;
        ObjectInputStream input = null;
        ObjectOutputStream output = null;
        try{
            server = new ServerSocket(PORT);
            LOGGER.info("Waiting for a client to connect");
            client = server.accept();
            LOGGER.info("Client connected");
            
            input = new ObjectInputStream(client.getInputStream());
            output = new ObjectOutputStream(client.getOutputStream());
            Message message = (Message) input.readObject();
            User user = message.getUser();
            int number = message.getType();
            String messageOut = "User: "+"\n"+user.getFullName()+"\n"+user.getLogin()+
                    "\n"+user.getEmail()+"\n"+user.getPassword()+"\n"+"Type: "+number;
            LOGGER.info("Message get: \n"+messageOut);
            output.writeObject(messageOut);
        }catch(IOException | ClassNotFoundException e){
            LOGGER.severe("Error: "+e.getLocalizedMessage());
        }catch(Exception e){
            LOGGER.severe("Error: "+e.getLocalizedMessage());
        }finally{
            try{
                if(server != null){
                    server.close();
                }
                if(client != null){
                    client.close();
                }
                if(input != null){
                    input.close();
                }
                if(output != null){
                    output.close();
                }
            }catch(IOException e){
                LOGGER.severe("Error: "+e.getLocalizedMessage());
            }
        }
    }
}
