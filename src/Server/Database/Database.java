package Server.Database;

import java.io.*;
import java.sql.*;
import java.util.*;

import Server.ConsoleMessage.DatabaseMessage;
import Shared.*;
import Shared.Properties;

/**
 * The Database class provides all of the low level database functionality including Select, update, insert and delete.
 * This class is designed to be extended by other classes, however is still functional as a stand alone class.
 * The Select, update, insert and delete methods contained within this are class are directly related to the SQL commands they reference.
 * The other Methods are largely responsible for maintaining the state of the database connection and checking its configuration.
 */
public class Database {

    private Properties properties;
    private Connection connection;
    private Statement statement;
    public DatabaseMessage databaseMessage;
    private boolean connectionStatus;
    private ArrayList<String> tables;
    private Object Exception;

    /**
     * Default constructor used to instantiate a Database Object
     * @param properties Object containing all of the database connection parameters
     */
    public Database(Properties properties) {
        this.databaseMessage = new DatabaseMessage();
        this.properties = properties;
    }

//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Check database

    /**
     * Checks the configuration of the database specified in the passed Properties file. It achieves this by checking that all of the required tables are present
     * within the database. If the database is found to be misconfigured then all of the tables within the database are dropped and new tables created. When a new database is generated all of the current data is lost
     * and a single "root" user with all permissions and password "password" is added.
     * @return Returns true if the database is configured correctly
     */
    public boolean checkConfiguration() {

        //Ensures a database connection has been started
        if(connectionStatus) {

            //Checks to ensure all of the required tables exist
            if(tableExist()) {

                //Prints a success message to the console and returns true (This will allow the sever to run)
                databaseMessage.printGeneral("DATABASE", "Configuration test passed", 75);
                return true;

            //All of the required tables don't exist and therefore the database requires resetting
            } else {

                //Prints a error message to the console and attempts to reset the database
                databaseMessage.printGeneral("DATABASE", "Configuration test failed", 75);
                databaseMessage.printError1004();

                //If resetDatabase returns true then the database was successfully reset and therefore the sever can run
                //If resetDatabase returns false then the database failed to reset and sever cannot run
                return restDatabase();
            }

        //Error message printed to the console when a connection hasn't been started
        } else {
            databaseMessage.printError1003();
            return false;
        }
    }

    //Function which checks the existence of the required tables for the server to run (users, billboards, sessions and schedule)
    private boolean tableExist() {

        //Gets an ArrayList containing the table names from the database and checks them against the expected tables
        //  returns true if all the expected tables are present and false if any are missing
        tables = getTables();
        return tables.contains("users") && tables.contains("billboards") && tables.contains("sessions") && tables.contains("schedule");
    }

