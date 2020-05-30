package Server.Database;

import java.sql.*;
import java.util.ArrayList;
import Shared.*;

/**
 * The UserDatabase class extends Database and provides a method set which handles all database actions relevant to the users table.
 * This class mostly accepts inputs and returns outputs of the type User by formatting predefined SQL queries with properties of the User Class, which are executed by the parent Database class.
 */
public class UserDatabase extends Database {

    private ResultSet results;
    private Object Exception;
    private Properties properties;

    /**
     * Default constructor used to instantiate a UserDatabase Object
     * @param properties Properties object containing all of the database connection information contained in "properties.txt"
     */
    public UserDatabase(Properties properties) {
        super(properties);
        this.properties = properties;
    }

    //Function used within many of the public methods. Used to determine if given Billboard exists within the table
    private boolean isInTable(User user) {

        //Initialises the return value to false
        boolean returnValue = false;

        //If user is not null the select the userID from users table where the userName is the given Users userName
        if(user != null) {

            //Attempts to query and select any rows where userName is the same as the given object
            try {

                //Select query which returns rows that have the same userName as the given object
                String sqlSelect = "select userID from users where userName = \"" + user.getUserName() + "\"";

                //Parses the select query to the parent Database class which returns a resultSet which returns true if it has a element using .first()
                results = super.runSelectQuery(sqlSelect);
                returnValue = results.next();
                results.close();

            //If an SQL error occurs then false is returned
            } catch (Throwable ignored) {}
        }

        //Return true if the given object is in the database
        return returnValue;
    }

    /**
     * Method which returns all of the data in the users table within the database as an ArrayList of type User. This method uses "SELECT * FROM users" to get all of the rows from the database,
     * with each row being formatted into a User object and added into the returned ArrayList.
     * @return ArrayList of type User containing all of the data from the database
     * @throws Throwable Exception is thrown if the parent Database throws an exception while executing the select query
     */
    public ArrayList<User> getUsers() throws Throwable {

        //Instantiates a new ArrayList of type User
        ArrayList<User> allUsers = new ArrayList<>();

        //Attempts to select all of the rows from the users table and format each row into a User object which can be appended to the allUsers ArrayList
        try {

            //SQL select statement
            String sqlSelect = "SELECT * FROM users";

            //Starts a connection with the database then runs the select query and assigns the result to results and then closes the connection
            super.startConnection();
            results = super.runSelectQuery(sqlSelect);
            super.closeConnection();

            //Iterates through results until .next() returns false each time creating a new User object and adding it to the allUsers ArrayList
            while(results.next()) {

                //Converts the bytes saved in the database back into a User object
                User user = new User(results.getBytes("userObject"));

                //Sets the userID to it remains correct and cant be maliciously changed. Also sets the salt and password of User to a blank string
                user.setUserID(results.getInt("userID"));
                user.setSalt("");
                user.setUserPassword("");

                //Adds User to the allUsers ArrayList
                allUsers.add(user);
            }

            //Closes the result set
            results.close();

        //If the parent (super) throws an exception then that exception is propagated
        } catch (Throwable throwable) {
            throw (Throwable) Exception;
        }

        //ArrayList of type User is returned
        return allUsers;
    }

    /**
     *Method which gets a single User from the database. This method uses a SQL select statement to get the specific User by either its userName or userID.
     * As the database has a uniqueness constraint on both the userID and userName only one row will match the given value.
     * @param value Either the userID or userName of the User in the database
     * @param userName True if searching by userName
     * @return User object which matches the input search condition
     * @throws Throwable Exception is thrown if the parent Database throws an exception while executing the select query
     */
    public User getUser(String value, boolean userName) throws Throwable {

        //Instantiates a empty User object
        User returnValue = new User();

        //Attempts to select the row in the database given the search parameters and setting the result to returnValue
        try {

            //Sets the Select string based upon if the userID or userName are being used to get the required value
            String sqlSelect;
            if(userName) {
                sqlSelect = "SELECT * FROM users WHERE userName = \"" + value + "\"";
            } else {
                sqlSelect = "SELECT * FROM users WHERE userID = " + value;
            }

            //Starts a connection with the database then runs the select query and assigns the result to results and then closes the connection
            super.startConnection();
            results = super.runSelectQuery(sqlSelect);
            super.closeConnection();

            //If a results has an element and results.next() returns true then assign the result to return value
            if(results.next()) {

                //Converts the bytes saved in the database back into a User object
                returnValue = new User(results.getBytes("userObject"));

                //Set the userID to ensure it remains correct and cant be maliciously changed
                returnValue.setUserID(results.getInt("userID"));
            }

            //Close results
            results.close();

        //If the parent (super) throws an exception then that exception is propagated
        } catch (Throwable throwable) {
            throw (Throwable) Exception;
        }

        //Returns the matched or empty User Object
        return returnValue;
    }

    /**
     *Method which updates an existing User object in the database. This method uses a prepared SQL update statement to update an existing row to the data of the given User.
     * The updating Users userName and userID are NOT able to be updated. The update is performed only on rows that match the given Users userID.
     * As the database has a uniqueness constraint on userID only one row will be updated.
     * @param user User Object which is being updated
     * @throws Throwable Exception is thrown if the parent Database throws an exception while executing the update query
     */
    public void updateDatabase(User user) throws Throwable {

        //If no connection has been started with the database then a connection must be started
        if(!super.getConnectionStatus()) {
            super.startConnection();
        }

        //If the the given User exists in the database then update its userObject data
        if (isInTable(user)) {

            //Prepared SQL update statement which takes two parameters
            String sqlUpdate = "UPDATE users SET userObject = ? where userName = ?";

            //Array of parameters which match the order of the prepared statement
            Object[] parameters = new Object[]{user, user.getUserName()};

            //Parent runs SQL Update statement using the Prepared query string and parameter array
            super.runInsertUpdateQuery(sqlUpdate, parameters);
        }

        //Closes database connection
        super.closeConnection();
    }

