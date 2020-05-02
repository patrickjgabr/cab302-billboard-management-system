package Server.ClientHandler;
import Server.Database.BillboardDatabase;
import Server.Database.SessionDatabase;
import Server.Database.UserDatabase;
import Server.Server;
import Shared.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;

/**
 * MessageHandler class handles all of the Message packet received by the ClientHandler. This class contains
 * one main method, getReturnMessage() and a suite of accessory private methods, which handles each communicationID
 * the message packet my contain. Each private method handles one communicationID.
 */

public class MessageHandler {
    
    private Message sentMessage;
    private Message returnMessage;
    private Properties properties;

    /**
     * Method which instantiates a MessageHandler object based off two inputs.
     * @param sentMessage Message object received by the ClientHandler thread containing the clients request.
     * @param properties Properties object containing all the client, server and database information.
     */

    public MessageHandler(Message sentMessage, Properties properties) {
        this.sentMessage = sentMessage;
        this.returnMessage = new Message(this.sentMessage.getSession());
        this.properties = properties;
    }

    /**
     * Main method which gets the appropriate return message based off the specific communicatedID of the received
     * Message Object from the client. Each communicationID class a different handle function.
     * @return Message Object containing data requested by the Client.
     */
    
    public Message getReturnMessage() {
        //Prints a message to the console indicating that a new message handler is opened.
        System.out.println("Message Handler opened... ");
        SessionDatabase sessionDatabase = new SessionDatabase(properties);

        //Group of if statements which directs the class to return a specific Message object based off the communicationID
        if (sentMessage.getCommunicationID() == 10) {
            handleUserLogin();
        } else if (sentMessage.getCommunicationID() == 11) {
            handleUserLogout();
        } else if (sentMessage.getCommunicationID() == 20 && sessionDatabase.checkSession(sentMessage.getSession())) {
            handleGetBillboards();
        } else if (sentMessage.getCommunicationID() == 21 && sessionDatabase.checkSession(sentMessage.getSession())) {
            handleAddBillboard();
        } else if (sentMessage.getCommunicationID() == 22 && sessionDatabase.checkSession(sentMessage.getSession())) {
            handleUpdateBillboard();
        } else if (sentMessage.getCommunicationID() == 30 && sessionDatabase.checkSession(sentMessage.getSession())) {
            handleGetUsers();
        } else if (sentMessage.getCommunicationID() == 31 && sessionDatabase.checkSession(sentMessage.getSession())) {
            handleCreateUser();
        } else if (sentMessage.getCommunicationID() == 32 && sessionDatabase.checkSession(sentMessage.getSession())) {
            handleUpdateUser();
        } else {
            returnMessage.setData(500);
        }

        //Prints a message to the console indicating that the message handler object is closed.
        System.out.println("Message Handler closed...");
        return  returnMessage;
    }

    /**
     * Method which handles a Message object containing communicationID 10. This indicates that the client is requesting a single
     * user from the database.
     */
    private void handleUserLogin() {
        try {
            //Instantiates a new UserDatabase object connecting to the database specified by the Properties Object.
            //  Uses the getUser method to get a User Object from the database.

            String[] loginDetails = (String[]) sentMessage.getData();
            System.out.println(checkCredentials(loginDetails));
            if(checkCredentials(loginDetails)) {
                SessionDatabase sessionDatabase = new SessionDatabase(properties);
                String token = sessionDatabase.setSession(loginDetails[0]);
                returnMessage.setSession(token);

                UserDatabase userDatabase = new UserDatabase(properties);
                User user = userDatabase.getUser(loginDetails[0]);
                returnMessage.setData(user.getPermission());

            } else {
                returnMessage.setData(500);
            }

            //NEED TO ADD PUTTING TOKEN INTO DATABASE
        } catch (Throwable throwable) {
            //Sets the return data to 500 if the Select is unsuccessful.
            returnMessage.setData(500);
        }
    }

    private void handleUserLogout() {
        try {
            String token = (String) sentMessage.getData();
            SessionDatabase sessionDatabase = new SessionDatabase(properties);

            if (sessionDatabase.removeSession(token)) {
                returnMessage.setData(200);
            } else {
                returnMessage.setData(500);
            }

        } catch (Throwable throwable) {
            //Sets the return data to 500 if the Select is unsuccessful.
            returnMessage.setData(500);
        }
    }

    private boolean checkCredentials(String[] loginDetails) {
        UserDatabase userDB = new UserDatabase(properties);
        try {
            User user = userDB.getUser(loginDetails[0]);
            System.out.println(user.getUserID());
            System.out.println(user.getUserPassword());
            MessageDigest passwordHash = null;
            try {
                String hashed = "jeff";
                passwordHash = MessageDigest.getInstance("SHA-256");

                byte [] byteArray = passwordHash.digest((user.getSalt() + loginDetails[0]).getBytes());

                StringBuilder sb = new StringBuilder();
                for (int i=0; i< byteArray.length; i++){
                    sb.append(Integer.toString(byteArray[i]));
                    sb.append(Integer.toString((byteArray[i] & 0xff) + 0x100, 16).substring(1));
                }
                hashed = sb.toString();
                System.out.println(hashed);
                if (user.getUserPassword().equals(hashed)){
                    return true;
                }
                else return false;
            } catch (NoSuchAlgorithmException e) {return false;}

        } catch (Throwable throwable) {
            return false;
        }
    }

