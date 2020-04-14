package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import Shared.*;

public class ClientHandler extends Thread {

    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    public ClientHandler(Socket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
        Message receivedMessage;

        System.out.println("Client handler socket open...");

        try {
            receivedMessage = (Message)inputStream.readObject();

            MessageHandler messageHandler = new MessageHandler(receivedMessage);
            Message returnMessage = messageHandler.getReturnMessage();

            outputStream.writeObject(receivedMessage);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Client handler socket closed...");
    }

    private void handleEchoMessage(Message received ) {
        try {
            outputStream.writeObject(received);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}