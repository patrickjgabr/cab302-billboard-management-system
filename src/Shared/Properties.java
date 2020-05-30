package Shared;

import java.io.*;

/**
 * The Properties class provides a storage structure for the information contained within the properties.txt and db.props files contained within externalResources.
 * Each of the properties in this class relate to a line within the properties.txt file
 */
public class Properties {

    private String serverAddress;
    private String serverPort;
    private String databaseURL;
    private String databaseName;
    private String databaseUser;
    private String databasePassword;
    private Object Exception;

    /**
     * Default constructor used to instantiate a Sever Object
     */
    public Properties() throws Throwable {
        readPropertiesFile();
    }

    //Function which reads the properties contained within the Properties.txt and db.props files and assigns the values to the properties
    private void readPropertiesFile() throws Throwable {

        //Instantiates a Properties
        java.util.Properties properties = new java.util.Properties();

        //Attempts to read the properties.txt and db.props file from external resources and assign their values to the properties of the Object
        try {

            //Reads the Properties.txt file and loads the values into properties
            FileInputStream propertiesFile = new FileInputStream("./externalResources/Properties.txt");
            properties.load(propertiesFile);

            //Gets the server address and port from properties
            serverAddress = properties.getProperty("server.address");
            serverPort = properties.getProperty("server.port");

            //Reads the db.props file and the loads the values into properties
            FileInputStream db = new FileInputStream("./externalResources/db.props");
            properties.load(db);

            //Gets the database properties URL, name, user, password from properties
            databaseURL = properties.getProperty("jdbc.url");
            databaseName = properties.getProperty("jdbc.schema");
            databaseUser = properties.getProperty("jdbc.userName");
            databasePassword = properties.getProperty("jdbc.password");

        } catch (IOException e) {
            throw (Throwable) Exception;
        }
    }

    /**
     * Returns the database URL
     * @return Database URL
     */
    public String getDatabaseURL() {
        return databaseURL;
    }

    /**
     * Returns the username of the user required to connect to the database
     * @return Database username
     */
    public String getDatabaseUser() {
        return databaseUser;
    }

    /**
     * Returns the password of the user required to connect to the database
     * @return Database password
     */
    public String getDatabasePassword() {
        return databasePassword;
    }

    /**
     * Returns the server address required for client connection
     * @return Server address
     */
    public String getServerAddress() {
        return serverAddress;
    }

    /**
     * Returns the port the server will listen on
     * @return Server port
     */
    public String getServerPort() {
        return serverPort;
    }

    /**
     * Returns the name of the database to connect to
     * @return Database name
     */
    public String getDatabaseName() { return databaseName; }
}
