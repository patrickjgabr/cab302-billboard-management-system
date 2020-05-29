package Server.ConsoleMessage;


import Shared.Properties;

public class DatabaseMessage extends ConsoleMessage {

    public DatabaseMessage() {
        super();
    }

    public void printGeneral(String header, String message, Integer lineSize) {
        super.printGeneral(header, message, lineSize);
    }

    public void printWarning(String message, Integer lineSize) {
        super.printWarning(message, lineSize);
    }

    public void printError1000(String databaseURL) {
        super.printError(1000, "Failed to connect with " + databaseURL,
                "Couldn't connect with specified database in \"Properties.txt\" using the given username and" +
                        " password",
                "Ensure the above database exists inside MariaDB and that the specified credentials are able to connect" +
                        "to MariaDB directly using the Command line or HeidiSQL.",
                100);
    }

    public void printError1001() {
        super.printError(1001, "Database close failed",
                "Unexpected error occurred. Couldn't close connection with database",
                "Restart the server", 100);
    }

    public void printError1002() {
        super.printError(1002, "Database close failed. No connection started",
                "Database connection cannot be closed when one has not be started. A connection needs to exist for one to be closed.",
                "Ensure that startConnection() has been used.", 100);
    }

    public void printError1003() {
        super.printError(1003, "Database operation failed. No connection started.",
                "Database connection required to perform operations on it.",
                "Ensure that startConnection() has been used and didn't produce an error.", 100);
    }

    public void printError1004() {
        super.printError(1004, "Database table configuration incorrect",
                "Not all tables required for the server to run exist within the database. You may be running on an old copy of the database." +
                        " The database must contain the following tables: billboards, permissions, schedule, sessions and users.",
                "The database will now be reset back to its default configuration. This will clear all data and insert a user \"root\" with all permissions and password \"password\"",
                100);
    }

    public void printError1005() {
        super.printError(1005, "Database failed to drop existing tables during reset",
                "While resetting the database the \"DROP TABLE table_name\" failed to drop the existing tables to ensure a full reset. If these tables aren't dropped " +
                        "then the database cannot be configured correctly and thus the server cannot run.",
                "Please check the database to see the current database state and reset the server.",
                100);
    }

    public void printError1006() {
        super.printError(1006, "Database failed to read \"database.sql\" file",
                "To create a new copy of the database the \"database.sql\" file is required. " +
                        "This file contains the SQL script which creates the new database required for the server" +
                        " to run correctly",
                "Please check a copy of the database.sql file is within the \"externalResources\" folder", 100);
    }
}