    /**
     * Method which handles a Message object containing communicationID 32. This indicates that the user table needs to
     * be updated using the User object contained in the Message object.
     */
    private void handleUpdateUser() {
        try {
            //Instantiates a new UserDatabase object connecting to the database specified by the Properties Object.
            //  Uses the updateDatabase method to update the database using the information contained in the given User Object.
            UserDatabase userDB = new UserDatabase(properties);
            userDB.updateDatabase((User)sentMessage.getData());

            //Sets return data to 200 if the Update is successful.
            returnMessage.setData(200);

        } catch (Throwable throwable) {
            //Sets return data to 500 if the Update is unsuccessful.
            returnMessage.setData(500);
        }
    }

    /**
     * Method which handles a Message object containing communicationID 31. This indicates that the User Object contained within
     * the Message packet needs to be added to the users and permissions table.
     */
    private void handleCreateUser() {
        try {
            //Instantiates a new UserDatabase object connecting to the database specified by the Properties Object.
            // Uses the addToDatabase method to add the information contained in the User Object to the users and permissions table.
            UserDatabase userDB = new UserDatabase(properties);
            userDB.addToDatabase((User)sentMessage.getData());

            //Sets return data to 200 if the Add is successful.
            returnMessage.setData(200);
        } catch (Throwable throwable) {

            //Sets return data to 500 if the Add is unsuccessful.
            returnMessage.setData(500);
        }
    }

    /**
     * Method which handles a Message object containing communicationID 30. This indicates that the client is requesting all
     * the users within the database.
     */
    private void handleGetUsers() {
        try {
            //Instantiates a new UserDatabase object connecting to the database specified by the Properties Object.
            //  Uses the getUsers method to get an ArrayList<User> from the database.
            UserDatabase userDB = new UserDatabase(properties);
            ArrayList<User> requestedUsers = userDB.getUsers();

            //Sets return data to the ArrayList<User> returned by the database.
            returnMessage.setData(requestedUsers);
        } catch (Throwable throwable) {
            //Sets the return data to 500 if the Select is unsuccessful.
            returnMessage.setData(500);
        }
    }

    /**
     * Method which handles a Message object containing communicationID 22. This indicates that the billboards table needs to
     * be updated using the Billboard object contained in the Message object.
     */
    private void handleUpdateBillboard() {
        try {
            //Instantiates a new BillboardDatabase object connecting to the database specified by the Properties Object.
            //  Uses the updateDatabase method to update database using the information contained within the Message object.
            BillboardDatabase billboardDB = new BillboardDatabase(properties);
            billboardDB.updateDatabase((Billboard)sentMessage.getData());

            //Sets the return data to 200 if the update is successful
            returnMessage.setData(200);
        } catch (Throwable throwable) {

            //Sets the return data to 500 if the update is unsuccessful
            returnMessage.setData(500);
        }
    }

    /**
     * Method which handles a Message object containing communicationID 21. This indicates that the Billboard Object contained within
     * the Message packet needs to be added to the billboard database.
     */
    private void handleAddBillboard() {
        try {
            //Instantiates a new BillboardDatabase object connecting to the database specified by the Properties Object.
            //  Uses the addToDatabase method to add the information contained within the Message object to the database.
            BillboardDatabase billboardDB = new BillboardDatabase(properties);
            billboardDB.addToDatabase((Billboard)sentMessage.getData());

            //Sets the return data to 200 if the add is successful
            returnMessage.setData(200);
        } catch (Throwable throwable) {

            //Sets the return data to 500 if the add is unsuccessful
            returnMessage.setData(500);
        }
    }

    /**
     * Method which handles a Message object containing communicationID 20. This indicates that the client is requesting all the
     * billboards from the billboard database.
     */
    private void handleGetBillboards() {
        try {
            //Instantiates a new BillboardDatabase object connecting to the database specified by the Properties Object.
            //  Uses the getBillboards method to get a ArrayList<Billboard> from the database.
            BillboardDatabase billboardDB = new BillboardDatabase(properties);
            ArrayList<Billboard> requestedBillboards = billboardDB.getBillboards();

            //Sets the return data to the Arraylist<Billboard> returned from the database.
            returnMessage.setData(requestedBillboards);
        } catch (Throwable throwable) {

            //Sets the return data to 500 if the select is unsuccessful
            returnMessage.setData(500);
        }
    }
}
