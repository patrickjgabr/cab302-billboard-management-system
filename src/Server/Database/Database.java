package Server.Database;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import Server.ConsoleMessage.DatabaseMessage;
import Shared.*;

public class Database {

    private Properties properties;
    private Connection connection;
    private Statement statement;
    private ResultSet results;
    public DatabaseMessage databaseMessage;
    private boolean connectionStatus;
    private ArrayList<String> tables;
    private Object Exception;

    public Database(Properties properties) {
        this.databaseMessage = new DatabaseMessage();
        this.properties = properties;
    }

//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Check database

    public boolean checkConfiguration() {
        databaseMessage.printGeneral("DATABASE", "Configuration test started", 75);
        if(connectionStatus) {
            if(tableExist()) {
                databaseMessage.printGeneral("DATABASE", "Configuration test passed", 75);
                return true;
            } else {
                databaseMessage.printGeneral("DATABASE", "Configuration test failed", 75);
                databaseMessage.printError1004();
                return confirmReset();
            }
        } else {
            databaseMessage.printError1003();
            return false;
        }
    }


    private boolean tableExist() {
        tables = getTables();
        if(tables.contains("users") && tables.contains("billboards") && tables.contains("sessions") && tables.contains("schedule")) {
            return true;
        }
        return false;
    }

    private ArrayList<String> getTables() {
        ArrayList<String> tables = new ArrayList<>();

        try {
            ResultSet resultSet = statement.executeQuery("SHOW TABLES");
            while(resultSet.next()) {
                tables.add(resultSet.getString("Tables_in_" + properties.getDatabaseName()));
            }
            databaseMessage.printGeneral("DATABASE", "SHOW TABLES Successful", 50);
            resultSet.close();
        } catch (SQLException e) {
            databaseMessage.printWarning("Database command \"SHOW TABLES\" Failed", 50);
        }

        return tables;
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Generate new database

    private boolean confirmReset() {
        System.out.print("Would you like to reset the database to the newest version (YES / NO): ");
        Scanner userInput = new Scanner(System.in);
        String input = userInput.nextLine();

        boolean returnValue;

        if (input.equals("YES")) {
            returnValue = generateNewDatabase();
        } else if (input.equals("NO")) {
            returnValue = false;
        } else {
            System.out.println("Oops invalid input... Try Again!");
            returnValue = confirmReset();
        }
        return  returnValue;
    }

    private boolean generateNewDatabase() {
        databaseMessage.printGeneral("DATABASE", "Creating new database", 75);
        boolean returnValue = dropAllTables();

        if (returnValue) {
            returnValue = createAllTables();
        }

        if(returnValue) {
            databaseMessage.printGeneral("DATABASE", "Successfully created new database", 75);
        } else {
            databaseMessage.printWarning("Database Failed to create new database", 75);
        }

        return returnValue;
    }

    private boolean dropAllTables() {
        boolean correctConfiguration = true;

        if(tables.size() != 0) {
            for (String table: tables) {
                try {
                    statement.addBatch("DROP TABLES " + table);
                } catch (SQLException e) {
                    correctConfiguration = false;
                }
            }

            try {
                statement.executeBatch();
                databaseMessage.printGeneral("DATABASE", "Dropping all tables", 50);
            } catch (SQLException e) {
                databaseMessage.printError1005();
                correctConfiguration = false;
            }
        }

        return correctConfiguration;
    }

    private boolean createAllTables() {
        boolean correctConfiguration = true;
        String tablesSQL = "";

        try {
            FileReader fileReader = new FileReader("./././externalResources/database.sql");
            BufferedReader reader = new BufferedReader(fileReader);
            String currentLine = "";

            while((currentLine = reader.readLine()) != null) {
                tablesSQL += currentLine;
            }

        } catch (IOException e) {
            databaseMessage.printError1006();
        }

        String[] tableStatement = tablesSQL.split(";");
        try {
            for (String statement: tableStatement) {
                this.statement.addBatch(statement);
            }
            statement.executeBatch();
            databaseMessage.printGeneral("DATABASE", "CREATE statements successful", 50);
            correctConfiguration = createRootUser();

            if(correctConfiguration) {
                checkConfiguration();
            }
        } catch (SQLException e) {
            databaseMessage.printWarning("Database \"CREATE\" statements failed", 50);
            correctConfiguration = false;
        }

        return correctConfiguration;
    }

    private boolean createRootUser() {
        ArrayList<Integer> permissions = new ArrayList<>(Arrays.asList(1, 1, 1, 1));
        User rootUser = new User("root", "c13866ce9e42e90d3cf50ded2dc9e00194ffc4ad4e15865cd1b368f168944646", permissions, 100000,"y6WOb24rUAINN6KoUQ7lWNeniyTpsxPaZqzEhvAMzSqE5MrIx2kJS9TaTm0rl96n");

        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO users VALUES (?,?,?)");
            statement.clearParameters();
            statement.setInt(1, 100000);
            statement.setString(2, "root");
            statement.setBinaryStream(3, new ByteArrayInputStream(rootUser.getByteArray()));
            statement.executeUpdate();
            databaseMessage.printGeneral("DATABASE", "Root user added to users table", 50);
            return true;
        } catch (SQLException e) {
            databaseMessage.printWarning("DATABASE failed to add root user", 50);
            return false;
        }
    }


