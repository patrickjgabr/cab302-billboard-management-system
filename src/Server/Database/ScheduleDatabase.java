package Server.Database;

import Shared.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * The ScheduleDatabase class extends Database and provides a method set which handles all database actions relevant to the schedule table.
 * This class mostly accepts inputs and returns outputs of the type Schedule by formatting predefined SQL queries with properties of the Schedule Class, which are executed by the parent Database class.
 */
public class ScheduleDatabase extends Database {

    private ResultSet results;
    private Object Exception;
    private Properties properties;

    /**
     * Default constructor used to instantiate a ScheduleDatabase Object
     * @param properties Properties object containing all of the database connection information contained in "properties.txt"
     */
    public ScheduleDatabase(Properties properties) {
        super(properties);
        this.properties = properties;
    }

    //Function used within many of the public methods. Used to determine if given Schedule exists within the table
    public boolean isInTable(Scheduled scheduled) throws Throwable {

        //Initialises the return value to false
        boolean returnValue = false;

        //Attempts to query and select any rows where scheduleID is the same as the given object
        try {

            //Select query which returns rows that have the same scheduleID as the given object
            String sqlSelect = "select scheduleID from schedule where scheduleID = " + scheduled.getID();

            //Parses the select query to the parent Database class which returns a resultSet which returns true if it has a element using .first()
            results = super.runSelectQuery(sqlSelect);
            returnValue = results.next();
            results.close();

        //If an SQL error occurs then false is returned
        } catch (SQLException ignored) {}

        //Return true if the given object is in the database
        return returnValue;
    }

    /**
     *Method which returns all of the data in the schedule table within the database as an ArrayList of type Schedule. This method uses "SELECT * FROM schedule" to get all of the rows from the database,
     * with each row being formatted into a Billboard object and added into the returned ArrayList.
     * @return ArrayList of type Schedule containing all of the data from the database
     * @throws Throwable Exception is thrown if the parent Database throws an exception while executing the select query
     */
    public ArrayList<Scheduled> getSchedule() throws Throwable {

        //Instantiates a new ArrayList of type Schedule
        ArrayList<Scheduled> schedule = new ArrayList<>();

        //Attempts to select all of the rows from the schedule table and format each row into a Schedule object which can be appended to the schedule ArrayList
        try{

            //SQL select statement
            String sqlSelect = "SELECT * FROM schedule";

            //Starts a connection with the database then runs the select query and assigns the result to results and then closes the connection
            super.startConnection();
            results = super.runSelectQuery(sqlSelect);
            super.closeConnection();

            //Iterates through results until .next() returns false each time creating a new Schedule object and adding it to the schedule ArrayList
            while (results.next()) {

                //Converts the bytes saved in the database back into a Schedule object
                Scheduled scheduled = new Scheduled(results.getBytes("scheduleObject"));

                //Sets the scheduleID, creatorID and billboardID to ensure they remain correct and cant be maliciously changed
                scheduled.setID(results.getInt("scheduleID"));
                scheduled.setCreatorID(results.getInt("creatorID"));
                scheduled.setBillboardID(results.getInt("billboardID"));

                //Adds Scheduled to the schedule ArrayList
                schedule.add(scheduled);
            }

            //Closes the result set
            results.close();

        //If the parent (super) throws an exception then that exception is propagated
        } catch (Throwable throwable) {
            throw (Throwable) Exception;
        }

        //ArrayList of type Schedule is returned
        return schedule;
    }

    /**
     *Method which gets a single Schedule from the database. This method uses a SQL select statement to get the specific Schedule by its scheduleID.
     *As the database has a uniqueness constraint on scheduleID one row will match the given value.
     * @param scheduleID scheduleID of the Schedule in the database
     * @return Schedule object which matches the input scheduleID
     * @throws Throwable Exception is thrown if the parent Database throws an exception while executing the select query
     */
    public Scheduled getScheduled(String scheduleID) throws Throwable {

        //Instantiates a empty Schedule object
        Scheduled scheduled = new Scheduled();

        //Attempts to select the row in the database which has the given scheduleID
        try {

            //SQL select which gets the row containing the scheduleID
            String sqlSelect = "SELECT * FROM schedule WHERE scheduleID = " + scheduleID;

            //Starts a connection with the database then runs the select query and assigns the result to results and then closes the connection
            super.startConnection();
            results = super.runSelectQuery(sqlSelect);
            super.closeConnection();

            //If a results has an element and results.next() returns true then assign the result to return value
            if(results.next()) {

                //Converts the bytes saved in the database back into a Schedule object
                scheduled = new Scheduled(results.getBytes("scheduleObject"));

                //Set scheduleID, creatorID and billboardID to ensure they remain correct and cant be maliciously changed
                scheduled.setID(results.getInt("scheduleID"));
                scheduled.setCreatorID(results.getInt("creatorID"));
                scheduled.setBillboardID(results.getInt("billboardID"));
            }

            //Close results
            results.close();

        //If the parent (super) throws an exception then that exception is propagated
        } catch (Throwable throwable) {
            throw (Throwable) Exception;
        }

        //Returns the matched or empty Schedule Object
        return scheduled;
    }

