package Server.Database;

import Shared.Billboard;
import Shared.Properties;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BillboardDatabase extends Database {

    private ResultSet results;
    private Object Exception;

    public BillboardDatabase(Properties properties) {
        super(properties);
    }

    private boolean isInTable(Billboard billboard) throws Throwable {
        boolean returnValue = false;
        try {
            String sqlSelect = "select billboardID from billboards where billboardName = \"" + billboard.getName() + "\"";
            results = super.runSelectQuery(sqlSelect);
            returnValue = results.first();
            results.close();
        } catch (SQLException ignored) {}
        return returnValue;
    }

    public ArrayList<Billboard> getBillboards() throws Throwable {

        ArrayList<Billboard> billboards = new ArrayList<>();
        try {
            String sqlSelect = "SELECT * FROM billboards";
            super.startConnection();
            results = super.runSelectQuery(sqlSelect);
            super.closeConnection();

            while(results.next()) {
                Billboard billboard = new Billboard(results.getBytes("billboardObject"));
                billboard.setScheduled(results.getInt("scheduled"));
                billboard.setBillboardID(results.getInt("billboardID"));
                billboards.add(billboard);
            }
            results.close();
        } catch (Throwable throwable) {
            throw (Throwable) Exception;
        }

        return billboards;
    }

    public Billboard getBillboard(String value, boolean id) throws Throwable {
        super.startConnection();
        Billboard returnValue = new Billboard();

        try {
            String sqlSelect;
            if(id) {
                sqlSelect = "SELECT * FROM billboards WHERE billboardID = " + value;
            } else {
                sqlSelect = "SELECT * FROM billboards WHERE billboardName = \"" + value + "\"";
            }

            results = super.runSelectQuery(sqlSelect);
            results.next();
            returnValue = new Billboard(results.getBytes("billboardObject"));
            returnValue.setBillboardID(results.getInt("billboardID"));
            returnValue.setScheduled(results.getInt("scheduled"));
            results.close();
        } catch (SQLException ignored) { }
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

    public boolean addToDatabase(Billboard billboard, Integer userID) throws Throwable {
        super.startConnection();
        if(!isInTable(billboard)) {
            String sqlInsert = "INSERT INTO billboards (creatorID, billboardName, billboardObject) VALUES (?, ?, ?)";
            Object[] parameters = new Object[]{userID, billboard.getName(), billboard};
            super.runInsertUpdateQuery(sqlInsert, parameters, "INSERT");
            super.closeConnection();
            return true;
        } else {
            super.closeConnection();
            return false;
        }
    }
}
