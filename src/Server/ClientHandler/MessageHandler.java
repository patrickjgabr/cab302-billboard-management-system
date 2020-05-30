package Server.ClientHandler;
import Server.ConsoleMessage.DatabaseMessage;
import Server.Database.BillboardDatabase;
import Server.Database.ScheduleDatabase;
import Server.Database.SessionDatabase;
import Server.Database.UserDatabase;
import Shared.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class MessageHandler {
    
    private Message sentMessage;
    private Message returnMessage;
    private Properties properties;
    private DatabaseMessage consoleMessage;

    /**
     * The MessageHandler class provides all of the Message response functionality for Control Panel requests.
     * This class is designed to take a received Message and preform specific actions and return a response Message.
     * Response Messages can either have a valid response status (CommunicationID 200) indicating a valid request was made or a invalid response status (CommunicationID 500 or greater).
     * @param sentMessage Message object received by sever containing the clients request.
     * @param properties Object containing all of the database connection parameters
     */
    public MessageHandler(Message sentMessage, Properties properties) {
        this.sentMessage = sentMessage;
        this.returnMessage = new Message(this.sentMessage.getSession());
        consoleMessage = new DatabaseMessage();
        this.properties = properties;
    }

    /**
     * Method which gets the appropriate return message dependent on the given Messages communicatedID, session and data.
     * Requests are parsed to a series of handle methods based on their communicationID, however in most cases also require a valid session and user permissions to be processed.
     * @return Message Object containing a return status and data if requested and processed successfully.
     */
    public Message getReturnMessage() {
        //Prints a message to the console indicating that a new message handler is opened.
        ClientHandlerMessage clientHandlerMessage = new ClientHandlerMessage();
        clientHandlerMessage.messageHandlerStart(sentMessage.getCommunicationID(), sentMessage.getSession());

        //Instantiates a new SessionDatabase object
        SessionDatabase sessionDatabase = new SessionDatabase(properties);

        //If communicationID is 10 then handle User login
        if (sentMessage.getCommunicationID() == 10) {
            handleUserLogin();

        //If communicationID is 11 then handle User logout
        } else if (sentMessage.getCommunicationID() == 11) {
            handleUserLogout();

        //If communicationID is not 10 or 11 ensure the request has a valid session
        } else if(sessionDatabase.checkSession(sentMessage.getSession())) {

            //Instantiate a User Object for requesting User that is making the request
            User user = sessionDatabase.getUserFromSession(sentMessage.getSession());

            //If communicationID is 20 handle get Billboards
            if (sentMessage.getCommunicationID() == 20 ) {
                handleGetBillboards();

            //If communicationID is 21 handle add Billboard
            } else if (sentMessage.getCommunicationID() == 21) {

                //If user has add Billboard permission then handle add Billboard
                if(user.getPermission().get(0) == 1) {
                    handleAddBillboard(user);

                //If user does not have add Billboard permission print error message and set return status as 504
                } else {
                    consoleMessage.printWarning("User not authorised to add Billboards", 75);
                    returnMessage.setCommunicationID(504);
                }

            //If communicationID is 22 handle update Billboard
            } else if (sentMessage.getCommunicationID() == 22) {
                handleUpdateBillboard(user);

            //If communicationID is 23 handle remove Billboard
            } else if(sentMessage.getCommunicationID() == 23) {
                handleRemoveBillboard(user);

            //If communicationID is 30 handle get Users
            } else if (sentMessage.getCommunicationID() == 30) {
                handleGetUsers();

            //If communicationID is 31 handle create User
            } else if (sentMessage.getCommunicationID() == 31) {

                //If user has edit user permission then handle create User
                if(user.getPermission().get(3) == 1) {
                    handleCreateUser();

                //If user doesn't have edit user permission print error message and set return status as 504
                } else {
                    consoleMessage.printWarning("User not authorised to add User", 75);
                    returnMessage.setCommunicationID(504);
                }

            //If communicationID is 32 handle update User
            } else if (sentMessage.getCommunicationID() == 32) {

                //If user has edit user permission then handle update User
                if (user.getPermission().get(3) == 1) {
                    handleUpdateUser();

                //If user doesn't have edit user permission print error message and set return status as 504
                } else {
                    consoleMessage.printWarning("User not authorised to update User", 75);
                    returnMessage.setCommunicationID(504);
                }

            //If communicationID is 33 handle remove User
            } else if(sentMessage.getCommunicationID() == 33) {
                handleRemoveUser(user);

            } else if(sentMessage.getCommunicationID() == 34) {
                handleUpdateUserPassword();

            //If communicationID is 40 handle request Scheduled
            } else if(sentMessage.getCommunicationID() == 40) {
                handleRequestSchedule();

            //If communicationID is 41 handle add Schedule
            } else if(sentMessage.getCommunicationID() == 41) {

                //If user has schedule permission than handle add Schedule
                if (user.getPermission().get(2) == 1) {
                    handleAddToSchedule(user.getUserID());

                //If user doesn't have schedule permission than handle print error message and set return status as 504
                } else {
                    consoleMessage.printWarning("User not authorised to add to schedule", 75);
                    returnMessage.setCommunicationID(504);
                }

            //If communicationID is 42 handle update Schedule
            } else if(sentMessage.getCommunicationID() == 42) {
                handleUpdateSchedule(user);

            //If communicationID is 43 handle remove Schedule
            } else if (sentMessage.getCommunicationID() == 43) {
                handleRemoveSchedule(user);

            //If communicationID doesn't match any of the above print error message and set return status as 501
            } else {
                consoleMessage.printWarning("Invalid communicationID", 75);
                returnMessage.setCommunicationID(501);
            }

        //If request doesn't have a valid session print error message and set return status as 503
        } else {
            returnMessage.setCommunicationID(503);
            consoleMessage.printWarning("Invalid session", 75);
        }

        //Prints a message to the console indicating that the message handler object is closed and return returnMessage
        clientHandlerMessage.messageHandlerClose(returnMessage.getCommunicationID());
        return  returnMessage;
    }

    //Function which handles user login requests, returning a token if a valid login request is made
    private void handleUserLogin() {

        //Attempt to authenticate the user and set a valid session token
        try {

            //Get the login details contained in the data property of the received message
            String[] loginDetails = (String[]) sentMessage.getData();

            //If the users credentials are valid then generate a token and return it along with the users permissions and return status 200
            if(checkCredentials(loginDetails)) {

                //Instantiate a SessionDatabase Object and set the session for the given userName (loginDetails[0]) and set it as the session of the return message
                SessionDatabase sessionDatabase = new SessionDatabase(properties);
                String token = sessionDatabase.setSession(loginDetails[0]);
                returnMessage.setSession(token);

                //Instantiate a UserDatabase Object and get the user from the given userName (loginDetails[0]) and set the permissions of the user as the return data with a valid response code 200
                UserDatabase userDatabase = new UserDatabase(properties);
                User user = userDatabase.getUser(loginDetails[0], true);
                returnMessage.setData(user.getPermission());
                returnMessage.setCommunicationID(200);

                //Print success message
                consoleMessage.printGeneral("REQUEST ACCEPTED", "Login successful for username [" + loginDetails[0] + "]", 75);

            //If the users credentials are invalid then set the response code to 502 and print an error message
            } else {
                returnMessage.setCommunicationID(502);
                consoleMessage.printWarning("Login Request Rejected for username [" + loginDetails[0] + "]                           Reason: Invalid Credentials", 75);
            }

        //If the SessionDatabase or UserDatabase throw an exception then print and error message and set the response code to 500
        } catch (Throwable throwable) {
            //Sets the return data to 500 if the Select is unsuccessful.
            consoleMessage.printWarning("Login Request Rejected     |     Reason: DATABASE ERROR", 75);
            returnMessage.setCommunicationID(500);
        }
    }

    //Function which handles user logout request, returning a valid response code if the user was successfully logged out
    private void handleUserLogout() {

        //Gets the token contained in the data property of the received message and instantiate a SessionDatabase object
        String token = (String) sentMessage.getData();
        SessionDatabase sessionDatabase = new SessionDatabase(properties);

        //If the session is successfully removed from the database then a valid response code is returned and success message printed
        if (sessionDatabase.removeSession(token, true)) {
            returnMessage.setCommunicationID(200);
            consoleMessage.printGeneral("REQUEST ACCEPTED", "Logout Successful for token", 75);

        //If the session is unsuccessfully removed from the database then a invalid response code is returned and error message printed
        } else {
            returnMessage.setCommunicationID(503);
            consoleMessage.printWarning("Logout Request Rejected  Reason: Invalid Token", 75);
        }
    }

    //Function which checks the credentials of given loginDetails returning true if the credentials are valid
    private boolean checkCredentials(String[] loginDetails) {

        //Instantiate a UserDatabse Object
        UserDatabase userDB = new UserDatabase(properties);

        //Attempt to get a User from the database that matches the given userName (loginDetails[0]) and check the given password hash with the actual hash
        try {

            //Get the user corresponding to the given userName (loginDetails[0]) and compare the password hashes
            User user = userDB.getUser(loginDetails[0], true);
            MessageDigest passwordHash;
            try {

                //Use a MessageDigest set to "SHA-256" to generate a byteArray of the sent password hash combined with the Users salt
                passwordHash = MessageDigest.getInstance("SHA-256");
                passwordHash.update((user.getSalt() + loginDetails[1]).getBytes());
                byte [] byteArray = passwordHash.digest();

                //Build a StringBuilder containing character output from the MessageDigest
                StringBuilder sb = new StringBuilder();
                for (byte b : byteArray) {
                    sb.append(String.format("%02x", b & 0xFF));
                }

                //Convert the StringBuilder into a String and compare it to the actual Users hashed password
                String hashed = sb.toString();

                //Return true if hashed passwords match
                return user.getUserPassword().equals(hashed);

            //If SHAR-256 algorithm fails return false
            } catch (NoSuchAlgorithmException e) {return false;}

        //If a exception is thrown then return false
        } catch (Throwable throwable) {
            return false;
        }
    }

    //Function which updates User in the database
    private void handleUpdateUser() {
        try {
            //Instantiates a new UserDatabase object connecting to the database specified by the Properties Object.
            UserDatabase userDB = new UserDatabase(properties);

            //Gets the User object from the received message
            User user = (User) sentMessage.getData();

            //Get the current copy of the User from the database
            User currentUser = userDB.getUser(user.getUserName(), true);

            //If the password has not changed set user password to the current copies password
            if(user.getUserPassword().equals("") || user.getUserPassword().equals(" ") || user.getUserPassword() == null) {
                user.setUserPassword(currentUser.getUserPassword());

            //If the password has changed then generate the new password
            } else {
                user.setUserPassword(generateNewPassword(user));
            }

            //Update the currentUser to user
            userDB.updateDatabase(user);

            //Sets return data to 200 if the Update is successful.
            returnMessage.setCommunicationID(200);
            consoleMessage.printGeneral("REQUEST ACCEPTED", "User updated   |   userID [" + ((User) sentMessage.getData()).getUserID() + "]", 75);

        //If a exception is thrown print an error message
        } catch (Throwable throwable) {
            //Sets return data to 500 if the Update is unsuccessful.
            consoleMessage.printWarning("Database failed to update user",75);
            returnMessage.setCommunicationID(500);
        }
    }

    //Function which adds a User to the database
    private void handleCreateUser() {
        try {
            //Instantiates a new UserDatabase object connecting to the database specified by the Properties Object.
            UserDatabase userDB = new UserDatabase(properties);

            //Gets the User data from the received message
            User user = (User)sentMessage.getData();

            //Generates a salt and hashes the given password hash with this value
            user.setSalt(generateSalt());
            user.setUserPassword(generateNewPassword(user));

            //If userPassword doesn't equal nothing than add the user to the database
            if(!user.getUserPassword().equals("")) {

                //Attempt to add the given User to the database
                try {
                    userDB.addToDatabase(user);

                    //Print a success message and set the return status to 200
                    returnMessage.setCommunicationID(200);
                    consoleMessage.printGeneral("REQUEST ACCEPTED", "User add   |   userID [" + ((User) sentMessage.getData()).getUserID() + "]", 75);

                //If a exception is thrown print an error message
                } catch (Throwable throwable) {

                    //Print a error message and set the return status to 500
                    returnMessage.setCommunicationID(500);
                    consoleMessage.printWarning("Database failed to add user",75);
                }

            //
            } else {

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

    private void handleUpdateUserPassword() {
        try {
            String[] changePassword = (String[])sentMessage.getData();

            UserDatabase userDatabase = new UserDatabase(properties);
            User user = userDatabase.getUser(changePassword[0], true);

            if(user.getUserID() != null) {
                user.setUserPassword(changePassword[1]);
                user.setUserPassword(generateNewPassword(user));

                userDatabase.updateDatabase(user);
                returnMessage.setCommunicationID(200);
                consoleMessage.printGeneral("REQUEST ACCEPTED", "User password updated", 75);
            } else {
                //Sets return data to 500 if the Update is unsuccessful.
                consoleMessage.printWarning("Database failed to find User to update",75);
                returnMessage.setCommunicationID(518);
            }
        } catch (Throwable throwable) {
            //Sets return data to 500 if the Update is unsuccessful.
            consoleMessage.printWarning("Database failed to update users password",75);
            returnMessage.setCommunicationID(500);
        }
    }

    private void handleRemoveUser(User user) {
        try {
            UserDatabase userDatabase = new UserDatabase(properties);
            User sentUser = (User)sentMessage.getData();
            User databaseUser = userDatabase.getUser(sentUser.getUserName(), true);

            if(sentUser.getUserID().equals(databaseUser.getUserID())) {
                if(!sentUser.getUserID().equals(user.getUserID()) && user.getPermission().get(3) == 1) {
                    userDatabase.removeUser(sentUser);
                    returnMessage.setCommunicationID(200);
                    consoleMessage.printGeneral("REQUEST ACCEPTED", "User removed", 75);
                } else {
                    returnMessage.setCommunicationID(515);
                    consoleMessage.printWarning("User not permitted to remove user",75);
                }
            } else {
                returnMessage.setCommunicationID(516);
                consoleMessage.printWarning("Database failed to find user to remove",75);
            }

        } catch (Throwable throwable) {
            //Sets the return data to 500 if the Select is unsuccessful.
            returnMessage.setCommunicationID(500);
            consoleMessage.printWarning("Database failed to select all users",75);
        }
    }

    private void handleUpdateBillboard(User user) {

        try {
            if(checkEditPermission(user)) {
                //Instantiates a new BillboardDatabase object connecting to the database specified by the Properties Object.
                //  Uses the updateDatabase method to update database using the information contained within the Message object.
                BillboardDatabase billboardDB = new BillboardDatabase(properties);

                Billboard sentBillboard = (Billboard)sentMessage.getData();
                Billboard billboard = billboardDB.getBillboard(sentBillboard.getBillboardID().toString(), true);

                if(billboard.getScheduled() == 0) {
                    billboardDB.updateDatabase(sentBillboard);
                    //Sets the return data to 200 if the update is successful
                    returnMessage.setCommunicationID(200);
                    consoleMessage.printGeneral("REQUEST ACCEPTED", "Billboard updated   |   billboardID [" + ((Billboard) sentMessage.getData()).getBillboardID() + "]", 75);
                } else if(billboard.getScheduled() == 1 && user.getPermission().get(1) == 1) {
                    billboardDB.updateDatabase(sentBillboard);
                    //Sets the return data to 200 if the update is successful
                    returnMessage.setCommunicationID(200);
                    consoleMessage.printGeneral("REQUEST ACCEPTED", "Billboard updated   |   billboardID [" + ((Billboard) sentMessage.getData()).getBillboardID() + "]", 75);
                } else {
                    returnMessage.setCommunicationID(512);
                    consoleMessage.printWarning("User not authorised to update billboard [" + ((Billboard)sentMessage.getData()).getBillboardID() +"] (Scheduled)", 75);
                }
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

    private void handleRemoveBillboard(User user) {
        try {
            BillboardDatabase billboardDatabase = new BillboardDatabase(properties);
            Billboard sentBillboard = (Billboard)sentMessage.getData();
            Billboard billboard = billboardDatabase.getBillboard(sentBillboard.getBillboardID().toString(), true);

            if(billboard.getBillboardID().equals(sentBillboard.getBillboardID())) {
                if((billboard.getCreatorName().equals(user.getUserName())) || user.getPermission().get(1) == 1) {
                    billboardDatabase.removeBillboard(sentBillboard);
                    returnMessage.setCommunicationID(200);
                    consoleMessage.printGeneral("REQUEST ACCEPTED", "Billboard removed", 75);
                } else {
                    returnMessage.setCommunicationID(511);
                    consoleMessage.printWarning("User not permitted to remove billboard",75);
                }

            } else {
                returnMessage.setCommunicationID(510);
                consoleMessage.printWarning("Database failed to find billboard to remove",75);
            }

        } catch (Throwable throwable) {
            //Sets the return data to 500 if the select is unsuccessful
            returnMessage.setCommunicationID(500);
            consoleMessage.printWarning("Database failed to remove billboard",75);
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
            if(userID.equals(scheduled.getCreatorID())) {
                ScheduleDatabase scheduleDatabase = new ScheduleDatabase(properties);
                boolean success = scheduleDatabase.addToDatabase(scheduled, userID);
                if(success) {
                    //Sets the return data to 200 if the add is successful
                    returnMessage.setCommunicationID(200);
                    consoleMessage.printGeneral("REQUEST ACCEPTED", "Billboard Added", 75);
                } else  {
                    //Sets the return data to 506 if billboard name already exists
                    returnMessage.setCommunicationID(500);
                    consoleMessage.printWarning("Database failed to schedule add to database",75);
                }
            } else {
                //Sets the return data to 505 if the add is unsuccessful due to non matching session and creator
                returnMessage.setCommunicationID(507);
                consoleMessage.printWarning("Schedule creator doesn't match token",75);
            }

        } catch (Throwable throwable) {
            //Sets the return data to 500 if the select is unsuccessful
            returnMessage.setCommunicationID(500);
            consoleMessage.printWarning("Database failed to schedule add to database",75);
        }
    }

    private void handleUpdateSchedule(User user) {
        try {
            ScheduleDatabase scheduleDatabase = new ScheduleDatabase(properties);

            Scheduled scheduled = (Scheduled)sentMessage.getData();
            if(scheduled.getCreatorID().equals(user.getUserID())) {
                scheduleDatabase.updateDatabase(scheduled);
                returnMessage.setCommunicationID(200);
                consoleMessage.printGeneral("REQUEST ACCEPTED", "Schedule updated", 75);
            } else {
                returnMessage.setCommunicationID(517);
                consoleMessage.printWarning("User not permitted to update schedule",75);
            }

        } catch (Throwable throwable) {
            //Sets the return data to 500 if the select is unsuccessful
            returnMessage.setCommunicationID(500);
            consoleMessage.printWarning("Database failed to select billboard",75);
        }
    }

    private void handleRemoveSchedule(User user) {
        try {
            ScheduleDatabase scheduleDatabase = new ScheduleDatabase(properties);
            Scheduled sentSchedule = (Scheduled)sentMessage.getData();

            Scheduled scheduled = scheduleDatabase.getScheduled(String.valueOf(sentSchedule.getID()));

            if(sentSchedule.getID() == scheduled.getID()) {
                if (user.getUserID().equals(scheduled.getCreatorID()) || user.getPermission().get(1) == 1) {
                    scheduleDatabase.removeSchedule(sentSchedule);

                    //Sets the return data to 200 if the remove is successful
                    returnMessage.setCommunicationID(200);
                    consoleMessage.printGeneral("REQUEST ACCEPTED", "Scheduled removed", 75);
                } else {
                    //Sets the return data to 200 if the remove is successful
                    returnMessage.setCommunicationID(513);
                    consoleMessage.printWarning("User not permitted to remove Scheduled", 75);
                }
            } else {
                //Sets the return data to 200 if the remove is successful
                returnMessage.setCommunicationID(514);
                consoleMessage.printWarning("Database failed to find schedule to remove", 75);
            }

        } catch (Throwable throwable) {
            //Sets the return data to 500 if the select is unsuccessful
            returnMessage.setCommunicationID(500);
            consoleMessage.printWarning("Database failed to select billboard",75);
        }
    }
}
