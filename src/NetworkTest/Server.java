import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.TreeMap;

public class Server {

    private ServerSocket server;
    private Socket socket;
    private Integer serverPort;
    private boolean serverStatus;

    public Server(Integer port) {
        serverPort = port;
    }

    public CommunicationPacket receiveMessages() {
        CommunicationPacket message = new CommunicationPacket();

        try {
            socket = server.accept();
            System.out.println("Client Connected...");
            ObjectInputStream inputObject = new ObjectInputStream(socket.getInputStream());
            message = (CommunicationPacket) inputObject.readObject();
        }
        //NEED TO HANDLE THIS EXCEPTION
        catch (IOException errorMessage) {
            errorMessage.printStackTrace();
        }
        catch (ClassNotFoundException errorMessage) {
            System.out.println(errorMessage.getMessage());
        }

        return message;
    }

    public void startServer() {
        setServer();
        serverStatus = true;
        System.out.println("Server Started...");
    }

    public void closeServer() {
        try {
            socket.close();
            serverStatus = false;
        }
        catch (IOException errorMessage) {
            System.out.println(errorMessage);
        }
    }

    private void setServer() {
        try {
            server = new ServerSocket(serverPort);
        }
        catch (IOException errorMessage) {
            System.out.println(errorMessage);
        }
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        if (serverStatus) {
            closeServer();
            this.serverPort = serverPort;
            setServer();
            startServer();
        }
        else {
            this.serverPort = serverPort;
            setServer();
        }
    }

    public boolean getServerStatus() {
        return serverStatus;
    }
}
