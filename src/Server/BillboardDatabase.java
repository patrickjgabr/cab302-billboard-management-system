package Server;

import java.sql.*;
import Shared.*;

public class BillboardDatabase{


    private Billboard billboard;
    private Connection connection;
    private Statement statement;
    private ResultSet results;

    public BillboardDatabase(Billboard billboard) {this.billboard = billboard;}

    public BillboardDatabase() {}

    public boolean isInTable() {
        boolean returnValue = false;

        try {
            connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/applicationdatabase", "root", "password");
            statement = connection.createStatement();

            String sqlSelect = "select billboardID from billboards where billboardID = " + billboard.getBillboardID();
            results = statement.executeQuery(sqlSelect);
            returnValue = results.first();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return returnValue;
    }

    public Billboard[] getBillboards() {
        Billboard[] billboards = {};

        try {
            connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/applicationdatabase", "root", "password");
            statement = connection.createStatement();
            String sqlSelect = "SELECT * FROM billboards";
            results = statement.executeQuery(sqlSelect);

            results.last();
            billboards = new Billboard[results.getRow()];
            results.beforeFirst();

            Integer index = 0;
            while(results.next()) {
                billboards[index] = resultsSetToBillboard(results);
                index++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return billboards;
    }

    public Billboard getBillboard(boolean byID, String value) {

        Billboard returnValue = new Billboard();

        try {

            connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/applicationdatabase", "root", "password");
            statement = connection.createStatement();
            String sqlSelect;

            if(byID) {
                sqlSelect = "SELECT * FROM billboards WHERE billboardID = " + value;
            }
            else {
                sqlSelect = "SELECT * FROM billboards WHERE name = \"" + value + "\"";
            }

            results = statement.executeQuery(sqlSelect);
            results.next();

            returnValue = resultsSetToBillboard(results);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return returnValue;

    }

    private Billboard resultsSetToBillboard(ResultSet resultSet) {
        Billboard returnValue = new Billboard();

        try {
            Integer billboardID = resultSet.getInt("billboardID");
            String creatorName = resultSet.getString("creatorName");
            String name = resultSet.getString("name");
            String imageUrl = resultSet.getString("imageUrl");
            String messageText = resultSet.getString("messageText");
            String messageTextColour = resultSet.getString("messageTextColour");
            String backgroundColour = resultSet.getString("backgroundColour");
            String informationText = resultSet.getString("informationText");
            String informationTextColour = resultSet.getString("informationTextColour");


            returnValue = new Billboard(creatorName, name, imageUrl, messageText, messageTextColour, backgroundColour, informationText, informationTextColour);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return returnValue;
    }

    public void updateDatabase() {
        if (isInTable()) {
            try {
                connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/applicationdatabase", "root", "password");
                statement = connection.createStatement();

                String sqlUpdate = "update billboards set name = " + "\"" + billboard.getName()+ "\", " +
                        "imageUrl = " + "\"" + billboard.getPictureLink() + "\", " +
                        "messageText = " + "\"" + billboard.getMessageText() + "\", " +
                        "messageTextColour = " + "\"" + billboard.getMessageTextColour() + "\", " +
                        "backgroundColour = " + "\"" + billboard.getBackgroundColour() + "\", " +
                        "informationText = " + "\"" + billboard.getInformationText() + "\", " +
                        "informationTextColour = " + "\"" + billboard.getBackgroundColour() + "\"" +
                        "where billboardID = " + billboard.getBillboardID();

                statement.executeQuery(sqlUpdate);
                connection.close();
                System.out.println("Billboard update");
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        else {
            System.out.println("Billboard not updated");
        }

    }

    public void addToDatabase() {
        if(!isInTable()) {
            try {
                connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/applicationdatabase", "root", "password");
                statement = connection.createStatement();

                String sqlInsert = "insert into billboards values ( NULL, " + "\"" + billboard.getCreatorName() + "\", " +
                                                                    "\"" + billboard.getName() + "\", " +
                                                                    "\"" + billboard.getPictureLink() + "\", " +
                                                                    "\"" + billboard.getMessageText() + "\", " +
                                                                    "\"" + billboard.getMessageTextColour() + "\", " +
                                                                    "\"" + billboard.getBackgroundColour() + "\", " +
                                                                    "\"" + billboard.getInformationText() + "\", " +
                                                                    "\"" + billboard.getInformationTextColour() + "\")";
                statement.executeQuery(sqlInsert);
                connection.close();
                System.out.println("Billboard added");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("Billboard not added");
        }
    }
}
