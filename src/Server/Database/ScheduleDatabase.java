package Server.Database;

import Shared.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class ScheduleDatabase extends Database {

    private ResultSet results;
    private Object Exception;
    private Properties properties;

    public ScheduleDatabase(Properties properties) {
        super(properties);
        this.properties = properties;
    }

    public boolean isInTable(Scheduled scheduled) throws Throwable {
        boolean returnValue = false;
        try {
            String sqlSelect = "select scheduleID from schedule where scheduleID = " + scheduled.getID();
            results = super.runSelectQuery(sqlSelect);
            returnValue = results.next();
            results.close();
        } catch (SQLException ignored) {}
        return returnValue;
    }

    public ArrayList<Scheduled> getSchedule() throws Throwable {

        ArrayList<Scheduled> schedule = new ArrayList<>();

        try{
            String sqlSelect = "SELECT * FROM schedule";
            super.startConnection();
            results = super.runSelectQuery(sqlSelect);
            super.closeConnection();

            while (results.next()) {
                Scheduled scheduled = new Scheduled(results.getBytes("scheduleObject"));
                scheduled.setID(results.getInt("scheduleID"));
                scheduled.setCreatorID(results.getInt("creatorID"));
                scheduled.setBillboardID(results.getInt("billboardID"));
                schedule.add(scheduled);
            }
            results.close();
        } catch (Throwable throwable) {
            throw (Throwable) Exception;
        }

        return schedule;
    }

    public Scheduled getScheduled(String scheduleID) throws Throwable {
        Scheduled scheduled = new Scheduled();

        try {
            String sqlSelect = "SELECT * FROM schedule WHERE scheduleID = " + scheduleID;
            super.startConnection();
            results = super.runSelectQuery(sqlSelect);
            super.closeConnection();

            results.next();
            scheduled = new Scheduled(results.getBytes("scheduleObject"));
            scheduled.setID(results.getInt("scheduleID"));
            scheduled.setCreatorID(results.getInt("creatorID"));
            scheduled.setBillboardID(results.getInt("billboardID"));
            results.close();
        } catch (Throwable throwable) {
            throw (Throwable) Exception;
        }
        return scheduled;
    }

    public boolean addToDatabase(Scheduled scheduled, Integer userID) throws Throwable {
        super.startConnection();
        if(!isInTable(scheduled)) {
            scheduled.setCreatorID(userID);
            String sqlInsert = "INSERT INTO schedule (creatorID, billboardID, scheduleObject, inputDate) VALUES (?, ?, ?, ?)";
            Object[] parameters = new Object[]{userID, scheduled.getBillboardID(), scheduled, LocalDate.now().toString()};
            super.runInsertUpdateQuery(sqlInsert, parameters, "INSERT");

            String sqlUpdate = "UPDATE billboards SET scheduled = ? where billboardID = ?";
            parameters = new Object[]{1, scheduled.getBillboardID()};
            super.runInsertUpdateQuery(sqlUpdate, parameters, "UPDATE");
            super.closeConnection();
            return true;
        } else {
            super.closeConnection();
            return false;
        }
    }

    public void updateDatabase(Scheduled scheduled) throws Throwable {
        super.startConnection();
        if(isInTable(scheduled)) {
            String sqlUpdate = "UPDATE schedule SET scheduleObject = ? where scheduleID = ?";
            Object[] parameters = new Object[]{scheduled, scheduled.getID()};
            super.runInsertUpdateQuery(sqlUpdate, parameters, "UPDATE");
            super.closeConnection();
        } else {
            super.closeConnection();
        }
    }

    public void removeSchedule(Scheduled scheduled) throws Throwable {
        super.startConnection();
        if(isInTable(scheduled)) {
            try {
                Connection connection = DriverManager.getConnection(properties.getDatabaseURL(), properties.getDatabaseUser(), properties.getDatabasePassword());
                Statement statement = connection.createStatement();

                String sqlRemoveSchedule = "DELETE FROM schedule WHERE scheduleID = " + scheduled.getID();
                statement.addBatch(sqlRemoveSchedule);
                statement.executeBatch();

                statement.close();
                connection.close();

                super.updateBillboardStatus();
                super.closeConnection();
            } catch (Throwable throwable) {
                super.closeConnection();
                throw (Throwable) Exception;
            }
        } else {
            super.closeConnection();
            throw (Throwable) Exception;
        }
    }
}
