package Server.Database;

import Server.ConsoleMessage.DatabaseMessage;
import Shared.Properties;
import Shared.User;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

/**
 * The SessionDatabase class extends Database and provides a method set which handles all database actions relevant to the session table.
 */
public class SessionDatabase extends Database {

    private Properties properties;
    private DatabaseMessage databaseMessage;

    /**
     * Default constructor used to instantiate a SessionDatabase Object
     * @param properties Properties object containing all of the database connection information contained in "properties.txt"
     */
    public SessionDatabase(Properties properties) {
        super(properties);
        databaseMessage = new DatabaseMessage();
        this.properties = properties;
    }

    /**
     * Method which sets the a session for the given userName. This method uses a prepared SQL Insert statement to insert a new row containing the session information for the given userName into the database.
     * Session tokens that are generated are random and entire within 24 hours of this method being called.
     * @param userName
     * @return
     */
    public String setSession(String userName) {

        //Instantiate a UserDatabase class
        UserDatabase userDatabase = new UserDatabase(properties);

        //Attempt to remove a existing session for the given userName and then generate a new session token and insert it into the database
        try {

            //Get the user that corresponds to the given userName
            User user = userDatabase.getUser(userName, true);

            //Remove the existing session if present from the database
            this.removeSession(Integer.toString(user.getUserID()), false);

            //Generate a the expiry time and session token and assign their value to expireTime and token respectively
            String[] expireTime = generateExpireTime();
            String token = generateToken();

            //Prepared SQL insert statement which takes four parameters
            String insetQuery = "INSERT INTO sessions (sessionToken, userID, expireDate, expireTime) VALUES (?, ?, ?, ?)";

            //Array of parameters which match the order of the prepared statement
            Object[] parameters = new Object[]{token, user.getUserID(), expireTime[0], expireTime[1]};

            //Starts a connection with the database then runs the insert query and close the connection
            super.startConnection();
            super.runInsertUpdateQuery(insetQuery, parameters);
            super.closeConnection();

            //Print success message to the console and return the token
            databaseMessage.printGeneral("DATABASE", "Session set for \"" + userName + "\"", 50);
            return token;


        //If the parent (super) throws an exception then that exception is propagated and a error message printed to the console
        } catch (Throwable throwable) {
            databaseMessage.printWarning("Database failed to set session.        User \"" + userName + "\" already has a session", 50);
            super.closeConnection();
            return "";
        }

    }

    //Function which generates the expiry given the current time
    private String[] generateExpireTime() {

        //Get the LocalDateTime current instance plus one day
        LocalDateTime time = LocalDateTime.now().plusDays(1);

        //Format time to a string value which the database will accept and return the resulting String[]
        String expireDate = (time.getYear() + "-" + time.getMonthValue() + "-" + time.getDayOfMonth());
        String expireTime = (time.getHour() + ":" + time.getMinute() + ":" + time.getSecond());
        return new String[]{expireDate, expireTime};
    }

    //Function which generates and return a random token of length 64
    private String generateToken() {

        //Instantiate a empty StringBuilder and token character set
        StringBuilder tokenBuilder = new StringBuilder(64);
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz!@#$%^&*()_+-={}[]:<>?,./";

        //Iterate 64 times each time picking a random character an adding it to the tokenBuilder
        for (int i = 0; i < 64; i++) {

            //Generate random number
            int index = (int)(characters.length()* Math.random());

            //Add character at position index to the tokenBuilder
            tokenBuilder.append(characters.charAt(index));
        }

        //Return the generated token as a String
        return tokenBuilder.toString();
    }

