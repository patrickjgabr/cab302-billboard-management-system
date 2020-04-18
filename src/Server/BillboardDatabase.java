package Server;

import java.sql.*;
import java.util.ArrayList;

import Shared.*;

public class BillboardDatabase extends Database {

    private ResultSet results;

    public BillboardDatabase(Properties properties) {
        super(properties);
    }

    public boolean isInTable(Billboard billboard) {
        boolean returnValue = false;

        try {
            String sqlSelect = "select billboardID from billboards where billboardID = " + billboard.getBillboardID();
            results = super.runSelectQuery(sqlSelect);
            returnValue = results.first();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return returnValue;
    }

    public ArrayList<Billboard> getBillboards() {
        ArrayList<Billboard> billboards = new ArrayList<>();

        try {
            String sqlSelect = "SELECT * FROM billboards";
            results = super.runSelectQuery(sqlSelect);

            while(results.next()) {
                Billboard billboard = resultsSetToBillboard(results);
                billboards.add(billboard);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return billboards;
    }

    public Billboard getBillboard(boolean byID, String value) {

        Billboard returnValue = new Billboard();

        try {
            String sqlSelect;

            if(byID) {
                sqlSelect = "SELECT * FROM billboards WHERE billboardID = " + value;
            }
            else {
                sqlSelect = "SELECT * FROM billboards WHERE name = \"" + value + "\"";
            }

            results = super.runSelectQuery(sqlSelect);
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

    public void updateDatabase(Billboard billboard) {
        if (isInTable(billboard)) {
            String sqlUpdate = "update billboards set name = " + "\"" + billboard.getName()+ "\", " +
                        "imageUrl = " + "\"" + billboard.getPictureLink() + "\", " +
                        "messageText = " + "\"" + billboard.getMessageText() + "\", " +
                        "messageTextColour = " + "\"" + billboard.getMessageTextColour() + "\", " +
                        "backgroundColour = " + "\"" + billboard.getBackgroundColour() + "\", " +
                        "informationText = " + "\"" + billboard.getInformationText() + "\", " +
                        "informationTextColour = " + "\"" + billboard.getBackgroundColour() + "\"" +
                        "where billboardID = " + billboard.getBillboardID();

            super.runUpdateQuery(sqlUpdate);
        }
    }

    public void addToDatabase(Billboard billboard) {
        if(!isInTable(billboard)) {
            String sqlInsert = "insert into billboards values ( NULL, " + "\"" + billboard.getCreatorName() + "\", " +
                                                                    "\"" + billboard.getName() + "\", " +
                                                                    "\"" + billboard.getPictureLink() + "\", " +
                                                                    "\"" + billboard.getMessageText() + "\", " +
                                                                    "\"" + billboard.getMessageTextColour() + "\", " +
                                                                    "\"" + billboard.getBackgroundColour() + "\", " +
                                                                    "\"" + billboard.getInformationText() + "\", " +
                                                                    "\"" + billboard.getInformationTextColour() + "\")";
            super.runInsertQuery(sqlInsert);
        }
    }
}
