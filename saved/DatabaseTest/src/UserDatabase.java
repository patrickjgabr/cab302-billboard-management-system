import java.sql.*;

public class UserDatabase implements Database {

    private User user;
    private Connection connection;
    private Statement statement;
    private ResultSet results;

    public UserDatabase(User user) {
        this.user = user;
    }

    @Override
    public boolean isInTable() {
        boolean returnValue = false;
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
        return returnValue;
    }

    @Override
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

    @Override
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