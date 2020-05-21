package Server.Database;

import Server.Database.Database;
import Server.Server;
import Shared.*;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Main_Database {

    public static void main(String[] args) {

        try {
            Properties properties = new Properties();

            Billboard billboard = new Billboard("root", "Test", "NEWNEWNEW", "msgText", "", "#000FFF", "infoText", "");
            billboard.setBillboardID(100000);

            BillboardDatabase billboardDatabase = new BillboardDatabase(properties);
            billboardDatabase.updateDatabase(billboard);

            Billboard billboardNew = billboardDatabase.getBillboard(billboard.getName(), false);
            System.out.println(billboardNew.getPictureLink());

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
