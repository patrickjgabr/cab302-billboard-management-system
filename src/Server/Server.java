package Server;

import Server.ClientHandler.ClientHandlerMessage;
import Server.ClientHandler.MessageHandler;
import Server.Database.BillboardDatabase;
import Server.Database.Database;
import Server.Database.ScheduleDatabase;
import Shared.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import static Shared.ScheduleHelper.GenerateEvents;

/**
 *The Server class provides a main server functionality that provides the main framework for handling clients. This class provides one public method (run) which when called runes the server infinitely until the thread
 * that the Server instance is running on terminates or the ServerSocket running on the client throws and exception and fails to restart.
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
    private ArrayList<Scheduled> scheduled;
    public boolean runServer;

    /**
     * Default constructor used to instantiate a Sever Object
     */
    public Server() {
        try {
            //Assigns the value of properties to a new Properties object which contains all of the information the server
            //  requires to be run
            this.properties = new Properties();

            //Creates a new database connection and checks its configuration. If the database is improperly configured the
            //  server will clear all of the current tables and reproduce a default copy
            Database db = new Database(properties);
            db.startConnection();
            runServer = db.checkConfiguration();
            db.closeConnection();

            //Creates a new thread pool which are used to handle client requests
            this.threads = Executors.newFixedThreadPool(50);

            //Generates the current schedule from the database
            scheduled = generateScheduled();
            schedule = generateSchedule(scheduled);

            try {
                //Attempts to setup the ServerSocket on the port specified by the properties object and initialises the socket
                // variable to be null for future use
                serverSocket = new ServerSocket(Integer.parseInt(properties.getServerPort()));
                socket = null;
                runServer = true;
            } catch (IOException e) {
                //If the ServerSocket fails to start on the specified port then this error message is printed to the console
                System.out.println("Server failed to start on port:" + properties.getServerPort());
                runServer = false;
            }
        } catch (Throwable e) {
            //If the properties file fails to be read than this error message is printed to the console. Instructs the user to ensure
            //  that the Properties.txt is setup in the specified location with the correct name
            System.out.println("Properties file failed to be read. Please ensure file named \"Properties.txt\" is in \"externalResources\" folder");
            runServer = false;
        }
    }

    /**
     * Method which when called continuously accepts clients, with each client being assigned to a new thread.
     */
    public void run() {
        //Sets initial client id to be 100. These ID's are printed in the console messages to allow users to debug more easily
        int clientID = 100;

        //Sets the futureStateList to an ArrayList of empty Objects
        futureStateList = new ArrayList<>();
        IntStream.rangeClosed(0, 49).forEach(x -> futureStateList.add(new Object()));

        //Run server is set to true initially and while remain true unless the ServerSocket throws an exception and fails to restart
        while(runServer) {

            //Checks the state of the futureStateList to see if any of the client threads edits the schedule.
            //  If an edit occurs then true is assigned to updateRequired
            //  If an edit doesn't occur then false is assigned to updateRequired
            boolean updateRequired = checkFutureState();

            //If an edit occurs on any of the client threads than the schedule is updated to reflect the most recent information
            if(updateRequired) {
                scheduled = generateScheduled();
                schedule = generateSchedule(scheduled);
            }

            try {
                //serverSocket.accept() blocks until a new connection is made
                socket = serverSocket.accept();

                //Sets the output and input streams based off the new connection accepted by the socket
                setOutputStream();
                setInputStream();

                //Assigns the client to a new thread
                createFutureState(clientID);
            }

            //Prints any errors which occur during the above process and attempts to close the socket and restart it
            catch(Exception e) {
                //Closes and attempts to restart the serverSocket
                try {
                    socket.close();
                    serverSocket = new ServerSocket(Integer.parseInt(properties.getServerPort()));
                    setInputStream();
                    setOutputStream();

                //If an error occurs while attempting to restart the serverSocket then the server stops
                } catch (IOException ex) {
                    runServer = false;
                }
            }
                //Iterate clientID
                clientID += 1;
            }

            //Once the server finishes running then the thread pool is force shutdown
            threads.shutdown();
    }

    //Function which is used to generate Event array based upon the schedule
    private ArrayList<Event> generateSchedule(ArrayList<Scheduled> scheduled) {
        return GenerateEvents(scheduled);
    }

    //Function which gets the current schedule out of the database
    private ArrayList<Scheduled> generateScheduled() {

        //Instantiates a new ClientMessageHandler, ScheduleDatabase and ArrayList of type Schedule
        //  All of which are used to get the current schedule and print status messages to the console.
        ClientHandlerMessage clientHandlerMessage = new ClientHandlerMessage();
        ScheduleDatabase scheduleDatabase = new ScheduleDatabase(properties);
        ArrayList<Scheduled> returnArray = new ArrayList<>();

        //Attempts to select all of the schedule objects in the database
        try {
            returnArray = scheduleDatabase.getSchedule();
            clientHandlerMessage.printGeneral("SERVER", "Updating Scheduled", 75);

        //If the scheduleDatabase.getSchedule() throws an exception than an error message is printed
        } catch (Throwable ignored) {
            clientHandlerMessage.printWarning("Failed to update schedule!", 75);
        }

        //Returns either an empty ArrayList or one populated with Schedule objects from the database
        return returnArray;
    }

    //This function is used to check the state of the Future objects assigned to each thread when they are started
    //  Threads either return true (If the schedule is edited) or false (Schedule is not edited) to its assigned Future object when it terminates
    private boolean checkFutureState() {

        //Sets updateRequired to false, with its value only changing if a thread edits the schedule
        boolean updateRequired = false;

        //Iterates over each of the objects in the futureStateList and checks its status
        for (int x = 0; x<futureStateList.size(); x++) {
            try {

                //Check the type of the object. If the element if not of type Future than the thread is not being used
                if(futureStateList.get(x) instanceof Future) {

                    //Casts the element to a usable Future object
                    Future<Boolean> futureObject = (Future<Boolean>) futureStateList.get(x);

                    //isDone() returns true if the thread has terminated and therefore is used to check the state of currently running threads
                    if(futureObject.isDone()) {

                        //Sets the value of updateRequired to true if the thread edited the schedule
                        if(futureObject.get()) {
                            updateRequired = futureObject.get();
                        }

                        //Sets the element in the futureStateList back to a blank object and therefore allows the thread to be reused
                        futureStateList.set(futureStateList.indexOf(futureObject), new Object());
                    }
                }

            } catch (Exception ignored) {}
        }

        //Returns true if the schedule is required to be updated by the sever
        return updateRequired;
    }

    //This function is used to create a new thread and assign a Future object to it within the futureStateList
    private void createFutureState(Integer ID) {

        //Iterates through the futureList and check the type of each of the values
        //  If the element is an instance of Future an a thread running on that value
        //  If the element is not an instance of Future than it can be used to create a new client thread.
        for (int x = 0; x< futureStateList.size(); x++) {
            if (!(futureStateList.get(x) instanceof Future)) {
                futureStateList.set(x, createNewThread(ID));
                break;
            }
        }
    }

    //This function creates the thread and assigns it to a Future<Boolean> which is returned
    private Future<Boolean> createNewThread(Integer ID) {
        return threads.submit(addClient(ID));
    }

    //Function which returns a Callable<Boolean> which is Overridden to contain the code required to handle clients
    private Callable<Boolean> addClient(Integer ID) {

        //Effectively the code that runs on the client threads when they are created
        return () -> {
            //Prints a message to the console indicating that a new client handler has been opened.
            ClientHandlerMessage clientHandlerMessage = new ClientHandlerMessage();
            clientHandlerMessage.clientHandlerStart(ID);

            //Return true if edits are made to the schedule
            boolean scheduleEdited = false;

            try {
                //Receives a message of type Message from the inputStream and gives that message to
                //  a new MessageHandler object. This returns the appropriate message. This message
                //  is then written to the outputStream and sent back to the client.
                Message receivedMessage = (Message) inputStream.readObject();
                Message returnMessage;

                //Checks to see if the requesting client is a Viewer (CommID 50) or ControlPanel (CommID ALL but 50)
                if (receivedMessage.getCommunicationID() == 50) {

                    //Gets the return message (Which will either be a billboard or nothing) from the handleClientViewer method
                    returnMessage = handleClientViewer();
                } else {

                    //Get the return message from the handleClientControlPanel
                    returnMessage = handleClientControlPanel(receivedMessage);
                }

                //Creates an ArrayList of known CommunicationIDs that effect the schedule
                ArrayList<Integer> scheduleEffectingID = new ArrayList<>(Arrays.asList(41, 42, 23, 33, 43));

                //If the sent ID matches one from the above ArrayList and the return status of the message is 200 (No error occurred) than the schedule needs to be updated
                if (scheduleEffectingID.contains(receivedMessage.getCommunicationID()) && returnMessage.getCommunicationID() == 200) {
                    scheduleEdited = true;
                }

                //Writes the return message to the output stream
                outputStream.writeObject(returnMessage);

                //Prints error messages if anything happens while the inputStream is reading and subsequently sets a return message with the errorID of 509
            } catch (IOException | ClassNotFoundException e) {
                Message notValidMessage = new Message();
                notValidMessage.setCommunicationID(509);
                outputStream.writeObject(notValidMessage);
            }

            //Prints a message to the console indicating that the new client handler has been closed
            clientHandlerMessage.clientHandlerClose(ID);

            //Returns true if the schedule has been edited and false if the schedule hasn't been edited
            return scheduleEdited;
        };
    }

    //Function which handles ControlPanel clients
    private Message handleClientControlPanel(Message receivedMessage) {

        //Creates a new MessageHandler which returns a Message object based on the input
        MessageHandler messageHandler = new MessageHandler(receivedMessage, properties);
        return messageHandler.getReturnMessage();
    }

    //Function which handles Viewer clients
    private Message handleClientViewer() {

        Message message = new Message();
        Scheduled matchedSchedule = null;

        //Gets the current time in minutes (Between 0 and 1440)
        Calendar currentTime = Calendar.getInstance();
        int minutes = (currentTime.get(Calendar.MINUTE) + currentTime.get(Calendar.HOUR_OF_DAY) * 60);

        //Iterates through each of the scheduled events and determines if they are scheduled to view in the current time
        for (Event event: schedule) {

            //If the current element if scheduled to display on the current day and has a start and end time that are less than and greater than the current time
            if(event.getDay() == currentTime.get(Calendar.DAY_OF_WEEK) && event.getStartTime() <= minutes && event.getEndTime() >= minutes) {

                //If no event currently matches than the schedule object related to the matching is saved to matchedSchedule
                if(matchedSchedule == null) {
                    matchedSchedule = getScheduledFromEvent(event);

                //If matchedSchedule currently has a match it uses the ID to determine which match has preference
                } else {
                    if(matchedSchedule.getID() < event.getEventID()) {
                        matchedSchedule = getScheduledFromEvent(event);
                    }
                }
            }
        }

        //If something is scheduled to be viewed than the billboard must be retrieved from the database and set as the return messages data
        if(matchedSchedule != null) {
            BillboardDatabase billboardDatabase = new BillboardDatabase(properties);

            //Tries to get the billboard corresponding to the matched schedule from the database
            try {
                Billboard billboard = billboardDatabase.getBillboard(matchedSchedule.getBillboardID().toString(), true);

                //Billboard is successful retried from the database and set as the returnMessages data with commID 200 (Success)
                message.setData(billboard);
                message.setCommunicationID(200);

            //If a database error occurs than the commID is set to 500 (Fail)
            } catch (Throwable throwable) {
                message.setCommunicationID(500);
            }

        //If nothing is scheduled to view at the current time than the commID is set to 201
        } else {
            message.setCommunicationID(201);
        }

        //Return message is returned
        return message;
    }

    //Function which takes an event and returns the corresponding Schedule object which its ID references
    private Scheduled getScheduledFromEvent(Event event) {
        Scheduled matchedScheduled = new Scheduled();

        //Iterates through schedule and compares the event ID and schedule elements ID's
        for (Scheduled scheduled: scheduled) {
            if(scheduled.getID() == event.getEventID()) {
                matchedScheduled = scheduled;
                break;
            }
        }
        return  matchedScheduled;
    }

     //Function which setups up the outputstream based off the socket which is established with a client.
    private void setOutputStream() {
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        }

        //Prints any errors which occur during the above process.
        //  These errors should only occur while the code is in production.
        catch (IOException errorMessage) { }
    }

    //Function which setups up the inputstream based off the socket which is established with a client.
    private void setInputStream() {
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
        }

        //Prints any errors which occur during the above process.
        //  These errors should only occur while the code is in production.
        catch (IOException errorMessage) { }
    }
}
