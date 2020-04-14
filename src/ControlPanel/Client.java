package ControlPanel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import Shared.*;

public class Client {

    private String serverAddress;
    private Integer serverPort;
    private Socket socket;
    private ServerSocket serverSocket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    public Client(String serverAddress, Integer serverPort) {

        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        setSocket();
        setOutputStream();
        setInputStream();

    }

    public Message sendMessage(Message message) {
        Message returnMessage = new Message();

        try {
            outputStream.writeObject(message);
            returnMessage = (Message)inputStream.readObject();
        } catch (IOException | ClassNotFoundException errorMessage) {
            System.out.println(errorMessage);
        }

        return returnMessage;
    }

    private void setSocket() {
        try{
            socket = new Socket(serverAddress, serverPort);
        }
        //NEED TO HANDLE THIS EXCEPTION
        catch (IOException errorMessage){
            System.out.println(errorMessage);
        }
    }

    private void setOutputStream() {
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        }
        //NEED TO HANDLE THIS EXCEPTION
        catch (IOException errorMessage) {
            System.out.println(errorMessage.getMessage());
        }
    }

    private void setInputStream() {
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
        }
        //NEED TO HANDLE THIS EXCEPTION
        catch (IOException errorMessage) {
            System.out.println(errorMessage.getMessage());
        }
    }
}
