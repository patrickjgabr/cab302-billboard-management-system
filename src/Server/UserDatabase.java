package Server;

import java.sql.*;
import java.util.ArrayList;
import Shared.*;

public class UserDatabase extends Database{

    private User user;
    private ResultSet results;

    public UserDatabase(User user) {
        //CHANGE
        super(new Properties("jdbc:mariadb://localhost:3306/applicationdatabase", "root", "password"));
        this.user = user;
    }

    public UserDatabase() {
        //CHANGE
        super(new Properties("jdbc:mariadb://localhost:3306/applicationdatabase", "root", "password"));
    }

    public User getUser(boolean byID, String value) {
        User returnValue = new User();

        try {
            String sqlSelect;

            if(byID) {
                sqlSelect = "SELECT * FROM USERS t1 JOIN PERMISSIONS t2 ON t2.userID = t1.userID WHERE t1.userID = " + value;
            }
            else {
                sqlSelect = "SELECT * FROM USERS t1 JOIN PERMISSIONS t2 ON t2.userID = t1.userID WHERE t1.userName = \"" + value + "\"";
            }

            results = super.runSelectQuery(sqlSelect);
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

            String sqlSelect = "SELECT * FROM USERS t1 JOIN PERMISSIONS t2 ON t2.userID = t1.userID";
            results = super.runSelectQuery(sqlSelect);

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
                String sqlSelect = "select userID from users where userID = " + user.getUserID().toString();
                results = super.runSelectQuery(sqlSelect);
                returnValue = results.first();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (returnValue) {
            System.out.println("User already exists in table");
        }

        return returnValue;
    }

    public void updateDatabase() {
        if (isInTable()) {
            updateUserTable();
            updatePermissionsTable();
        }
    }

    private void updateUserTable() {
        String sqlUpdate = "update users set userPassword = " + "\"" + user.getUserPassword() + "\"" + "where userID = " + user.getUserID();
        super.runUpdateQuery(sqlUpdate);
    }

    private void updatePermissionsTable() {
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
        super.runUpdateQuery(sqlUpdate);
    }

    public void addToDatabase() {
        if (!isInTable()) {
            addToUserTable();
            updateUserID();
            addToPermissionsTable();
            System.out.println("User added");
        }
    }

    //Add hashing here will need another select statement
    private void addToUserTable() {
        String sqlInsert ="insert into users (userName, userPassword) values (\"" + user.getUserName() + "\"" + ",\"" + user.getUserPassword() + "\"" + ")";
        super.runInsertQuery(sqlInsert);
    }

    private void updateUserID() {
        try {
            String sqlSelect = "select * from users where userName = \"" + user.getUserName() + "\"";
            results = super.runSelectQuery(sqlSelect);
            results.next();
            user.setUserID(results.getInt("userID"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addToPermissionsTable() {
        String sqlInsert = "insert into permissions value (" + user.getUserID() + ", " + user.getPermission().get(0) + ", " + user.getPermission().get(1) + ", " +
                    user.getPermission().get(2) + ", " + user.getPermission().get(3) + ", " + user.getPermission().get(4) + ", " +
                    user.getPermission().get(5) + ", " + user.getPermission().get(6) + ", " + user.getPermission().get(7) + ", " +
                    user.getPermission().get(8) + "," + user.getPermission().get(9)+ ")";
        super.runInsertQuery(sqlInsert);

    }
}