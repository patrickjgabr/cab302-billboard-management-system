package Server;

import Shared.Properties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket serverSocket;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private Properties properties;
    public boolean runServer = true;

    public Server() {
        try {
            this.properties = new Properties();
            try {
                serverSocket = new ServerSocket(Integer.parseInt(properties.getServerPort()));
                socket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Properties file failed to be read. Please ensure file named \"Properties.txt\" is in \"externalResources\" folder");
        }


    }

    public void run() {
        while(runServer) {
            try {
                socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getLocalAddress().toString());
                setOutputStream();
                setInputStream();

                Thread thread = new ClientHandler(socket, inputStream, outputStream, this.properties);
                thread.start();
            }
            catch(Exception e) {
                try {
                    socket.close();
                    e.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }

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
