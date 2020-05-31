package Shared;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import Shared.*;

/**
 * The Client class provides server requesting Clients a standardised way of sending and receiving Message requests to the Server.
 * This class utilises a Socket configured to Servers address and port, read from "Properties.txt", which writes and receives Message Objects using a
 * ObjectOutputStream and ObjectInputStream class. Client has one main method, sendMessage, to write and return a given Message object.
 */
public class Client {

    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private Properties properties;

    /**
     * Default constructor used to instantiate a Client Object
     */
    public Client() {

        try {
            properties = new Properties();

        } catch (Throwable e) {
            System.out.println("Properties file failed to be read. Please ensure file named \"Properties.txt\" is in \"externalResources\" folder");
        }

    }

    /**
     * Method which writes the given Message Object to the Socket configured to communicate with the Server, and returns the Message Object returned from the Server.
     * @param message Message Object to be sent to the Server
     * @return Message Object returned from the Server
     */
    public Message sendMessage(Message message) {

        //Sets the Socket and ObjectStreams
        setSocket();
        setOutputStream();
        setInputStream();

        //Instantiates a new Message Object
        Message returnMessage = new Message();

        //Attempts to write the given Message Object to the outputStream and receive return Message Object
        try {

            //Writes the given Message Object to the outputStream
            outputStream.writeObject(message);

            //Assigns the value of returnMessage to the Object returned from the inputStream
            returnMessage = (Message)inputStream.readObject();
        } catch (IOException | ClassNotFoundException ignored) { }

        //Return received Message Object
        return returnMessage;
    }

    //Function which sets the value of socket to the server address and port specified in the Properties.txt file
    private void setSocket() {
        try{
            socket = new Socket(properties.getServerAddress(), Integer.parseInt(properties.getServerPort()));
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    //Function which sets the value of the outputStream to use the new Socket
    private void setOutputStream() {
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Function which sets the value of the inputStream to use the new Socket
    private void setInputStream() {
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
