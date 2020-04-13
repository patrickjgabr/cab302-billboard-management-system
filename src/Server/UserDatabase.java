package Server;

import java.sql.*;
import java.util.ArrayList;
import Shared.*;

public class UserDatabase {

    private User user;
    private Connection connection;
    private Statement statement;
    private ResultSet results;

    public UserDatabase(User user) {
        this.user = user;
    }

    public UserDatabase() {

    }

    //public Session login(String userName) {

    //}

    public User getUser(boolean byID, String value) {
        User returnValue = new User();

        try {

            connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/applicationdatabase", "root", "password");
            statement = connection.createStatement();
            String sqlSelect;

            if(byID) {
                sqlSelect = "SELECT * FROM USERS t1 JOIN PERMISSIONS t2 ON t2.userID = t1.userID WHERE t1.userID = " + value;
            }
            else {
                sqlSelect = "SELECT * FROM USERS t1 JOIN PERMISSIONS t2 ON t2.userID = t1.userID WHERE t1.userName = \"" + value + "\"";
            }

            results = statement.executeQuery(sqlSelect);
            results.next();

            returnValue = resultsSetToUser(results);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return returnValue;
    }

    public User[] getUsers() {
        User[] allUser = {};

        try {
            connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/applicationdatabase", "root", "password");
            statement = connection.createStatement();
            String sqlSelect = "SELECT * FROM USERS t1 JOIN PERMISSIONS t2 ON t2.userID = t1.userID";
            results = statement.executeQuery(sqlSelect);

            results.last();
            allUser = new User[results.getRow()];
            results.beforeFirst();

            Integer index = 0;
            while(results.next()) {
                allUser[index] = resultsSetToUser(results);
                index++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return allUser;
   }

    private User resultsSetToUser(ResultSet resultSet) {
        User returnValue = new User();

        try {
            Integer userID = resultSet.getInt("userID");
            String userName = resultSet.getString("userName");
            String userPassword = resultSet.getString("userPassword");
            ArrayList<Integer> permission = generatePermissionsArray(resultSet);

            returnValue = new User(userName, userPassword, permission, userID);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return returnValue;
    }

    private ArrayList<Integer> generatePermissionsArray(ResultSet resultSet) {
        ArrayList<Integer> permissions = new ArrayList<>();

        String[] tableColumn = {"createUser", "editUser", "deleteUser", "changePassword", "assignRole", "createBillboard", "editBillboard", "viewBillboard", "viewSchedule", "editSchedule"};

        try {
            Integer index = 0;
            for (String column : tableColumn) {
                permissions.add(index, resultSet.getInt(column));
                index ++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return  permissions;
    }

    public boolean isInTable() {
        boolean returnValue = false;

        if(user != null) {
            try {
                connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/applicationdatabase", "root", "password");
                statement = connection.createStatement();

                String sqlSelect = "select userID from users where userID = " + user.getUserID().toString();
                results = statement.executeQuery(sqlSelect);
                returnValue = results.first();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return returnValue;
    }

    public void updateDatabase() {
        if (isInTable()) {
            try {
                connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/applicationdatabase", "root", "password");
                statement = connection.createStatement();
                updateUserTable();
                updatePermissionsTable();
                connection.close();
                System.out.println("User updated");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("User not updated");
        }
    }

    private void updateUserTable() {
        try {
            String sqlUpdate = "update users set userPassword = " + "\"" + user.getUserPassword() + "\"" + "where userID = " + user.getUserID();
            statement.executeQuery(sqlUpdate);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updatePermissionsTable() {
        try {
            String sqlUpdate = "update permissions set createUser = " + "\"" + user.getPermission().get(0) + "\", " +
                                "editUser = " + "\"" + user.getPermission().get(1) + "\", " +
                                "deleteUser = " + "\"" + user.getPermission().get(2) + "\", " +
                                "changePassword = " + "\"" + user.getPermission().get(3) + "\", " +
                                "assignRole = " + "\"" + user.getPermission().get(4) + "\", " +
                                "createBillboard = " + "\"" + user.getPermission().get(5) + "\", " +
                                "editBillboard = " + "\"" + user.getPermission().get(6) + "\", " +
                                "viewBillboard = " + "\"" + user.getPermission().get(7) + "\", " +
                                "viewSchedule = " + "\"" + user.getPermission().get(8) + "\", " +
                                "editSchedule = " + "\"" + user.getPermission().get(9) + "\"" +
                                "where userID = " + user.getUserID();
            statement.executeQuery(sqlUpdate);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addToDatabase() {
        if (!isInTable()) {
            try {
                connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/applicationdatabase", "root", "password");
                statement = connection.createStatement();
                addToUserTable();
                updateUserID();
                addToPermissionsTable();
                connection.close();
                System.out.println("User added");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("User not added");
        }
    }

    private void addToUserTable() {
        try {
            String sqlInsert ="insert into users values ( NULL" + ",\"" + user.getUserName() + "\"" + ",\"" + user.getUserPassword() + "\"" + ")";
            statement.executeQuery(sqlInsert);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateUserID() {
        try {
            String sqlSelect = "select * from users where userName = \"" + user.getUserName() + "\"";
            results = statement.executeQuery(sqlSelect);
            results.next();
            user.setUserID(results.getInt("userID"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addToPermissionsTable() {
        try {
            String sqlInsert = "insert into permissions value (" + user.getUserID() + ", " + user.getPermission().get(0) + ", " + user.getPermission().get(1) + ", " +
                    user.getPermission().get(2) + ", " + user.getPermission().get(3) + ", " + user.getPermission().get(4) + ", " +
                    user.getPermission().get(5) + ", " + user.getPermission().get(6) + ", " + user.getPermission().get(7) + ", " +
                    user.getPermission().get(8) + "," + user.getPermission().get(9)+ ")";
            statement.executeQuery(sqlInsert);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}