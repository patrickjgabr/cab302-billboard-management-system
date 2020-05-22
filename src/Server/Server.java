package Server;

import Server.ClientHandler.ClientHandler;
import Server.ClientHandler.ClientHandlerMessage;
import Server.ClientHandler.MessageHandler;
import Server.Database.Database;
import Server.Database.ScheduleDatabase;
import Shared.Event;
import Shared.Message;
import Shared.Properties;
import Shared.Scheduled;

import javax.xml.crypto.Data;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import static Shared.ScheduleHelper.GenerateEvents;

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
    private ExecutorService threads;
    private ArrayList<Object> futureStateList;
    private ArrayList<Event> schedule;
    public boolean runServer = true;

    /**
     * Method which instantiates a Server object from no inputs.
     */
    public Server() {
        try {
            //Properties object contains all of the information the server needs to run and connect to the database.
            this.properties = new Properties();

            Database db = new Database(properties);
            db.startConnection();
            runServer = db.checkConfiguration();
            db.closeConnection();

            this.threads = Executors.newFixedThreadPool(50);

            schedule = generateSchedule();
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
        Integer clientID = 100;

        futureStateList = new ArrayList<>();
        IntStream.rangeClosed(0, 49).forEach(x -> {
            futureStateList.add(new Object());
        });

        while(runServer) {

            boolean updateRequired = checkFutureState();

            if(updateRequired) {
                this.schedule = generateSchedule();
            }

            try {
                //serverSocket.accept() blocks until a new connection is made.
                // A message is written to the console when a new connection is started.
                socket = serverSocket.accept();

                //Sets the output and input streams based off the new connection accepted by the socket
                setOutputStream();
                setInputStream();

                createFutureState(clientID);
            }

            //Prints any errors which occur during the above process and attempts to close the socket.
            //  These errors should only occur while the code is in production.
            catch(Exception e) {
                try {
                    socket.close();
                } catch (IOException ex) {}
            }

                //Iterate clientID
                clientID += 1;
            }

            threads.shutdown();
    }

    private ArrayList<Event> generateSchedule() {
        ClientHandlerMessage clientHandlerMessage = new ClientHandlerMessage();
        ScheduleDatabase scheduleDatabase = new ScheduleDatabase(properties);
        ArrayList<Event> returnArray = new ArrayList<>();
        try {
            clientHandlerMessage.printGeneral("SERVER", "Updating Schedule", 75);
            ArrayList<Scheduled> scheduled = scheduleDatabase.getSchedule();
            returnArray = GenerateEvents(scheduled);
        } catch (Throwable throwable) {}

        return returnArray;
    }


    private boolean checkFutureState() {
        boolean updateRequired = false;

        for (int x = 0; x<futureStateList.size(); x++) {
            try {
                if(futureStateList.get(x) instanceof Future) {
                    Future<Boolean> futureObject = (Future<Boolean>) futureStateList.get(x);
                    if(futureObject.isDone()) {
                        updateRequired = futureObject.get();
                        futureStateList.set(futureStateList.indexOf(futureObject), new Object());
                    }
                }
            } catch (Exception e) {}
        }

        return updateRequired;
    }

    private void createFutureState(Integer ID) {
        for (int x = 0; x< futureStateList.size(); x++) {
            if (!(futureStateList.get(x) instanceof Future)) {
                futureStateList.set(x, createNewThread(ID));
                break;
            }
        }
    }

    private Future<Boolean> createNewThread(Integer ID) {
        Future<Boolean> future = threads.submit(addClient(ID));
        return  future;
    }

    private Callable<Boolean> addClient(Integer ID) {

        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                //Prints a message to the console indicating that a new client handler has been opened.
                ClientHandlerMessage clientHandlerMessage = new ClientHandlerMessage();
                clientHandlerMessage.clientHandlerStart(ID);

                //Return true if edits are made to the schedule
                boolean scheduleEdited = false;

                try {
                    //Receives a message of type Message from the inputStream and gives that message to
                    //  a new MessageHandler object. This returns the appropriate message. This message
                    //  is then written to the outputStream and sent back to the client.
                    Message receivedMessage = (Message)inputStream.readObject();
                    MessageHandler messageHandler = new MessageHandler(receivedMessage, properties);
                    Message returnMessage = messageHandler.getReturnMessage();

                    if((receivedMessage.getCommunicationID() == 41 || receivedMessage.getCommunicationID() == 42) && returnMessage.getCommunicationID() == 200) {
                        scheduleEdited = true;
                    }

                    outputStream.writeObject(returnMessage);

                    //Prints error messages is anything happens to inputStream reading and outputStream witting.
                } catch (IOException | ClassNotFoundException e) {
                    Message notValidMessage = new Message();
                    notValidMessage.setCommunicationID(509);
                    outputStream.writeObject(notValidMessage);
                }

                //Prints a message to the console indicating that the new client handler has been closed
                clientHandlerMessage.clientHandlerClose(ID);

                return scheduleEdited;
            }
        };
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
