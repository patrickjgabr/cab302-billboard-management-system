package Server;
import Shared.*;

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

        //Group of if statements which directs the class to return a specific Message object based off the communicationID
        if (sentMessage.getCommunicationID() == 10) {
            handleUserLogin();
        } else if (sentMessage.getCommunicationID() == 11) {
            handleUserLogout();
        } else if (sentMessage.getCommunicationID() == 20) {
            handleGetBillboards();
        } else if (sentMessage.getCommunicationID() == 21) {
            handleAddBillboard();
        } else if (sentMessage.getCommunicationID() == 22) {
            handleUpdateBillboard();
        } else if (sentMessage.getCommunicationID() == 30) {
            handleGetUsers();
        } else if (sentMessage.getCommunicationID() == 31) {
            handleCreateUser();
        } else if (sentMessage.getCommunicationID() == 32) {
            handleUpdateUser();
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
            UserDatabase userDB = new UserDatabase(properties);
            User requestedUser = userDB.getUser(false, (String[]) sentMessage.getData());
            String token = generateToken();

            //Sets return data to the User object returned by the database.
            returnMessage.setData(requestedUser.getPermission());
            returnMessage.setSession(token);

            //NEED TO ADD PUTTING TOKEN INTO DATABASE
        } catch (Exception e) {

            //Sets the return data to 500 if the Select is unsuccessful.
            returnMessage.setData(500);
        }
    }

    private void handleUserLogout() {

    }


    private String generateToken() {

        StringBuilder tokenBuilder = new StringBuilder(64);
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz!@#$%^&*()_+-={}[]:<>?,./";

        for (int i = 0; i < 64; i++) {
            int index = (int)(characters.length()* Math.random());
            tokenBuilder.append(characters.charAt(index));
        }

        System.out.println(tokenBuilder.toString());
        return tokenBuilder.toString();
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
        } catch (Exception e) {

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
        } catch (Exception e) {

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
        } catch (Exception e) {

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
        } catch (Exception e) {

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
        } catch (Exception e) {

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
        } catch (Exception e) {

            //Sets the return data to 500 if the select is unsuccessful
            returnMessage.setData(500);
        }
    }
}