    public void runInsertUpdateQuery(String query, Object[] values, String type) throws Throwable {
        if(connectionStatus) {
            try {
                PreparedStatement insertQuery = connection.prepareStatement(query);
                insertQuery.clearParameters();

                int index = 1;
                for (Object value: values) {
                    if (value instanceof String) {
                        insertQuery.setString(index, (String) value);
                    } else if (value instanceof Integer) {
                        insertQuery.setInt(index, (Integer) value);
                    } else if (value instanceof User) {
                        User user = (User)value;
                        insertQuery.setBinaryStream(index, new ByteArrayInputStream(user.getByteArray()), user.getByteArray().length);
                    } else if (value instanceof Billboard) {
                        Billboard billboard = (Billboard)value;
                        insertQuery.setBinaryStream(index, new ByteArrayInputStream(billboard.getByteArray()), billboard.getByteArray().length);
                    }

                    index++;
                }
                insertQuery.executeUpdate();
                databaseMessage.printGeneral("DATABASE", type + " was successful", 50);
            } catch (SQLException e) {
                databaseMessage.printWarning("Database " + type + " statement failed", 50);
                throw (Throwable) Exception;
            }
        } else {
            databaseMessage.printError1003();
        }
    }

    public ResultSet runSelectQuery(String query) throws Throwable {
        if(connectionStatus) {
            try {
                results = statement.executeQuery(query);

                databaseMessage.printGeneral("DATABASE", "SELECT Successful", 50);
            } catch (SQLException e) {
                databaseMessage.printWarning("Database \"SELECT\" failed", 50);
                throw (Throwable) Exception;
            }
        } else {
            databaseMessage.printError1003();
            throw (Throwable) Exception;
        }

        return results;
    }

    public void runDelete(String query) throws Throwable {
        if(connectionStatus) {
            try {
                statement.executeQuery(query);
                databaseMessage.printGeneral("DATABASE", "DELETE Successful", 50);
            } catch (Throwable throwable) {
                databaseMessage.printWarning("Database \"DELETE\" failed", 50);
                throw (Throwable) Exception;
            }
        } else {
            databaseMessage.printError1003();
        }
    }


//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Start and close connection functions
    public void startConnection() {
        try {
            connection = DriverManager.getConnection(properties.getDatabaseURL(), properties.getDatabaseUser(), properties.getDatabasePassword());
            statement = connection.createStatement();
            databaseMessage.printGeneral("DATABASE", "Connection started with " + properties.getDatabaseURL() , 100);
            connectionStatus = true;
        } catch (SQLException e) {
            databaseMessage.printError1000(properties.getDatabaseURL());
        }
    }

    public void closeConnection() {
        if(connectionStatus) {
            try {
                statement.close();
                connection.close();
                connectionStatus = false;
                databaseMessage.printGeneral("DATABASE", "Connection closed with " + properties.getDatabaseURL() , 100);
            } catch (SQLException e) {
                databaseMessage.printError1001();
            }
        } else {
            databaseMessage.printError1002();
        }
    }

    public boolean getConnectionStatus() {
        return connectionStatus;
    }
}