    //Function which gets all of the table names from the database
    private ArrayList<String> getTables() {

        //Generates a blank ArrayList which the table names will be added into
        ArrayList<String> tables = new ArrayList<>();
        try {

            //Uses sql command "SHOW TABLES" to get all the table names from the database and then add them into the tables ArrayList
            ResultSet resultSet = statement.executeQuery("SHOW TABLES");
            while(resultSet.next()) {
                tables.add(resultSet.getString("Tables_in_" + properties.getDatabaseName()));
            }
            resultSet.close();


        //If an error occurs than tables will remain empty (This will cause the database to restart
        } catch (SQLException ignored) {
        }

        return tables;
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Generate new database

    //Function which generates a new database and turns true if this operation is success
    private boolean restDatabase() {
        return generateNewDatabase();
    }

    //Function which is preforms each of the operations required to reset the database. Including dropping all tables, recreating them and adding user "root"
    private boolean generateNewDatabase() {

        //Prints out a console message saying that a new database is being generated and drops all tables
        databaseMessage.printGeneral("DATABASE", "Creating new database", 75);
        boolean returnValue = dropAllTables();

        //If all the tables were successfully dropped then the new tables are created
        if (returnValue) {
            returnValue = createAllTables();
        }

        //If all of the tables were successfully dropped and recreated then a success message is printed
        if(returnValue) {
            databaseMessage.printGeneral("DATABASE", "Successfully created new database", 75);

        //If the existing tables fail to be dropped or the new tables fail to be recreated then a error message is printed
        } else {
            databaseMessage.printWarning("Database Failed to create new database", 75);
        }

        //Returns the state of the database
        //  True if successfully regenerated
        return returnValue;
    }

    //Function which is responsible for dropping all existing tables from the database
    private boolean dropAllTables() {

        //Initialises the configuration value to true
        boolean correctConfiguration = true;

        //Ensures tables exist before trying to drop them
        if(tables.size() != 0) {

            //Drops all of the current tables using the sql command "DROP TABLES table_name"
            for (String table: tables) {
                try {
                    statement.addBatch("DROP TABLES " + table);

                //Sets configuration to false if statement fails to add to the batch and breaks
                } catch (SQLException e) {
                    correctConfiguration = false;
                    break;
                }
            }

            //Attempts to execute the batch of sql drop commands and prints a success message
            try {
                statement.executeBatch();
                databaseMessage.printGeneral("DATABASE", "Dropping all tables", 50);

            //If batch fails to execute then a error message is printed and configuration is set to false
            } catch (SQLException e) {
                databaseMessage.printError1005();
                correctConfiguration = false;
            }
        }

        //Returns whether all tables were successfully dropped
        //  True if all tables were successfully dropped
        return correctConfiguration;
    }

    //Function which creates the tables once all of the current tables have been dropped
    private boolean createAllTables() {

        //Initialises a configuration variable and a string to hold the sql queries
        boolean correctConfiguration;
        StringBuilder tablesSQL = new StringBuilder();

        //Attempts to read the database.sql file found in external resources which contains the sql required to generate the tables
        try {
            FileReader fileReader = new FileReader("./././externalResources/database.sql");
            BufferedReader reader = new BufferedReader(fileReader);
            String currentLine;

            //Reads the database.sql file line by line and appends it to tablesSQL
            while((currentLine = reader.readLine()) != null) {
                tablesSQL.append(currentLine);
            }

        //If an exception is thrown while reading the file than the error message is printed to the console
        } catch (IOException e) {
            databaseMessage.printError1006();
        }

        //Takes the read file and breaks it up into its individual sql statements. SQL statements are separated by ";" characters
        String[] tableStatement = tablesSQL.toString().split(";");

        //These individual SQL commands are then individually added to the query batch and executed
        try {

            //Adds each SQL command to the batch
            for (String statement: tableStatement) {
                this.statement.addBatch(statement);
            }

            //Execute command batch and prints a success message to the console
            statement.executeBatch();
            databaseMessage.printGeneral("DATABASE", "CREATE statements successful", 50);

            //Once the create tables have been generated user "root" is added to the users table
            //  If this user cannot be added then correctConfiguration will be set to fasle
            correctConfiguration = createRootUser();

            //If all of the tables have been successfully added and user "root" added to the users table, then the checkConfiguration() function is run again
            if(correctConfiguration) {
                checkConfiguration();
            }

        //Prints an error message to the console if the batch of commands fails to execute and sets correctConfiguration to false
        } catch (SQLException e) {
            databaseMessage.printWarning("Database \"CREATE\" statements failed", 50);
            correctConfiguration = false;
        }

        //Returns the status of the database
        //  True if the tables have successfully been created and user "root" added
        return correctConfiguration;
    }

    //Function which inserts user "root" into the table
    private boolean createRootUser() {

        //Initialises the User object containing all of the the default values; all perms, password "password" and ID 100000.
        ArrayList<Integer> permissions = new ArrayList<>(Arrays.asList(1, 1, 1, 1));
        User rootUser = new User("root", "c13866ce9e42e90d3cf50ded2dc9e00194ffc4ad4e15865cd1b368f168944646", permissions, 100000,"y6WOb24rUAINN6KoUQ7lWNeniyTpsxPaZqzEhvAMzSqE5MrIx2kJS9TaTm0rl96n");

        //Attempts to insert the user object into the database using a prepared statement and then prints a success message and returns true
        try {

            //Creates a prepared statement with 3 values
            PreparedStatement statement = connection.prepareStatement("INSERT INTO users VALUES (?,?,?)");
            statement.clearParameters();

            //Inserts userID, userName and the object bytearray into the prepared statement
            statement.setInt(1, 100000);
            statement.setString(2, "root");
            statement.setBinaryStream(3, new ByteArrayInputStream(rootUser.getByteArray()));
            statement.executeUpdate();
            databaseMessage.printGeneral("DATABASE", "Root user added to users table", 50);
            return true;

        //If the statement fails to execute the prepared statement then an error message is printed and false returned
        } catch (SQLException e) {
            databaseMessage.printWarning("DATABASE failed to add root user", 50);
            return false;
        }
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //SQL statements

    /**
     * Attempts to run the given SQL Insert or Update using the given parameters. This method takes the given query which should be a prepared statement and
     * formats the each parameter depending on its Object type (e.g Billboard or Integer). If an error occurs executing the resulting statement a Exception is thrown.
     * @param query Prepared SQL update or insert query
     * @param values Objects which represent Parameters within the "query"
     * @throws Throwable Exception is thrown if the database manager throws either a integrity or SQL error
     */
    public void runInsertUpdateQuery(String query, Object[] values) throws Throwable {

        //Ensures that a connection has been started
        if(connectionStatus) {

            //Attempts to generate a prepareStatement and populate with the values from "values" given their type
            try {

                //Initialises the prepareStatement using the given query
                PreparedStatement insertQuery = connection.prepareStatement(query);
                insertQuery.clearParameters();

                //Loops through each the values array and sets the parameter values given their type
                int index = 1;
                for (Object value: values) {

                    //If the element is of type String then the "setString" method is used to set the parameter
                    if (value instanceof String) {
                        insertQuery.setString(index, (String)value);

                    //If the element is of type Integer then the "setInt" method is used to set the parameter
                    } else if (value instanceof Integer) {
                        insertQuery.setInt(index, (Integer) value);

                    //If the element is of type User then the "setBinaryStream" method is used to set the parameter
                    } else if (value instanceof User) {

                        //Casts the element to type User and uses the "getByteArray()" User method to return the "byte[]" array of the Object
                        //  This "byte[]" can parsed to a ByteArrayInputStream to get the BinaryStream containing all of the Object data
                        User user = (User)value;
                        insertQuery.setBinaryStream(index, new ByteArrayInputStream(user.getByteArray()), user.getByteArray().length);


                    //If the element is of type Billboard then the "setBinaryStream" method is used to set the parameter
                    } else if (value instanceof Billboard) {

                        //Casts the element to object type Billboard and uses the "getByteArray()" Billboard method to return the "byte[]" array of the Object
                        //  This "byte[]" can parsed to a ByteArrayInputStream to get the BinaryStream containing all of the Object data
                        Billboard billboard = (Billboard)value;
                        insertQuery.setBinaryStream(index, new ByteArrayInputStream(billboard.getByteArray()), billboard.getByteArray().length);

                    //If the element is of type Schedule then the "setBinaryStream" method is used to set the parameter
                    } else if (value instanceof Scheduled) {

                        //Casts the element to object type Schedule and uses the "getByteArray()" Schedule method to return the "byte[]" array of the Object
                        //  This "byte[]" can parsed to a ByteArrayInputStream to get the BinaryStream containing all of the Object data
                        Scheduled scheduled = (Scheduled)value;
                        insertQuery.setBinaryStream(index, new ByteArrayInputStream(scheduled.getByteArray()), scheduled.getByteArray().length);
                    }

                    //Increment the index value by 1
                    index++;
                }

                //Executes prepared statement with parameters set
                insertQuery.executeUpdate();

            //If a SQL or data integrity exception is thrown then this exception is propagated
            } catch (SQLException e) {
                throw (Throwable) Exception;
            }

        //Prints a error message saying that no connection has been started
        } else {
            databaseMessage.printError1003();
        }
    }

    /**
     *Attempts to run the given SQL Select query. This method takes the Select statement and executes it, returning a ResultSet.
     * The returned ResultSet is an Object containing all of the relevant data given the query
     * @param query SQL Select query
     * @return ResultSet Object containing all of the relevant data given the query
     * @throws Throwable Exception is thrown if the database manger throws an SQL error
     */
    public ResultSet runSelectQuery(String query) throws Throwable {

        //Initialises a null ResultSet
        ResultSet results = null;

        //Ensures that a connection has been started
        if(connectionStatus) {

            //Attempts to run the given query assigning the result to results
            try {
                results = statement.executeQuery(query);

            //If a SQL exception is thrown then this exception is propagated
            } catch (SQLException e) {
                throw (Throwable) Exception;
            }

        //If not connection has been started a error message is printed.
        } else {
            databaseMessage.printError1003();
        }

        return results;
    }

    /**
     *Attempts to run the given SQL Delete query using the given parameters. This method takes the Delete and executes it, returning the number of rows updated
     * @param query SQL Delete query
     * @return Primitive int representing the number of rows affected by query
     * @throws Throwable Exception is thrown when the database manger throws an SQL error
     */
    public int runDelete(String query) throws Throwable {

        //Ensures that a connection has been started
        if(connectionStatus) {

            //Attempts to run the given query and return the number of rows effected
            try {
                statement.executeQuery(query);
                return statement.getUpdateCount();

            //If an SQL exception is thrown then this exception is propagated
            } catch (Throwable throwable) {
                throw (Throwable) Exception;
            }

        //Prints a error message saying that no connection has been started
        } else {
            databaseMessage.printError1003();
        }

        //Returns 0 if a connection isn't started
        return 0;
    }

    /**
     *Updates the scheduled status of all billboards within the database. This method sets the scheduled status, 0 if not scheduled and 1 if scheduled,
     * of all billboards within the database by searching for each billboards unique ID within the schedule table.
     * @throws Throwable Exception is thrown when the database manger throws an SQL error
     */
    public void updateBillboardStatus() throws Throwable {

        //Ensures that a connection has been started
        if(connectionStatus) {

            //Instantiates an ArrayList containing the scheduled objects within the schedule table and the billboard objects within the billboard table.
            ScheduleDatabase scheduleDatabase = new ScheduleDatabase(properties);
            ArrayList<Scheduled> scheduled = scheduleDatabase.getSchedule();
            BillboardDatabase billboardDatabase = new BillboardDatabase(properties);
            ArrayList<Billboard> billboards = billboardDatabase.getBillboards();

            //Instantiates a empty TreeMap that will be used to store the billboardID and its scheduled status
            TreeMap<Integer, Integer> scheduledValue = new TreeMap<>();

            //Iterates through each billboard object and determines if the billboardID is used in any of elements within scheduled
            for (Billboard billboard: billboards) {

                //Sets the isScheduled value to false
                boolean isScheduled = false;

                //Iterates through the scheduled objects and compares the billboardID of the outer billboard element with the billboardID associated with the element within scheduled
                for (Scheduled schedule: scheduled) {
                    Integer billboardID = billboard.getBillboardID();
                    Integer scheduleID = schedule.getBillboardID();

                    //Compares IDs to determine if they are equal
                    // Assigns true to isScheduled if the IDs are equal
                    if (billboardID.equals(scheduleID)) {
                        isScheduled = true;
                        break;
                    }
                }

                //If isScheduled has a value of true then the billboardID value and 1 are added to the scheduledValue TreeMap
                if(isScheduled) {
                    scheduledValue.put(billboard.getBillboardID(), 1);

                //If isScheduled has a value of false then the billboardID value and 0 are added to the scheduledValue TreeMap
                } else {
                    scheduledValue.put(billboard.getBillboardID(), 0);
                }
            }

            //Integrates through each of the elements of scheduledValue and sets the scheduled status of each billboard using the key (billboardID) and value (status) pair of the TreeMap
            for (Map.Entry<Integer, Integer> value: scheduledValue.entrySet()) {

                //SQL update query required to set the scheduled status of the billboard using its relevant ID
                String sqlUpdate = "UPDATE billboards SET scheduled = " + value.getValue() + " WHERE billboardID = " + value.getKey();
                statement.execute(sqlUpdate);
            }

        //Prints a error message saying that no connection has been started
        } else {
            databaseMessage.printError1003();
        }
    }

//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Start and close connection functions

    /**
     *Creates a connection with database using the URL specified in the "properties.txt" file and subsequently allows the other methods to be run.
     * This method must be called before any others.
     */
    public void startConnection() {

        //Attempts to start a connection with the specified database and set connectionStatus to true
        try {
            connection = DriverManager.getConnection(properties.getDatabaseURL() + '/' + properties.getDatabaseName(), properties.getDatabaseUser(), properties.getDatabasePassword());
            statement = connection.createStatement();
            connectionStatus = true;

        //If an SQL exception occurs then an error message is printed to the console
        } catch (SQLException e) {
            databaseMessage.printError1000(properties.getDatabaseURL());
        }
    }

    /**
     *Closes the current connection with database. Once this method is called not other methods can be run unless the startConnection method is run.
     * To call this method a valid connection must have been started.
     */
    public void closeConnection() {

        //Checks to ensure that a connection has been started
        if(connectionStatus) {

            //Attempts to close the current connection and set connectionStatus to false
            try {
                statement.close();
                connection.close();
                connectionStatus = false;

            //If an SQL exception occurs then an error message is printed to the console
            } catch (SQLException e) {
                databaseMessage.printError1001();
            }

        //If no connection has been started then an error message is printed to the console
        } else {
            databaseMessage.printError1002();
        }
    }

    /**
     *Gets the connection status of the Database Object
     * @return True if a connection has been started
     */
    public boolean getConnectionStatus() {
        return connectionStatus;
    }
}
