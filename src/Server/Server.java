package Server;

import Server.ClientHandler.ClientHandler;
import Server.Database.Database;
import Shared.Properties;

import javax.xml.crypto.Data;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *Server class handles all of the server side components. This class runs constantly on the server machine.
 * Consists of two methods which setup the server input and output streams based off the socket initialised when instantiated.
 * Also contains a run method which runs the server continuously.
 */

public class Server {

    private ServerSocket serverSocket;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private Properties properties;
    public boolean runServer = true;

    /**
     * Method which instantiates a Server object from no inputs.
     */
    public Server() {
        try {
            //Properties object contains all of the information the server needs to run and connect to the database.
            this.properties = new Properties();

            //Database object is created to check that the database specified in the properties object contains the
            // tables required for the server to handle the client requests. If these tables don't exist they are created.
            Database database = new Database(properties);
            database.checkConfiguration();
            try {
                //Attempts to setup the ServerSocket on the port specified by the properties object and initialises the socket
                // variable to be null for further use.
                serverSocket = new ServerSocket(Integer.parseInt(properties.getServerPort()));
                socket = null;
            } catch (IOException e) {
                //If the ServerSocket fails to start on the specified port then this error message is printed to the console.
                System.out.println("Server failed to start on port:" + properties.getServerPort());
            }
        } catch (FileNotFoundException e) {
            //If the properties file fails to be read than this error message is printed to the console. Instructs the user to ensure
            //  that the Properties.txt is setup in the specified location with the correct name
            System.out.println("Properties file failed to be read. Please ensure file named \"Properties.txt\" is in \"externalResources\" folder");
        }
    }

    /**
     * Method which runs the Server object continuously reading new connections from the serverSocket. Each connection is given a new thread
     * (ClientHandler) each time the serverSocket accepts a new connection.
     */
    public void run() {
        //Run server is set to true initially and while it remains true the server will accept new connections
        while(runServer) {
            Database db = new Database(properties);
            db.startConnection();
            if(!db.checkConfiguration()) {
                db.closeConnection();
                break;
            }
            db.closeConnection();

            try {
                //serverSocket.accept() blocks until a new connection is made.
                // A message is written to the console when a new connection is started.
                socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getLocalAddress().toString());

                //Sets the output and input streams based off the new connection accepted by the socket
                setOutputStream();
                setInputStream();

                //Instantiates and starts a new ClientHandler thread and passes the socket, output and input streams
                // to the ClientHandler object so it can continue to communicate with the connection.
                // Start method runs the ClientHandler thread using its overloaded run() method.
                Thread thread = new ClientHandler(socket, inputStream, outputStream, this.properties);
                thread.start();
            }

            //Prints any errors which occur during the above process and attempts to close the socket.
            //  These errors should only occur while the code is in production.
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

    /**
     * Method which setups up the outputstream based off the socket which is established with a client.
     */
    private void setOutputStream() {
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        }

        //Prints any errors which occur during the above process.
        //  These errors should only occur while the code is in production.
        catch (IOException errorMessage) {
            errorMessage.printStackTrace();
        }
    }

    /**
     * Method which setups up the inputstream based off the socket which is established with a client.
     */
    private void setInputStream() {
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
        }

        //Prints any errors which occur during the above process.
        //  These errors should only occur while the code is in production.
        catch (IOException errorMessage) {
            errorMessage.printStackTrace();
        }
    }
}
