package ControlPanel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import Shared.*;

public class Client {

    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private Properties properties;

    public Client() {

        try {
            properties = new Properties();

        } catch (Throwable e) {
            System.out.println("Properties file failed to be read. Please ensure file named \"Properties.txt\" is in \"externalResources\" folder");
        }

    }

    public Message sendMessage(Message message) {
        setSocket();
        setOutputStream();
        setInputStream();
        Message returnMessage = new Message();

        try {
            outputStream.writeObject(message);
            returnMessage = (Message)inputStream.readObject();
        } catch (IOException | ClassNotFoundException ignored) { }

        return returnMessage;
    }

    private void setSocket() {
        try{
            socket = new Socket(properties.getServerAddress(), Integer.parseInt(properties.getServerPort()));
        }
        catch (IOException ignored){ }
    }

    private void setOutputStream() {
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (IOException ignored) { }
    }

    private void setInputStream() {
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
        }
        catch (IOException ignored) { }
    }
}
