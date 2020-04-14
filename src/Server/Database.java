package Server;

import java.sql.*;
import Shared.*;

public class Database {

    private Properties properties;
    private Connection connection;
    private Statement statement;
    private ResultSet results;

    public Database(Properties properties) {
        this.properties = properties;
    }

    public void runInsertQuery(String Query) {
        try {
            startConnection();
            statement.executeQuery(Query);
            closeConnection();

            System.out.println("Insert Successful");
        } catch (SQLException e) {
            System.out.println("Insert Failed: " + Query);
        }
    }

    public void runUpdateQuery(String Query) {
        try {
            startConnection();
            statement.executeQuery(Query);
            closeConnection();

            System.out.println("Update Successful");
        } catch (SQLException e) {
            System.out.println("Update Failed: " + Query);
        }
    }

    public ResultSet runSelectQuery(String Query) {
        try {
            startConnection();
            results = statement.executeQuery(Query);
            closeConnection();

            System.out.println("Select Successful");
        } catch (SQLException e) {
            System.out.println("Select statement Failed: " + Query);
        }
        return results;
    }



    private void startConnection() {
        try {
            System.out.println("Database connection started...");
            connection = DriverManager.getConnection(properties.getDatabaseURL(), properties.getDatabaseUser(), properties.getDatabasePassword());
            statement = connection.createStatement();
        } catch (SQLException e) {
            System.out.println("Database connection Failed: " + properties.getDatabaseURL());
        }
    }

    private void closeConnection() {
        try {
            connection.close();
            System.out.println("Database connection closed... ");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Database close Failed: " + properties.getDatabaseURL());
        }
    }
}
