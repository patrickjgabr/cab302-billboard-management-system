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

/**
 * The MessageHandler class provides all of the Message response functionality for Control Panel requests.
 * This class is designed to take a received Message and preform specific actions and return a response Message.
 * Response Messages can either have a valid response status (CommunicationID 200) indicating a valid request was made or a invalid response status (CommunicationID 500 or greater).
 */
public class MessageHandler {
    
    private Message sentMessage;
    private Message returnMessage;
    private Properties properties;
    private DatabaseMessage consoleMessage;

    /**
     * Default constructor used to instantiate a MessageHandler Object
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

            //If communiationID is 34 handle password Update
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

            //Print a error message and set the return status to 500
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

            //If userPassword equals nothing then print an error message
            } else {

                //Print a error message and set the return status to 508
                returnMessage.setCommunicationID(508);
                consoleMessage.printWarning("Database failed to add user",75);
            }

        //If a exception is thrown print an error message
        } catch (Throwable throwable) {

            //Print a error message and set the return status to 500
            returnMessage.setCommunicationID(500);
            consoleMessage.printWarning("Database failed to add user",75);
        }
    }

    //Function which generates a random salt string and returns it
    private String generateSalt() {

        //Instantiate StringBuilder to hold the salt and character set
        StringBuilder tokenBuilder = new StringBuilder(64);
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz!@#$%^&*()_+-={}[]:<>?,./";

        //Iterate 64 times each time getting a random character
        for (int i = 0; i < 64; i++) {
            int index = (int)(characters.length()* Math.random());
            tokenBuilder.append(characters.charAt(index));
        }

        //Return the string of the
        return tokenBuilder.toString();
    }

    //Function which generates the new hashed password using the given Users password and salt
    private String generateNewPassword(User user) {

        //Attempt to take the given Users password and salt and hash them
        try {

            //Instantiate MessageDigest with SHA-256 hashing
            MessageDigest passwordHash = MessageDigest.getInstance("SHA-256");

            //Input the current salt and password bytes into the MessageDigest and generate the hash
            passwordHash.update((user.getSalt() + user.getUserPassword()).getBytes());
            byte [] byteArray = passwordHash.digest();

            //Instantiate a StringBuilder to store the hashed characters
            StringBuilder sb = new StringBuilder();

            //Iterate through characters in the output byteArray and append them to the StringBuilder
            for (byte b : byteArray) {
                sb.append(String.format("%02x", b & 0xFF));
            }

            //Cover the StringBuilder into the hashed password String and return it
            return sb.toString();

        //If SHA-256 algorithm doesn't exist then
        } catch (NoSuchAlgorithmException ignored) { }

        //Return blank password if password hash fails
        return "";
    }

    //Function which gets all the users from the database
    private void handleGetUsers() {

        //Attempts to use getUsers method from UserDatabase
        try {

            //Instantiates a new UserDatabase object connecting to the database specified by the Properties Object.
            UserDatabase userDB = new UserDatabase(properties);

            //Get ArrayList of Users from the UserDatabase
            ArrayList<User> requestedUsers = userDB.getUsers();

            //Prints success message and sets the return data to the requestUsers and valid response status
            returnMessage.setData(requestedUsers);
            returnMessage.setCommunicationID(200);
            consoleMessage.printGeneral("REQUEST ACCEPTED", "All users selected", 75);

        //If a exception is thrown print an error message
        } catch (Throwable throwable) {

            //Print error message and set response status as 500
            returnMessage.setCommunicationID(500);
            consoleMessage.printWarning("Database failed to select all users",75);
        }
    }

    //Function which updates user password
    private void handleUpdateUserPassword() {

        //Attempts to update the password of a specific user
        try {

            //Get userName and password from the data property in the received Message
            String[] changePassword = (String[])sentMessage.getData();

            //Instantiate a UserDatabase Object and get the given User corresponding to the userName of (changePassword[0])
            UserDatabase userDatabase = new UserDatabase(properties);
            User user = userDatabase.getUser(changePassword[0], true);

            //If user was found then generate the new password
            if(user.getUserID() != null) {

                //Set the users password to the changedPassword and then set it again using the hash returned by generateNewPassword
                user.setUserPassword(changePassword[1]);
                user.setUserPassword(generateNewPassword(user));

                //Update the resulting User object print a success message and set the response status to 200
                userDatabase.updateDatabase(user);
                returnMessage.setCommunicationID(200);
                consoleMessage.printGeneral("REQUEST ACCEPTED", "User password updated", 75);

            //If user doesn't exist in the database
            } else {

                //Print error message and set response status as 518
                consoleMessage.printWarning("Database failed to find User to update",75);
                returnMessage.setCommunicationID(518);
            }

        //If a exception is thrown print an error message
        } catch (Throwable throwable) {

            //Print error message and set response status as 500
            consoleMessage.printWarning("Database failed to update users password",75);
            returnMessage.setCommunicationID(500);
        }
    }

    //Function which removes User
    private void handleRemoveUser(User user) {

        //Attempts to remove remove user
        try {

            //Instantiate UserDatabase Object
            UserDatabase userDatabase = new UserDatabase(properties);

            //Get User from the received messages data and get the actual User from the database
            User sentUser = (User)sentMessage.getData();
            User databaseUser = userDatabase.getUser(sentUser.getUserName(), true);

            //If the sentUser matches the user returned by the database then remove the sentUser
            if(sentUser.getUserID().equals(databaseUser.getUserID())) {

                //If the sentUser matches the given Session User and the user has the edit user permission remove the sentUser from the database
                if(!sentUser.getUserID().equals(user.getUserID()) && user.getPermission().get(3) == 1) {

                    //Remove user print a success message and set the response status as 200
                    userDatabase.removeUser(sentUser);
                    returnMessage.setCommunicationID(200);
                    consoleMessage.printGeneral("REQUEST ACCEPTED", "User removed", 75);

                //If the sentUser doesn't match the given Session User or doesn't have the edit user permission than print a error message and set return status to 515
                } else {
                    returnMessage.setCommunicationID(515);
                    consoleMessage.printWarning("User not permitted to remove user",75);
                }

            //If the sentUser doesnt exist in the database then print a error message and set return status to 516
            } else {
                returnMessage.setCommunicationID(516);
                consoleMessage.printWarning("Database failed to find user to remove",75);
            }

        //If the database throws an exception print a error message and set return status to 500
        } catch (Throwable throwable) {

            //Print error message and set response status as 500
            returnMessage.setCommunicationID(500);
            consoleMessage.printWarning("Database failed to select all users",75);
        }
    }

    //Function which updates Billboards
    private void handleUpdateBillboard(User user) {

        //Attempts to remove the given message Billboard
        try {

            //Instantiates a new BillboardDatabase object connecting to the database specified by the Properties Object.
            BillboardDatabase billboardDB = new BillboardDatabase(properties);

            //Get the sent billboard from the received message data and request the databases copy of that current billboard
            Billboard sentBillboard = (Billboard) sentMessage.getData();
            Billboard billboard = billboardDB.getBillboard(sentBillboard.getBillboardID().toString(), true);

            //If billboard isn't scheduled then update it
            if (billboard.getScheduled() == 0) {

                //Update the Billboard using sentBillboard print a success message and set the response status to 200
                billboardDB.updateDatabase(sentBillboard);
                returnMessage.setCommunicationID(200);
                consoleMessage.printGeneral("REQUEST ACCEPTED", "Billboard updated   |   billboardID [" + ((Billboard) sentMessage.getData()).getBillboardID() + "]", 75);

                //If billboard is scheduled and given User has edit all permission then update it
            } else if (billboard.getScheduled() == 1 && user.getPermission().get(1) == 1) {

                //Update the Billboard using sentBillboard print a success message and set the response status to 200
                billboardDB.updateDatabase(sentBillboard);
                returnMessage.setCommunicationID(200);
                consoleMessage.printGeneral("REQUEST ACCEPTED", "Billboard updated   |   billboardID [" + ((Billboard) sentMessage.getData()).getBillboardID() + "]", 75);

                //If user is not permitted to to edit the Billboard then an error message is printed and return status set to 512
            } else if (billboard.getScheduled() == 1 && user.getPermission().get(1) != 1) {
                returnMessage.setCommunicationID(512);
                consoleMessage.printWarning("User not authorised to update billboard [" + ((Billboard) sentMessage.getData()).getBillboardID() + "] (Scheduled)", 75);

            //If user is not permitted to to edit the Billboard then an error message is printed and return status set to 504
            } else {
                consoleMessage.printWarning("User not authorised to update billboard [" + ((Billboard)sentMessage.getData()).getBillboardID() +"]", 75);
                returnMessage.setCommunicationID(504);
            }

        //If the database throws an exception print a error message and set return status to 500
        } catch (Throwable throwable) {

            returnMessage.setCommunicationID(500);
            consoleMessage.printWarning("Database failed to update billboard",75);
        }
    }

    //Function which adds received Billboard to the database
    private void handleAddBillboard(User user) {

        //Attempts to add the received billboard to the database
        try {

            //Get the sent billboard from the received message data
            Billboard billboard = (Billboard)sentMessage.getData();

            //If the given session User matches the creator name assigned to the Billboard
            if(user.getUserName().equals(billboard.getCreatorName())) {

                //Instantiates a new BillboardDatabase object connecting to the database specified by the Properties Object.
                BillboardDatabase billboardDB = new BillboardDatabase(properties);

                //Adds the Billboard to the database which returns true if the Billboard was added and has a unique name
                boolean unique = billboardDB.addToDatabase(billboard, user.getUserID());

                //If Billboard was added successfully print success message and set response status as 200
                if(unique) {
                    returnMessage.setCommunicationID(200);
                    consoleMessage.printGeneral("REQUEST ACCEPTED", "Billboard Added   |   billboardID [" + ((Billboard) sentMessage.getData()).getBillboardID() + "]", 75);

                //If Billboard wasn't added successfully a error message is printed and sets the response status as 506
                } else  {

                    //Sets the return data to 506 if billboard name already exists
                    returnMessage.setCommunicationID(506);
                    consoleMessage.printWarning("Billboard name not unique",75);
                }

            //If given User doesn't match the Billboards creatorName then print an error message and set the status as 505
            } else {
                //Sets the return data to 505 if the add is unsuccessful due to non matching session and creator
                returnMessage.setCommunicationID(505);
                consoleMessage.printWarning("Billboard creator doesn't match token",75);
            }

        //If the database throws an exception print a error message and set return status to 500
        } catch (Throwable throwable) {

            //Sets the return data to 500 if the add is unsuccessful
            returnMessage.setCommunicationID(500);
            consoleMessage.printWarning("Database failed to add billboard",75);
        }
    }

    //Function which gets all of the Billboards from the database
    private void handleGetBillboards() {

        //Attempts to get all of the Billboards from the database
        try {

            //Instantiates a new BillboardDatabase object connecting to the database specified by the Properties Object.
            BillboardDatabase billboardDB = new BillboardDatabase(properties);

            //Gets all of the Billboards from the database
            ArrayList<Billboard> requestedBillboards = billboardDB.getBillboards();

            //Sets returnMessage data to Billboard Array prints success message and sets response status as 200
            returnMessage.setData(requestedBillboards);
            returnMessage.setCommunicationID(200);
            consoleMessage.printGeneral("REQUEST ACCEPTED", "All billboards selected", 75);

        //If the database throws an exception print a error message and set return status to 500
        } catch (Throwable throwable) {
            returnMessage.setCommunicationID(500);
            consoleMessage.printWarning("Database failed to select billboards",75);
        }
    }

    //Function which removes given Billboard
    private void handleRemoveBillboard(User user) {

        //Attempts to remove received Billboard
        try {

            //Instantiates a new BillboardDatabase object connecting to the database specified by the Properties Object.
            BillboardDatabase billboardDatabase = new BillboardDatabase(properties);

            //Get the sent billboard from the received message data and the database copy of received Billboard
            Billboard sentBillboard = (Billboard)sentMessage.getData();
            Billboard billboard = billboardDatabase.getBillboard(sentBillboard.getBillboardID().toString(), true);

            //If the sentBillboard exists in the database then remove it
            if(billboard.getBillboardID().equals(sentBillboard.getBillboardID())) {

                //If the given User created the Billboard or has the edit all permission then remove the Billboard
                if((billboard.getCreatorName().equals(user.getUserName())) || user.getPermission().get(1) == 1) {
                    billboardDatabase.removeBillboard(sentBillboard);
                    returnMessage.setCommunicationID(200);
                    consoleMessage.printGeneral("REQUEST ACCEPTED", "Billboard removed", 75);

                //If the given User didn't created the Billboard and doesn't have the edit all permission then print an error message and set the response status as 511
                } else {
                    returnMessage.setCommunicationID(511);
                    consoleMessage.printWarning("User not permitted to remove billboard",75);
                }

            //If then sentBillboard doesn't exist in the database print an error message and set the response status as 510
            } else {
                returnMessage.setCommunicationID(510);
                consoleMessage.printWarning("Database failed to find billboard to remove",75);
            }

        //If the database throws an exception print a error message and set return status to 500
        } catch (Throwable throwable) {
            returnMessage.setCommunicationID(500);
            consoleMessage.printWarning("Database failed to remove billboard",75);
        }
    }

    //Function which handles a schedule request
    private void handleRequestSchedule() {

        //Attempts to get all of the Schedule Objects from the database
        try {

            //Instantiates a new ScheduleDatabase object connecting to the database specified by the Properties Object.
            ScheduleDatabase scheduleDatabase = new ScheduleDatabase(properties);

            //Gets an ArrayList of all the Scheduled Objects in the database
            ArrayList<Scheduled> scheduled = scheduleDatabase.getSchedule();

            //Sets returnMessage data to Schedule Array prints success message and sets response status as 200
            returnMessage.setData(scheduled);
            returnMessage.setCommunicationID(200);
            consoleMessage.printGeneral("REQUEST ACCEPTED", "Schedule selected", 75);

        //If the database throws an exception print a error message and set return status to 500
        } catch (Throwable throwable) {
            returnMessage.setCommunicationID(500);
            consoleMessage.printWarning("Database failed to select schedule",75);
        }
    }

    //Function which adds a Schedule Object to the database
    private void handleAddToSchedule(Integer userID) {

        //Attempts to add the given Schedule Object
        try {

            //Get the sent Schedule from the received message data
            Scheduled scheduled = (Scheduled)sentMessage.getData();

            //If the given userID equals the scheduled creatorID then add the schedule to the database
            if(userID.equals(scheduled.getCreatorID())) {

                //Instantiates a ScheduleDatabase Object
                ScheduleDatabase scheduleDatabase = new ScheduleDatabase(properties);

                //Adds the Schedule to the database which returns true if the Schedule was added
                boolean success = scheduleDatabase.addToDatabase(scheduled, userID);

                //If the Schedule was successfully added then print a success message and set the response status as 200
                if(success) {
                    returnMessage.setCommunicationID(200);
                    consoleMessage.printGeneral("REQUEST ACCEPTED", "Billboard Added", 75);

                //If the Schedule was not successfully added then print a error message and set the response status as 500
                } else  {
                    returnMessage.setCommunicationID(500);
                    consoleMessage.printWarning("Database failed to schedule add to database",75);
                }

            //If the given userID doesn't match the scheduled creatorId then print a error message and set the response status as 507
            } else {
                returnMessage.setCommunicationID(507);
                consoleMessage.printWarning("Schedule creator doesn't match token",75);
            }

        //If the database throws an exception print a error message and set return status to 500
        } catch (Throwable throwable) {
            returnMessage.setCommunicationID(500);
            consoleMessage.printWarning("Database failed to schedule add to database",75);
        }
    }

    //Function which updates a Schedule Object in the database
    private void handleUpdateSchedule(User user) {

        //Attempts to update the data with the received Schedule Object
        try {

            //Instantiates a ScheduleDatabase Object
            ScheduleDatabase scheduleDatabase = new ScheduleDatabase(properties);

            //Get the sent Schedule from the received message data
            Scheduled scheduled = (Scheduled)sentMessage.getData();

            //If given User has the schedule permission update the Schedule
            if(user.getPermission().get(2) == 1) {

                //Update the Schedule Object in the database to the received Schedule Object
                scheduleDatabase.updateDatabase(scheduled);

                //Print a success message and set return status as 200
                returnMessage.setCommunicationID(200);
                consoleMessage.printGeneral("REQUEST ACCEPTED", "Schedule updated", 75);

            //If the received Schedule doesn't have the same creatorID as the given User than print an error message and set the return status as 517
            } else {
                returnMessage.setCommunicationID(517);
                consoleMessage.printWarning("User not permitted to update schedule",75);
            }

        //If the database throws an exception print a error message and set return status to 500
        } catch (Throwable throwable) {
            //Sets the return data to 500 if the select is unsuccessful
            returnMessage.setCommunicationID(500);
            consoleMessage.printWarning("Database failed to select billboard",75);
        }
    }

    //Function which removes a received Schedule Object
    private void handleRemoveSchedule(User user) {

        //Attempts to remove received Schedule
        try {

            //Instantiates a ScheduleDatabase Object
            ScheduleDatabase scheduleDatabase = new ScheduleDatabase(properties);

            //Get the sent Schedule from the received message data
            Scheduled sentSchedule = (Scheduled)sentMessage.getData();

            //Get database copy of the current Scheduled Object
            Scheduled scheduled = scheduleDatabase.getScheduled(String.valueOf(sentSchedule.getID()));

            //If the sent Schedule object exists in the database then remove it
            if(sentSchedule.getID() == scheduled.getID()) {

                //If the given User has the schedule billboard permission then remove the received Schedule Object
                if (user.getPermission().get(2) == 1) {

                    //Remove the received Schedule from the database
                    scheduleDatabase.removeSchedule(sentSchedule);

                    //Print a success message and set the response status to 200
                    returnMessage.setCommunicationID(200);
                    consoleMessage.printGeneral("REQUEST ACCEPTED", "Scheduled removed", 75);

                //If the given User does not have the schedule billboard permission then print a error message and set the response status as 513
                } else {
                    returnMessage.setCommunicationID(513);
                    consoleMessage.printWarning("User not permitted to remove Scheduled", 75);
                }

            //If the sent Schedule object doesn't exist in the database print a error message and set the response status as 514
            } else {
                returnMessage.setCommunicationID(514);
                consoleMessage.printWarning("Database failed to find schedule to remove", 75);
            }

        //If the database throws an exception print a error message and set return status to 500
        } catch (Throwable throwable) {
            returnMessage.setCommunicationID(500);
            consoleMessage.printWarning("Database failed to select billboard",75);
        }
    }
}
