package Server.Database;

import Shared.Billboard;
import Shared.Properties;

import java.sql.*;
import java.util.ArrayList;

/**
 * The BillboardDatabase class extends Database and provides a method set which handles all database actions relevant to the billboards table.
 * This class mostly accepts inputs and returns outputs of the type Billboard by formatting predefined SQL queries with properties of the Billboard Class, which are executed by the parent Database class.
 */
public class BillboardDatabase extends Database {

    private ResultSet results;
    private Object Exception;
    private Properties properties;

    /**
     * Default constructor used to instantiate a BillboardDatabase Object
     * @param properties Properties object containing all of the database connection information contained in "properties.txt"
     */
    public BillboardDatabase(Properties properties) {
        super(properties);
        this.properties = properties;
    }

    //Function used within many of the public methods. Used to determine if given Billboard exists within the table
    private boolean isInTable(Billboard billboard) throws Throwable {

        //Initialises the return value to false
        boolean returnValue = false;

        //Attempts to query and select any rows where billboardName is the same as the given object
        try {

            //Select query which returns rows that have the same billboardName as the given object
            String sqlSelect = "select billboardID from billboards where billboardName = \"" + billboard.getName() + "\"";

            //Parses the select query to the parent Database class which returns a resultSet which returns true if it has a element using .first()
            results = super.runSelectQuery(sqlSelect);
            returnValue = results.first();
            results.close();

        //If an SQL error occurs then false is returned
        } catch (SQLException ignored) {}

        //Return true if the given object is in the database
        return returnValue;
    }

    /**
     *Method which returns all of the data in the billboards table within the database as an ArrayList of type Billboard. This method uses "SELECT * FROM billboards" to get all of the rows from the database,
     * with each row being formatted into a Billboard object and added into the returned ArrayList.
     * @return ArrayList of type Billboard containing all of the data from the database
     * @throws Throwable Exception is thrown if the parent Database throws an exception while executing the select query
     */
    public ArrayList<Billboard> getBillboards() throws Throwable {

        //Instantiates a new ArrayList of type Billboard
        ArrayList<Billboard> billboards = new ArrayList<>();

        //Attempts to select all of the rows from the billboards table and format each row into a Billboard object which can be appended to the billboards ArrayList
        try {

            //SQL select statement
            String sqlSelect = "SELECT * FROM billboards";

            //Starts a connection with the database then runs the select query and assigns the result to results and then closes the connection
            super.startConnection();
            results = super.runSelectQuery(sqlSelect);
            super.closeConnection();

            //Iterates through results until .next() returns false each time creating a new Billboard object and adding it to the billboards ArrayList
            while(results.next()) {

                //Converts the bytes saved in the database back into a Billboard object
                Billboard billboard = new Billboard(results.getBytes("billboardObject"));

                //Sets the scheduled and billboardID to ensure they remain correct and cant be maliciously changed
                billboard.setScheduled(results.getInt("scheduled"));
                billboard.setBillboardID(results.getInt("billboardID"));

                //Adds Billboard to the billboards ArrayList
                billboards.add(billboard);
            }

            //Closes the result set
            results.close();

        //If the parent (super) throws an exception then that exception is propagated
        } catch (Throwable throwable) {
            throw (Throwable) Exception;
        }

        //ArrayList of type Billboard is returned
        return billboards;
    }

    /**
     *Method which gets a single Billboard from the database. This method uses a SQL select statement to get the specific Billboard by either its billboardName or billboardID.
     * As the database has a uniqueness constraint on both the billboardID and billboardName only one row will match the given value.
     * @param value Either the billboardID or billboardName of the Billboard in the database
     * @param id True if searching by billboardID
     * @return Billboard object which matches the input search conditions
     * @throws Throwable Exception is thrown if the parent Database throws an exception while executing the select query
     */
    public Billboard getBillboard(String value, boolean id) throws Throwable {

        //Instantiates a empty Billboard object
        Billboard returnValue = new Billboard();

        //Attempts to select the row in the database given the search parameters and setting the result to returnValue
        try {

            //Sets the Select string based upon if the billboardID or name are being used to get the required value
            String sqlSelect;
            if(id) {
                sqlSelect = "SELECT * FROM billboards WHERE billboardID = " + value;
            } else {
                sqlSelect = "SELECT * FROM billboards WHERE billboardName = \"" + value + "\"";
            }

            //Starts a connection with the database then runs the select query and assigns the result to results and then closes the connection
            super.startConnection();
            results = super.runSelectQuery(sqlSelect);
            super.closeConnection();

            //If a results has an element and results.next() returns true then assign the result to return value
            if(results.next()) {

                //Converts the bytes saved in the database back into a Billboard object
                returnValue = new Billboard(results.getBytes("billboardObject"));

                //Set the scheduled and billboardID to ensure they remain correct and cant be maliciously changed
                returnValue.setBillboardID(results.getInt("billboardID"));
                returnValue.setScheduled(results.getInt("scheduled"));
            }

            //Close results
            results.close();

        //If the parent (super) throws an exception then that exception is propagated
        } catch (Throwable throwable) {
            throw (Throwable) Exception;
        }

        //Returns the matched or empty Billboard Object
        return returnValue;
    }

