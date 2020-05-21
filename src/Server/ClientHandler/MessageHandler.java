package Server.ClientHandler;
import Server.ConsoleMessage.ConsoleMessage;
import Server.ConsoleMessage.DatabaseMessage;
import Server.Database.BillboardDatabase;
import Server.Database.ScheduleDatabase;
import Server.Database.SessionDatabase;
import Server.Database.UserDatabase;
import Server.Server;
import Shared.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
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
    private DatabaseMessage consoleMessage;

    /**
     * Method which instantiates a MessageHandler object based off two inputs.
     * @param sentMessage Message object received by the ClientHandler thread containing the clients request.
     * @param properties Properties object containing all the client, server and database information.
     */

    public MessageHandler(Message sentMessage, Properties properties) {
        this.sentMessage = sentMessage;
        this.returnMessage = new Message(this.sentMessage.getSession());
        consoleMessage = new DatabaseMessage();
        this.properties = properties;
    }

    /**
     * Main method which gets the appropriate return message based off the specific communicatedID of the received
     * Message Object from the client. Each communicationID class a different handle function.
     * @return Message Object containing data requested by the Client.
     */
    
    public Message getReturnMessage() {
        //Prints a message to the console indicating that a new message handler is opened.

        ClientHandlerMessage clientHandlerMessage = new ClientHandlerMessage();
        clientHandlerMessage.messageHandlerStart(sentMessage.getCommunicationID(), sentMessage.getSession());

        SessionDatabase sessionDatabase = new SessionDatabase(properties);

        //Group of if statements which directs the class to return a specific Message object based off the communicationID
        if (sentMessage.getCommunicationID() == 10) {
            handleUserLogin();
        } else if (sentMessage.getCommunicationID() == 11) {
            handleUserLogout();
        } else if(sessionDatabase.checkSession(sentMessage.getSession())) {

            User user = sessionDatabase.getUserFromSession(sentMessage.getSession());

            if (sentMessage.getCommunicationID() == 20 ) {
                handleGetBillboards();
            } else if (sentMessage.getCommunicationID() == 21) {
                if(user.getPermission().get(0) == 1) {
                    handleAddBillboard(user);
                } else {
                    consoleMessage.printWarning("User not authorised to add Billboards", 75);
                    returnMessage.setCommunicationID(504);
                }
            } else if (sentMessage.getCommunicationID() == 22) {
                handleUpdateBillboard(user);
            } else if (sentMessage.getCommunicationID() == 30) {
                handleGetUsers();
            } else if (sentMessage.getCommunicationID() == 31) {
                if(user.getPermission().get(3) == 1) {
                    handleCreateUser();
                } else {
                    consoleMessage.printWarning("User not authorised to add User", 75);
                    returnMessage.setCommunicationID(504);
                }
            } else if (sentMessage.getCommunicationID() == 32) {
                if (user.getPermission().get(3) == 1) {
                    handleUpdateUser();
                } else {
                    consoleMessage.printWarning("User not authorised to update User", 75);
                    returnMessage.setCommunicationID(504);
                }
            } else if(sentMessage.getCommunicationID() == 40) {
                handleRequestSchedule();
            } else if(sentMessage.getCommunicationID() == 41) {
                if(user.getPermission().get(2) == 1) {
                    handleAddToSchedule(user.getUserID());
                } else {
                    consoleMessage.printWarning("User not authorised to add to schedule", 75);
                    returnMessage.setCommunicationID(504);
                }

            } else {
                consoleMessage.printWarning("Invalid communicationID", 75);
                returnMessage.setCommunicationID(501);
            }
        } else {
            returnMessage.setCommunicationID(503);
            consoleMessage.printWarning("Invalid session", 75);
        }

        //Prints a message to the console indicating that the message handler object is closed.
        clientHandlerMessage.messageHandlerClose(returnMessage.getCommunicationID());
        return  returnMessage;
    }

    private boolean checkPermissions(String token) {
        SessionDatabase sessionDatabase = new SessionDatabase(properties);
        User user = sessionDatabase.getUserFromSession(token);

        boolean returnValue = true;

        if(sentMessage.getCommunicationID() == 21 && user.getPermission().get(0) == 0) {
            returnValue = false;
        } else if (sentMessage.getCommunicationID() == 41 && user.getPermission().get(2) == 0) {
            returnValue = false;
        } else if (sentMessage.getCommunicationID() == 31 && user.getPermission().get(3) == 0) {
            returnValue = false;
        }

        return returnValue;
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
            if(checkCredentials(loginDetails)) {
                SessionDatabase sessionDatabase = new SessionDatabase(properties);
                String token = sessionDatabase.setSession(loginDetails[0]);
                returnMessage.setSession(token);

                UserDatabase userDatabase = new UserDatabase(properties);
                User user = userDatabase.getUser(loginDetails[0], true);
                returnMessage.setData(user.getPermission());
                returnMessage.setCommunicationID(200);
                consoleMessage.printGeneral("REQUEST ACCEPTED", "Login successful for username [" + loginDetails[0] + "]", 75);
            } else {
                returnMessage.setCommunicationID(502);
                consoleMessage.printWarning("Login Request Rejected for username [" + loginDetails[0] + "]                           Reason: Invalid Credentials", 75);
            }

        } catch (Throwable throwable) {
            //Sets the return data to 500 if the Select is unsuccessful.
            consoleMessage.printWarning("Login Request Rejected     |     Reason: DATABASE ERROR", 75);
            returnMessage.setCommunicationID(500);
        }
    }

    private void handleUserLogout() {
        String token = (String) sentMessage.getData();
        SessionDatabase sessionDatabase = new SessionDatabase(properties);

        if (sessionDatabase.removeSession(token, true)) {
            returnMessage.setCommunicationID(200);
            consoleMessage.printGeneral("REQUEST ACCEPTED", "Logout Successful for token", 75);
        } else {
            returnMessage.setCommunicationID(503);
            consoleMessage.printWarning("Logout Request Rejected  Reason: Invalid Token", 75);
        }
    }

    private boolean checkCredentials(String[] loginDetails) {
        UserDatabase userDB = new UserDatabase(properties);
        try {
            User user = userDB.getUser(loginDetails[0], true);
            MessageDigest passwordHash;
            try {
                passwordHash = MessageDigest.getInstance("SHA-256");
                passwordHash.update((user.getSalt() + loginDetails[1]).getBytes());
                byte [] byteArray = passwordHash.digest();

                StringBuilder sb = new StringBuilder();
                for (byte b : byteArray) {
                    sb.append(String.format("%02x", b & 0xFF));
                }
                String hashed = sb.toString();
                return user.getUserPassword().equals(hashed);
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
            User user = (User) sentMessage.getData();
            user.setUserPassword(generateNewPassword(user));
            userDB.updateDatabase((User)sentMessage.getData());

            //Sets return data to 200 if the Update is successful.
            returnMessage.setCommunicationID(200);
            consoleMessage.printGeneral("REQUEST ACCEPTED", "User updated   |   userID [" + ((User) sentMessage.getData()).getUserID() + "]", 75);

        } catch (Throwable throwable) {
            //Sets return data to 500 if the Update is unsuccessful.
            consoleMessage.printWarning("Database failed to update user",75);
            returnMessage.setCommunicationID(500);
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
            User user = (User)sentMessage.getData();
            user.setSalt(generateSalt());
            user.setUserPassword(generateNewPassword(user));

            if(!user.getUserPassword().equals("")) {
                try {
                    userDB.addToDatabase(user);
                    //Sets return data to 200 if the Add is successful.
                    returnMessage.setCommunicationID(200);
                    consoleMessage.printGeneral("REQUEST ACCEPTED", "User add   |   userID [" + ((User) sentMessage.getData()).getUserID() + "]", 75);
                } catch (Throwable throwable) {
                    //Sets return data to 500 if the Add is unsuccessful.
                    returnMessage.setCommunicationID(500);
                    consoleMessage.printWarning("Database failed to add user",75);
                }

            } else {
                //Sets return data to 508 if the Add is unsuccessful due to password being null.
                returnMessage.setCommunicationID(508);
                consoleMessage.printWarning("Database failed to add user",75);
            }


        } catch (Throwable throwable) {

            //Sets return data to 500 if the Add is unsuccessful.
            returnMessage.setCommunicationID(500);
            consoleMessage.printWarning("Database failed to add user",75);
        }
    }

    private String generateSalt() {

        StringBuilder tokenBuilder = new StringBuilder(64);
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz!@#$%^&*()_+-={}[]:<>?,./";

        for (int i = 0; i < 64; i++) {
            int index = (int)(characters.length()* Math.random());
            tokenBuilder.append(characters.charAt(index));
        }

        return tokenBuilder.toString();
    }

    private String generateNewPassword(User user) {
        try {
            MessageDigest passwordHash = MessageDigest.getInstance("SHA-256");
            passwordHash.update((user.getSalt() + user.getUserPassword()).getBytes());
            byte [] byteArray = passwordHash.digest();

            StringBuilder sb = new StringBuilder();
            for (byte b : byteArray) {
                sb.append(String.format("%02x", b & 0xFF));
            }
            String hashed = sb.toString();
            return hashed;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "";
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
            returnMessage.setCommunicationID(200);
            consoleMessage.printGeneral("REQUEST ACCEPTED", "All users selected", 75);
        } catch (Throwable throwable) {
            //Sets the return data to 500 if the Select is unsuccessful.
            returnMessage.setCommunicationID(500);
            consoleMessage.printWarning("Database failed to select all users",75);
        }
    }

    /**
     * Method which handles a Message object containing communicationID 22. This indicates that the billboards table needs to
     * be updated using the Billboard object contained in the Message object.
     */
    private void handleUpdateBillboard(User user) {

        try {
            if(checkEditPermission(user)) {
                //Instantiates a new BillboardDatabase object connecting to the database specified by the Properties Object.
                //  Uses the updateDatabase method to update database using the information contained within the Message object.
                BillboardDatabase billboardDB = new BillboardDatabase(properties);
                billboardDB.updateDatabase((Billboard)sentMessage.getData());

                //Sets the return data to 200 if the update is successful
                returnMessage.setCommunicationID(200);
                consoleMessage.printGeneral("REQUEST ACCEPTED", "Billboard updated   |   billboardID [" + ((Billboard) sentMessage.getData()).getBillboardID() + "]", 75);
            } else {
                consoleMessage.printWarning("User not authorised to update billboard [" + ((Billboard)sentMessage.getData()).getBillboardID() +"]", 75);
                returnMessage.setCommunicationID(504);
            }
        } catch (Throwable throwable) {

            //Sets the return data to 500 if the update is unsuccessful
            returnMessage.setCommunicationID(500);
            consoleMessage.printWarning("Database failed to update billboard",75);
        }
    }

    private boolean checkEditPermission(User user) throws Throwable {
        BillboardDatabase billboardDatabase = new BillboardDatabase(properties);
        Billboard billboard = (Billboard)sentMessage.getData();
        billboardDatabase.getBillboard(billboard.getBillboardID().toString(), true);

        if(user.getPermission().get(1) == 1) {
            return true;
        } else {
            if(billboard.getScheduled() == 0 && billboard.getCreatorName() == user.getUserName()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Method which handles a Message object containing communicationID 21. This indicates that the Billboard Object contained within
     * the Message packet needs to be added to the billboard database.
     */
    private void handleAddBillboard(User user) {
        try {
            Billboard billboard = (Billboard)sentMessage.getData();
            if(user.getUserName().equals(billboard.getCreatorName())) {
                //Instantiates a new BillboardDatabase object connecting to the database specified by the Properties Object.
                //  Uses the addToDatabase method to add the information contained within the Message object to the database.
                BillboardDatabase billboardDB = new BillboardDatabase(properties);
                boolean unique = billboardDB.addToDatabase(billboard, user.getUserID());

                if(unique) {
                    //Sets the return data to 200 if the add is successful
                    returnMessage.setCommunicationID(200);
                    consoleMessage.printGeneral("REQUEST ACCEPTED", "Billboard Added   |   billboardID [" + ((Billboard) sentMessage.getData()).getBillboardID() + "]", 75);
                } else  {
                    //Sets the return data to 506 if billboard name already exists
                    returnMessage.setCommunicationID(506);
                    consoleMessage.printWarning("Billboard name not unique",75);
                }
            } else {
                //Sets the return data to 505 if the add is unsuccessful due to non matching session and creator
                returnMessage.setCommunicationID(505);
                consoleMessage.printWarning("Billboard creator doesn't match token",75);
            }
        } catch (Throwable throwable) {

            //Sets the return data to 500 if the add is unsuccessful
            returnMessage.setCommunicationID(500);
            consoleMessage.printWarning("Database failed to add billboard",75);
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
            returnMessage.setCommunicationID(200);
            consoleMessage.printGeneral("REQUEST ACCEPTED", "All billboards selected", 75);
        } catch (Throwable throwable) {

            //Sets the return data to 500 if the select is unsuccessful
            returnMessage.setCommunicationID(500);
            consoleMessage.printWarning("Database failed to select billboards",75);
        }
    }

    private void handleRequestSchedule() {
        try {
            ScheduleDatabase scheduleDatabase = new ScheduleDatabase(properties);
            ArrayList<Scheduled> scheduled = scheduleDatabase.getSchedule();
            returnMessage.setData(scheduled);
            returnMessage.setCommunicationID(200);
            consoleMessage.printGeneral("REQUEST ACCEPTED", "Schedule selected", 75);
        } catch (Throwable throwable) {

            //Sets the return data to 500 if the select is unsuccessful
            returnMessage.setCommunicationID(500);
            consoleMessage.printWarning("Database failed to select schedule",75);
        }
    }

    private void handleAddToSchedule(Integer userID) {
        try {
            Scheduled scheduled = (Scheduled)sentMessage.getData();
            if(userID == scheduled.getCreatorID()) {
                ScheduleDatabase scheduleDatabase = new ScheduleDatabase(properties);
                boolean success = scheduleDatabase.addToDatabase(scheduled, userID);
                if(success) {
                    //Sets the return data to 200 if the add is successful
                    returnMessage.setCommunicationID(200);
                    consoleMessage.printGeneral("REQUEST ACCEPTED", "Billboard Added   |   billboardID [" + ((Billboard) sentMessage.getData()).getBillboardID() + "]", 75);
                } else  {
                    //Sets the return data to 506 if billboard name already exists
                    returnMessage.setCommunicationID(500);
                    consoleMessage.printWarning("Database failed to select billboard",75);
                }
            } else {
                //Sets the return data to 505 if the add is unsuccessful due to non matching session and creator
                returnMessage.setCommunicationID(507);
                consoleMessage.printWarning("Schedule creator doesn't match token",75);
            }

        } catch (Throwable throwable) {

            //Sets the return data to 500 if the select is unsuccessful
            returnMessage.setCommunicationID(500);
            consoleMessage.printWarning("Database failed to select billboard",75);
        }
    }
}
