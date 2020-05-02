package Server.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

import Server.Database.Database;
import Server.Server;
import Shared.*;

public class BillboardDatabase extends Database {

    private ResultSet results;

    public BillboardDatabase(Properties properties) {
        super(properties);
    }

    public boolean isInTable(Billboard billboard) throws Throwable {
        boolean returnValue = false;
        try {
            String sqlSelect = "select billboardID from billboards where billboardName = \"" + billboard.getName() + "\"";
            results = super.runSelectQuery(sqlSelect);
            returnValue = results.first();
            results.close();
        } catch (SQLException e) {}
        return returnValue;
    }

    public ArrayList<Billboard> getBillboards() throws Throwable {
        super.startConnection();
        ArrayList<Billboard> billboards = new ArrayList<>();
        try {
            String sqlSelect = "SELECT * FROM billboards";
            results = super.runSelectQuery(sqlSelect);

            while(results.next()) {
                Billboard billboard = new Billboard(results.getBytes("billboardObject"));
                billboards.add(billboard);
            }
            results.close();
        } catch (SQLException e) {}
        super.startConnection();
        return billboards;
    }

    public Billboard getBillboard(Integer billboardID) throws Throwable {
        super.startConnection();
        Billboard returnValue = new Billboard();

        try {
            String sqlSelect = "SELECT * FROM billboards WHERE billboardID = " + billboardID;
            results = super.runSelectQuery(sqlSelect);
            results.next();
            returnValue = new Billboard(results.getBytes("billboardObject"));
            results.close();
        } catch (SQLException e) {}
        super.closeConnection();
        return returnValue;
    }

    public void updateDatabase(Billboard billboard) throws Throwable {
        if(!super.getConnectionStatus()) {
            super.startConnection();
        }
        if (isInTable(billboard)) {
            String sqlUpdate = "UPDATE billboards SET billboardObject = ? where billboardName = ?";
            Object[] parameters = new Object[]{billboard, billboard.getName()};
            super.runInsertUpdateQuery(sqlUpdate, parameters, "UPDATE");
        }
        super.closeConnection();
    }

    public void addToDatabase(Billboard billboard) throws Throwable {
        super.startConnection();
        if(!isInTable(billboard)) {
            String sqlInsert = "INSERT INTO billboards (billboardName, billboardObject) VALUES (?, ?)";
            Object[] parameters = new Object[]{billboard.getName(), billboard};
            super.runInsertUpdateQuery(sqlInsert, parameters, "INSERT");
            updateBillboardID(billboard);
        }
        if(super.getConnectionStatus()) {
            super.closeConnection();
        }
    }

    public void updateBillboardID(Billboard billboard) throws Throwable {
        String sqlSelect = "SELECT billboardID FROM billboards WHERE billboardName = \"" + billboard.getName() + "\"";
        ResultSet result = super.runSelectQuery(sqlSelect);

        try {
            result.next();
            billboard.setBillboardID(result.getInt("billboardID"));
            result.close();
            updateDatabase(billboard);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(super.getConnectionStatus()) {
            super.closeConnection();
        }
    }
}
