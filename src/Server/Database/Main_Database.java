package Server.Database;

import ControlPanel.Client;
import Server.Database.Database;
import Server.Server;
import Shared.*;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class Main_Database {

    public static void main(String[] args) {

        try {
            Properties properties = new Properties();

            //UserDatabase userDatabase = new UserDatabase(properties);
            ArrayList<Integer> perms = new ArrayList<>();
            perms.add(0,0);
            perms.add(1,0);
            perms.add(2,0);
            perms.add(3,0);
            User newUser = new User("user2", "pass", perms, 0, "test");
            newUser.setUserID(100003);
            //userDatabase.removeUser(newUser);

            //Billboard billboard = new Billboard("user2", "Test2", "NA", "This is the main test", "", "", "info text", "");
            //BillboardDatabase billboardDatabase = new BillboardDatabase(properties);
            //billboardDatabase.addToDatabase(billboard, 100003);

            ScheduleDatabase scheduleDatabase = new ScheduleDatabase(properties);
            Scheduled scheduled = new Scheduled(100000,100000,ScheduleHelper.DateTime(0,0,0,0),1440, new int[]{1,0,0});
            scheduleDatabase.addToDatabase(scheduled, 100000);
            //scheduled.setID(100012);
            //scheduleDatabase.removeSchedule(scheduled);

            ArrayList<Scheduled> schedule = scheduleDatabase.getSchedule();
            ScheduleHelper scheduleHelper = new ScheduleHelper();
            ArrayList<Event> events = scheduleHelper.GenerateEvents(schedule);

            for (Event event: events) {
                System.out.println("Start Time: " + event.getStartTime());
                System.out.println("End Time: " + event.getEndTime());
                System.out.println("Day: " + event.getDay());
            }

            System.out.println("----------------");
            Calendar currentTime = Calendar.getInstance();
            System.out.println("Current Time: " + (currentTime.get(Calendar.MINUTE) + (currentTime.get(Calendar.HOUR_OF_DAY) * 60)));
            System.out.println("Day: " + currentTime.get(Calendar.DAY_OF_WEEK));

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}

/*
try {
        Properties properties = new Properties();

        SessionDatabase sessionDatabase = new SessionDatabase(properties);
        sessionDatabase.setSession("hasSession");

    } catch (
    FileNotFoundException e) {
        e.printStackTrace();
    }
 */

/*
try {
            Properties properties = new Properties();

            ArrayList<Integer> permissions = new ArrayList<>(Arrays.asList(0, 0, 0, 0));
            User rootUser = new User("invalidSession", "-84ac65418454-109938252-39d95133-29e3130d64405234-8f869455537422a281c0001227a-81af505-1138f-24e8291d-68bc11270-32e0-12088-36dc-94a211371-3fd-12f4", permissions, 100003,"y6WOb24rUAINN6KoUQ7lWNeniyTpsxPaZqzEhvAMzSqE5MrIx2kJS9TaTm0rl96n");

            Connection connection = DriverManager.getConnection(properties.getDatabaseURL(), properties.getDatabaseUser(), properties.getDatabasePassword());

            try {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO users VALUES (?,?,?)");
                statement.clearParameters();
                statement.setInt(1, 100003);
                statement.setString(2, "invalidSession");
                statement.setBinaryStream(3, new ByteArrayInputStream(rootUser.getByteArray()));
                statement.executeUpdate();

            } catch (SQLException e) {

            }


        } catch (Throwable e) {
            e.printStackTrace();
        }
 */
