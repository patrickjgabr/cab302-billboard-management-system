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
    public boolean runServer = true;

    public Server() {
        try {
            serverSocket = new ServerSocket(8080);
            socket = null;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while(runServer) {
            try {
                socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getLocalAddress().toString());
                setOutputStream();
                setInputStream();

                Thread thread = new ClientHandler(socket, inputStream, outputStream);
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
