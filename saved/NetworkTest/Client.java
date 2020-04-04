import java.io.*;
import java.net.Socket;

public class Client {
    private Socket socket;
    private ObjectOutputStream outStream;
    private String serverAddress;
    private Integer serverPort;

    public Client(String address, int port) {
        serverAddress = address;
        serverPort = port;
        setSocket();
        setOutputStream();
    }

    public void sendMessage(CommunicationPacket message) {
        try {
            outStream.writeObject(message);
        } catch (IOException errorMessage) {
            System.out.println(errorMessage);
        }

        try {
            outStream.close();
            socket.close();
        } catch (IOException errorMessage){
            System.out.println(errorMessage);
        }
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
            outStream = new ObjectOutputStream(socket.getOutputStream());
        }
        //NEED TO HANDLE THIS EXCEPTION
        catch (IOException errorMessage) {
            System.out.println(errorMessage.getMessage());
        }
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
        setSocket();
        setOutputStream();
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
        setSocket();
        setOutputStream();
    }
}
