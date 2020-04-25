package Server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
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
            statement.close();
            connection.close();
            System.out.println("Database connection closed... ");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Database close Failed: " + properties.getDatabaseURL());
        }
    }

    // ----------------------------------- CREATING FUNCTIONS ------------------------------------------------

    public void checkDatabase() {

        if (tablesExist() ) {//&& tablesCorrect()) {
            System.out.println("Database check: Passed");
        } else {
            System.out.println("Database check: Failed");
        }
    }

    private boolean tablesExist() {
        startConnection();
        String sqlShow = "SHOW TABLES";
        ArrayList<String> tables = new ArrayList<>();
        try {
            results = statement.executeQuery(sqlShow);
            while(results.next()) {
                tables.add(results.getString("Tables_in_" + properties.getDatabaseName()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeConnection();


        boolean correctConfiguration = true;

        if (!tables.contains("billboards") || !tables.contains("permissions") || !tables.contains("schedule") || !tables.contains("sessions") || !tables.contains("users")) {
            correctConfiguration = false;
            System.out.println("Database error: Database missing table/s");
            dropAllTables(tables);
        }
        return  correctConfiguration;
    }

    private void dropAllTables(ArrayList<String> tables) {
        System.out.println("Dropping all tables... ");
        startConnection();
        for (String table: tables) {
            String dropTable = "DROP TABLE " + table;
            try {
                statement.executeQuery(dropTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        closeConnection();
    }

    private boolean tablesCorrect() {
        return true;
    }

    // ------------------------------- CREATING FUNCTIONS ------------------------------------

    private void createDatabase() {
        startConnection();
        System.out.println("Creating database... ");
        createNewDatabase();
        createTables();
        System.out.println("Database created...");
        closeConnection();

    }

    private  void createNewDatabase() {
        try {
            String sqlDrop = "DROP DATABASE IF EXISTS " + properties.getDatabaseName() + "; ";
            statement.executeQuery(sqlDrop);
            String sqlCreate =  "CREATE DATABASE " + properties.getDatabaseName() + ";";
            statement.executeQuery(sqlCreate);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables() {
        String tablesSQL = "";

        try {
            FileReader fileReader = new FileReader("././externalResources/database.sql");
            BufferedReader reader = new BufferedReader(fileReader);
            String currentLine = "";

            while((currentLine = reader.readLine()) != null) {
                tablesSQL += currentLine;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] tableStatement = tablesSQL.split(";");
        try {
            statement.executeQuery("USE " + properties.getDatabaseName());
            for (String statement: tableStatement) {
                this.statement.executeQuery(statement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
