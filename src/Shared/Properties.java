package Shared;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class Properties {

    private String serverAddress;
    private String serverPort;
    private String databaseURL;
    private String databaseName;
    private String databaseUser;
    private String databasePassword;

    public Properties() throws FileNotFoundException {
        readPropertiesFile();
    }

    private void readPropertiesFile() throws FileNotFoundException {
        Scanner fileScanner = new Scanner(new File("././externalResources/Properties.txt"));

        serverAddress = fileScanner.nextLine().substring(16);
        serverPort = fileScanner.nextLine().substring(13);
        databaseURL = fileScanner.nextLine().substring(14);
        databaseUser = fileScanner.nextLine().substring(19);
        databasePassword = fileScanner.nextLine().substring(19);
        databaseName = databaseURL.split("/")[3];

        System.out.println("Properties file read");
    }

    public String getDatabaseURL() {
        return databaseURL;
    }

    public void setDatabaseURL(String url) {this.databaseURL = url;}

    public String getDatabaseUser() {
        return databaseUser;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public String getServerPort() {
        return serverPort;
    }

    public String getDatabaseName() { return databaseName; }
}