    /**
     * Method which checks for a given taken within session table and ensures that it hasn't expired if present. This method uses a SQL select statement to get the specific token from the database.
     * As the database has a uniqueness constraint on the sessionToken only one row will match the given value.
     * @param token String representing the token
     * @return  True if the token exists and is valid
     */
    public boolean checkSession(String token) {

        //If no connection has been started with the database then a connection must be started
        if(!super.getConnectionStatus()) {
            super.startConnection();
        }

        //Attempts to get the row from the database containing the given token and check its validity.
        try {

            //SQL select string which selects the session row from the database containing this token
            String select = "SELECT * FROM sessions WHERE sessionToken = \"" + token + "\"";

            //Parent runs SQL select statement and assigns the result to result
            ResultSet result = super.runSelectQuery(select);

            //If the cursor can move to the first position and therefore result isn't empty evaluate the expiry time
            if(result.next()) {

                //Instantiate a returnValue as false
                boolean returnValue = false;

                //Get the expiry time from the database as a single String value
                String expireTimeString = result.getString("expireDate") + " " + result.getString("expireTime");

                //Generate a Calender Object for both the current time and expiry time
                Calendar expireCalender = getTokenExpireTime(expireTimeString);
                Calendar currentCalender = Calendar.getInstance();

                //If the expiry time when compared to the current time returns a value of 1 then the token is valid so return true
                if(expireCalender.compareTo(currentCalender) > 0) {
                    returnValue = true;
                }

                //Close database connection and return false
                super.closeConnection();
                return returnValue;

            } else {
                //Close database connection and return false
                super.closeConnection();
                return false;
            }

        } catch (Throwable throwable) {
            //Close database connection and return false
            super.closeConnection();
            return false;
        }
    }

    //Function which returns a Calender object set to the date and time of the input string
    private Calendar getTokenExpireTime(String timeString) {

        //Instantiate a DateTimeFormatter with the pattern that the database outputs
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        //Instantiate a LocalDateTime using the given value and the dateTimeFormatter
        LocalDateTime expireTime = LocalDateTime.parse(timeString, dateTimeFormatter);

        //Instantiate a Calender object and set its time to those of the expireTime
        Calendar calendarExpire = Calendar.getInstance();
        calendarExpire.set(Calendar.YEAR, expireTime.getYear());
        calendarExpire.set(Calendar.MONTH, expireTime.getMonthValue());
        calendarExpire.set(Calendar.DAY_OF_YEAR, expireTime.getDayOfYear());
        calendarExpire.set(Calendar.HOUR_OF_DAY, expireTime.getHour());
        calendarExpire.set(Calendar.MINUTE, expireTime.getMinute());
        calendarExpire.set(Calendar.SECOND, expireTime.getSecond());

        //Return Calender object set to the expiry time
        return calendarExpire;
    }

    /**
     * Method which removes the a row from the database containing a specific token or userID. This method uses a SQL Delete statement to remove rows containing the given token or userID from the database.
     * As the database has a unique constraint on sessionToken and userID only one row can be removed.
     * @param value Either a String of the token or userID
     * @param token True if removing by token
     * @return
     */
    public boolean removeSession(String value, boolean token) {

        //Attempt to remove the row from the database containing the specific value
        try {
            //Instantiate a SQL delete string
            String deleteSQL;

            //If the given value is the token them set the SQL delete string to delete by this value
            if(token) {
                deleteSQL = "DELETE FROM sessions WHERE sessionToken = \"" + value + "\"";

            //If the given value is the userID them set the SQL delete string to delete by this value
            } else {
                deleteSQL = "DELETE FROM sessions WHERE userID = " + value + "";
            }

            //Starts a connection with the database then runs the delete query and then closes the connection
            super.startConnection();
            super.runDelete(deleteSQL);
            return true;

        } catch (Throwable ignored) { }

        //Closes the connection and returns false
        super.closeConnection();
        return false;
    }

    /**
     *Method which returns the User associated with a particular token. This uses a SQL Select query to get the userID that corresponds to the given token and then queries the UserDatabase to get the corresponding user.
     * As the database has a unique constraint on sessionToken only one row can be selected and therefore only one possible User returned.
     * @param session String of users token
     * @return
     */
    public User getUserFromSession(String session) {

        //Attempts to select the row containing the given session and then use the corresponding userID to get the referenced User from the user table
        try {

            //SQL select statement which gets the row corresponding to the given session
            String sqlSelect = "SELECT * FROM sessions WHERE sessionToken = \"" + session + "\"";

            //Starts a connection with the database then runs the select query and assigns the result to resultSet and then closes the connection
            super.startConnection();
            ResultSet resultSet = super.runSelectQuery(sqlSelect);
            super.closeConnection();

            //If a resultSet has an element and results.next() returns true then get the referenced User from the user table
            if(resultSet.next()) {

                //Instantiate a UserDatabase object and return the User from the users table with the given userID
                UserDatabase userDatabase = new UserDatabase(properties);
                return userDatabase.getUser(resultSet.getString("userID"), false);
            }

        } catch (Throwable ignored) {}

        //Return a empty user
        return new User();
    }
}