    /**
     *Method which adds a new User Object to the database. This method uses a prepared SQL Insert statement to insert a new row containing the given Users data into the database.
     * The given User object must have a unique userName to be added successfully.
     * @param user User Object to be inserted into the users table
     * @throws Throwable Exception is thrown if the parent Database throws an exception while executing the insert query
     */
    public void addToDatabase(User user) throws Throwable {

        //Starts connection with database
        super.startConnection();

        //If User object doesn't exist in the database (isInTable matches by userName) then add the given User into the database
        if (!isInTable(user)) {

            //Prepared SQL insert statement which takes three parameters
            String sqlInsert = "INSERT INTO users (userName, userObject) VALUES (?, ?)";

            //Array of parameters which match the order of the prepared statement
            Object[] parameters = new Object[]{user.getUserName(), user};

            //Parent runs SQL insert statement using the Prepared query string and parameter array
            super.runInsertUpdateQuery(sqlInsert, parameters);

        //If billboard table already contains the given User (Same userName) close the connection and throw an exception
        } else {
            super.closeConnection();
            throw (Throwable) Exception;
        }

        //Update the User within the database so that the userObject has an object with the correct userID
        updateUserID(user);

        //If the database has a current connection then close it
        if(super.getConnectionStatus()) {
            super.closeConnection();
        }
    }

    //Function which updates the userObject of a new user to contain the the correct userID
    private void updateUserID(User user) throws Throwable {

        //SQL select string which selects the userID from the row containing the given Users userName
        String sqlSelect = "SELECT userID FROM users WHERE userName = \"" + user.getUserName() + "\"";

        //Parent runs SQL select statement and assigns the result to result
        ResultSet result = super.runSelectQuery(sqlSelect);

        //Attempts to get the first value of result and set the userID to the given Users userID
        try {

            //Move courser to first position
            result.next();

            //Set the given Users userID
            user.setUserID(result.getInt("userID"));

            //Close result and updateDatabase using the given User with assigned userID
            result.close();
            updateDatabase(user);
        } catch (SQLException ignored) {}

        //If the connection with the database hasn't been closed then close it
        if(super.getConnectionStatus()) {
            super.closeConnection();
        }
    }

    /**
     *Method which removes a User Object from database. This method uses a SQL DELETE statement to removes rows within the database containing the given Users userID.
     *As the database has a uniqueness constraint on userID only one row can possibly be removed. Removing a given User object will also remove all other references to that object in the database
     * @param user User Object to be removed
     * @throws Throwable Exception is thrown if the parent Database throws an exception while executing the delete query
     */
    public void removeUser(User user) throws Throwable {

        //Start database connection
        super.startConnection();

        //If the given User exists in the database then it can be removed as well as data that references it
        if(isInTable(user)) {

            //Attempt to remove the rows from the sessions table where the userID matches the userID of the given User
            // Then remove rows from the schedule table where the creatorID matches the userID of the given User
            // Then remove rows from the schedule table where the billboardID of matches a billboardID that was created by the given User
            // Then remove rows from the billboards table where the creatorID matches the userID of the given User
            try {

                //Create a connection and statement directly with the database
                Connection connection = DriverManager.getConnection(properties.getDatabaseURL(), properties.getDatabaseUser(), properties.getDatabasePassword());
                Statement statement = connection.createStatement();

                //SQL delete command which removes all rows from sessions containing the given Users userID is added to the batch
                String sqlRemoveSession = "DELETE FROM sessions WHERE userID = " + user.getUserID();
                statement.addBatch(sqlRemoveSession);

                //SQL delete command which removes all rows from schedule containing the given Users userID is added to the batch
                String sqlRemoveSchedule = "DELETE FROM schedule WHERE creatorID = " + user.getUserID();
                statement.addBatch(sqlRemoveSchedule);

                //SQL delete command which removes all rows from schedule where the billboardID matches a billboardID that was created by the given User is added to the batch
                String sqlRemoveScheduleBillboard = "DELETE FROM schedule WHERE billboardID IN (SELECT billboardID FROM billboards WHERE creatorID = " + user.getUserID() + ")";
                statement.addBatch(sqlRemoveScheduleBillboard);

                //SQL delete command which removes all the rows from the billboards table containing the given Users userID is added to the batch
                String sqlRemoveBillboards = "DELETE FROM billboards WHERE creatorID = " + user.getUserID();
                statement.addBatch(sqlRemoveBillboards);

                //SQL delete command which removes all of the rows from the users table containing the given Users userID is added to the batch
                String sqlRemoveUser = "DELETE FROM users WHERE userID = " + user.getUserID();
                statement.addBatch(sqlRemoveUser);

                //Statement batch is executed
                statement.executeBatch();

                //Statement and connection is closed
                statement.close();
                connection.close();

                //Update all of the schedule values for each row of the billboards table and then close the connection
                super.updateBillboardStatus();
                super.closeConnection();

            //If the parent (super) or statement batch throws an exception then that exception is propagated
            } catch (Throwable throwable) {
                super.closeConnection();
                throw (Throwable) Exception;
            }

        //If the database doesn't contain the given User throw a exception
        } else {
            super.closeConnection();
            throw (Throwable) Exception;
        }
    }
}