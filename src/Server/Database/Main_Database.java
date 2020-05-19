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

            ArrayList<Integer> permissions = new ArrayList<>(Arrays.asList(1, 1, 1, 1));
            User rootUser = new User("root", "c13866ce9e42e90d3cf50ded2dc9e00194ffc4ad4e15865cd1b368f168944646", permissions, 100000,"y6WOb24rUAINN6KoUQ7lWNeniyTpsxPaZqzEhvAMzSqE5MrIx2kJS9TaTm0rl96n");

            Connection connection = DriverManager.getConnection(properties.getDatabaseURL(), properties.getDatabaseUser(), properties.getDatabasePassword());

            try {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO users VALUES (?,?,?)");
                statement.clearParameters();
                statement.setInt(1, 100000);
                statement.setString(2, "root");
                statement.setBinaryStream(3, new ByteArrayInputStream(rootUser.getByteArray()));
                statement.executeUpdate();

            } catch (SQLException e) {

            }

            MessageDigest passwordHash = MessageDigest.getInstance("SHA-256");
            passwordHash.update((rootUser.getSalt() +"5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8").getBytes());
            byte [] byteArray = passwordHash.digest();

            StringBuilder sb = new StringBuilder();
            for (byte b : byteArray) {
                sb.append(String.format("%02x", b & 0xFF));
            }
            String hashed = sb.toString();
            System.out.println(hashed);

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
