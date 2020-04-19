package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import Shared.*;

/**
 * ClientHandler class extends the Thread class and is used to handle a each Clients connection to the server.
 * Has one method run() which is called by thread when the start method is called.
 */
public class ClientHandler extends Thread {

    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private Properties properties;

    /**
     * Method which instantiates a ClientHandler object from four inputs.
     * @param socket Socket containing connection accepted by the ServerSocket.
     * @param inputStream ObjectInputStream created by the server which will read information from the client.
     * @param outputStream ObjectOutputStream created by the server which will be used to send information from the server to the client.
     * @param properties Properties object which contains all of the client, server and database information.
     */
    public ClientHandler(Socket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream, Properties properties) {
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.properties = properties;
    }

    /**
     * Override Method which is run() when thread.start() is called by the server.
     */
    @Override
    public void run() {
        //Prints a message to the console indicating that a new client handler has been opened.
        System.out.println("Client handler socket open...");

        try {
            //Receives a message of type Message from the inputStream and gives that message to
            //  a new MessageHandler object. This returns the appropriate message. This message
            //  is then written to the outputStream and sent back to the client.
            Message receivedMessage = (Message)inputStream.readObject();
            MessageHandler messageHandler = new MessageHandler(receivedMessage, properties);
            Message returnMessage = messageHandler.getReturnMessage();
            outputStream.writeObject(returnMessage);

        //Prints error messages is anything happens to inputStream reading and outputStream witting.
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        //Prints a message to the console indicating that the new client handler has been closed
        System.out.println("Client handler socket closed...");
    }
}