    /**
     *Method which adds a new Schedule Object to the database. This method uses a prepared SQL Insert statement to insert a new row containing the given Schedules data into the database.
     * The given Schedule object must have a creatorID and billboardID that reference a userID in the users table and a billboardID in the billboards table respectively to be added successfully.
     * @param scheduled Schedule Object to be inserted into the schedule table
     * @param userID Creating users userID
     * @return True if the Schedule is successfully added
     * @throws Throwable Exception is thrown if the parent Database throws an exception while executing the insert query
     */
    public boolean addToDatabase(Scheduled scheduled, Integer userID) throws Throwable {

        //Starts connection with database
        super.startConnection();

        //If Schedule object doesn't exist in the database (isInTable matches by scheduleID) then add the given Schedule into the database
        if(!isInTable(scheduled)) {

            //Sets creatorID to be the given userID to ensure no malicious exploit
            scheduled.setCreatorID(userID);

            //Prepared SQL insert statement which takes four parameters
            String sqlInsert = "INSERT INTO schedule (creatorID, billboardID, scheduleObject, inputDate) VALUES (?, ?, ?, ?)";

            //Array of parameters which match the order of the prepared statement
            Object[] parameters = new Object[]{userID, scheduled.getBillboardID(), scheduled, LocalDate.now().toString()};

            //Parent runs SQL insert statement using the Prepared query string and parameter array
            super.runInsertUpdateQuery(sqlInsert, parameters);

            //Prepared SQL update which updates the schedule status of the given Schedules billboardID to 1
            String sqlUpdate = "UPDATE billboards SET scheduled = ? where billboardID = ?";

            //Array of parameters which match the order of the prepared statement
            parameters = new Object[]{1, scheduled.getBillboardID()};

            //Parent runs SQL update statement using the Prepared query string and parameter array
            super.runInsertUpdateQuery(sqlUpdate, parameters);

            //Closes the database connection
            super.closeConnection();

            //Returns true as the Schedule was successfully added
            return true;

        //If schedule table already contains the given Schedule (Same scheduleID) close the connection and return false
        } else {

            //Closes connection and returns false as the Schedule wasn't added
            super.closeConnection();
            return false;
        }
    }

    /**
     *Method which updates an existing Schedule object in the database. This method uses a prepared SQL update statement to update an existing row to the data of given Schedule.
     * The updating Schedule scheduleID, billboardID and creatorID are NOT able to be updated. The update is preformed only on rows that match the given Schedules scheduleID.
     * As the database has a uniqueness constraint on scheduleID only one row will be updated.
     * @param scheduled Schedule Object which is being updated
     * @throws Throwable Exception is thrown if the parent Database throws an exception while executing the update query
     */
    public void updateDatabase(Scheduled scheduled) throws Throwable {

        //Starts database connection
        super.startConnection();

        //If the the given Schedule exists in the database then update its scheduleObject data
        if(isInTable(scheduled)) {

            //Prepared SQL update statement which takes two parameters
            String sqlUpdate = "UPDATE schedule SET scheduleObject = ? where scheduleID = ?";

            //Array of parameters which match the order of the prepared statement
            Object[] parameters = new Object[]{scheduled, scheduled.getID()};

            //Parent runs SQL Update statement using the Prepared query string and parameter array
            super.runInsertUpdateQuery(sqlUpdate, parameters);
        }

        //Closes database connection
        super.closeConnection();
    }

    /**
     *Method which removes a Schedule Object from database. This method uses a SQL DELETE statement to removes rows within the database containing the given Schedules scheduleID.
     *As the database has a uniqueness constraint on scheduleID only one row can possibly be removed.
     * @param scheduled Schedule Object to be removed
     * @throws Throwable Exception is thrown if the parent Database throws an exception while executing the delete query
     */
    public void removeSchedule(Scheduled scheduled) throws Throwable {

        //Start database connection
        super.startConnection();

        //If the given Schedule exists in the database then it can be removed
        if(isInTable(scheduled)) {

            //Attempt to remove the row within the schedule table where the scheduleID matches the given Schedule
            try {

                //Create a connection and statement directly with the database
                Connection connection = DriverManager.getConnection(properties.getDatabaseURL(), properties.getDatabaseUser(), properties.getDatabasePassword());
                Statement statement = connection.createStatement();

                //SQL delete command which the row from schedule containing the given Schedules scheduleID is added to the batch
                String sqlRemoveSchedule = "DELETE FROM schedule WHERE scheduleID = " + scheduled.getID();
                statement.addBatch(sqlRemoveSchedule);

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
                throw (Throwable) Exception;
            }

        //If the database doesn't contain the given Schedule throw a exception
        } else {
            super.closeConnection();
            throw (Throwable) Exception;
        }
    }
}
