import java.sql.*;

public class BillboardDatabase implements Database {


    private Billboard billboard;
    private Connection connection;
    private Statement statement;
    private ResultSet results;

    public BillboardDatabase(Billboard billboard) {this.billboard = billboard;}

    @Override
    public boolean isInTable() {
        boolean returnValue = false;

        try {
            connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/applicationdatabase", "root", "password");
            statement = connection.createStatement();

            String sqlSelect = "select billboardID from billboards where billboardID = " + billboard.getBillboardID();
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

                String sqlUpdate = "update billboards set name = " + "\"" + billboard.getName()+ "\", " +
                        "imageUrl = " + "\"" + billboard.getPictureLink() + "\", " +
                        "messageText = " + "\"" + billboard.getMessageText() + "\", " +
                        "messageTextColour = " + "\"" + billboard.getMessageTextColour() + "\", " +
                        "backgroundColour = " + "\"" + billboard.getBackgroundColour() + "\", " +
                        "informationText = " + "\"" + billboard.getInformationText() + "\", " +
                        "informationTextColour = " + "\"" + billboard.getBackgroundColour() + "\"" +
                        "where billboardID = " + billboard.getBillboardID();

                statement.executeQuery(sqlUpdate);
                connection.close();
                System.out.println("Billboard update");
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        else {
            System.out.println("Billboard not updated");
        }

    }

    @Override
    public void addToDatabase() {
        if(!isInTable()) {
            try {
                connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/applicationdatabase", "root", "password");
                statement = connection.createStatement();

                String sqlInsert = "insert into billboards values ( NULL, " + "\"" + billboard.getCreatorID() + "\", " +
                                                                    "\"" + billboard.getName() + "\", " +
                                                                    "\"" + billboard.getPictureLink() + "\", " +
                                                                    "\"" + billboard.getMessageText() + "\", " +
                                                                    "\"" + billboard.getMessageTextColour() + "\", " +
                                                                    "\"" + billboard.getBackgroundColour() + "\", " +
                                                                    "\"" + billboard.getInformationText() + "\", " +
                                                                    "\"" + billboard.getInformationTextColour() + "\")";
                statement.executeQuery(sqlInsert);
                connection.close();
                System.out.println("Billboard added");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("Billboard not added");
        }
    }
}