    /**
     *Method which updates an existing Billboard object in the database. This method uses a prepared SQL update statement to update an existing row to the data of the given Billboard.
     * The updating Billboards name and billboardID are NOT able to be updated. The update is performed only on rows that match the given Billboards billboardID.
     * As the database has a uniqueness constraint on billboardID only one row will be updated.
     * @param billboard Billboard Object which is being updated
     * @throws Throwable Exception is thrown if the parent Database throws an exception while executing the update query
     */
    public void updateDatabase(Billboard billboard) throws Throwable {

        //Start database connection
        super.startConnection();

        //If the the given Billboard exists in the database then update its billboardObject data
        if (isInTable(billboard)) {

            //Prepared SQL update statement which takes two parameters
            String sqlUpdate = "UPDATE billboards SET billboardObject = ? where billboardName = ?";

            //Array of parameters which match the order of the prepared statement
            Object[] parameters = new Object[]{billboard, billboard.getName()};

            //Parent runs SQL Update statement using the Prepared query string and parameter array
            super.runInsertUpdateQuery(sqlUpdate, parameters);
        }

        //Closes database connection
        super.closeConnection();
    }

    /**
     *Method which adds a new Billboard Object to the database. This method uses a prepared SQL Insert statement to insert a new row containing the given Billboards data into the database.
     * The given Billboard object must have a unique name and a creatorID that references a userID in the users table to be added successfully.
     * @param billboard Billboard Object to be inserted into the billboards table
     * @param userID Creating users userID
     * @return True if the Billboard is successfully added
     * @throws Throwable Exception is thrown if the parent Database throws an exception while executing the insert query
     */
    public boolean addToDatabase(Billboard billboard, Integer userID) throws Throwable {

        //Starts connection with database
        super.startConnection();

        //If Billboard object doesn't exist in the database (isInTable matches by billboardName) then add the given Billboard into the database
        if(!isInTable(billboard)) {

            //Prepared SQL insert statement which takes three parameters
            String sqlInsert = "INSERT INTO billboards (creatorID, billboardName, billboardObject) VALUES (?, ?, ?)";

            //Array of parameters which match the order of the prepared statement
            Object[] parameters = new Object[]{userID, billboard.getName(), billboard};

            //Parent runs SQL insert statement using the Prepared query string and parameter array
            super.runInsertUpdateQuery(sqlInsert, parameters);

            //Closes the database connection
            super.closeConnection();

            //Returns true as the Billboard was successfully added
            return true;

        //If billboard table already contains the given Billboard (Same billboardName) close the connection and throw an exception
        } else {

            //Closes connection and returns false as the Billboard wasn't added
            super.closeConnection();
            return false;
        }
    }

    /**
     *Method which removes a Billboard Object from database. This method uses a SQL DELETE statement to removes rows within the database containing the given Billboards billboardID.
     *As the database has a uniqueness constraint on billboardID only one row can possibly be removed. Removing a given User object will also remove all other references to that object in the database
     * @param billboard Billboard Object to be removed
     * @throws Throwable Exception is thrown if the parent Database throws an exception while executing the delete query
     */
    public void removeBillboard(Billboard billboard) throws Throwable {

        //Start database connection
        super.startConnection();

        //If the given Billboard exists in the database then it can be removed as well as data that references it
        if(isInTable(billboard)) {

            //Attempt to remove the rows from the schedule table where the billboardID of the given Billboard matches
            // Then remove the given Billboard from the billboards table
            try {

                //Create a connection and statement directly with the database
                Connection connection = DriverManager.getConnection(properties.getDatabaseURL(), properties.getDatabaseUser(), properties.getDatabasePassword());
                Statement statement = connection.createStatement();

                //SQL delete command which removes all rows from schedule containing the given Billboards billboardID is added to the batch
                String sqlRemoveSchedule = "DELETE FROM schedule WHERE billboardID = " + billboard.getBillboardID();
                statement.addBatch(sqlRemoveSchedule);

                //SQL remove command which removes all rows from billboards containing the given Billboards billboardID is added to the batch
                String sqlRemoveBillboard = "DELETE FROM billboards WHERE billboardID = " + billboard.getBillboardID();
                statement.addBatch(sqlRemoveBillboard);

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
            throw (Throwable) Exception;
        }
    }
}
