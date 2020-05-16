package Server.Database;

import Server.ConsoleMessage.DatabaseMessage;
import Shared.Properties;
import Shared.User;

import javax.xml.transform.Result;
import java.awt.*;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class SessionDatabase extends Database {

    private Properties properties;
    private DatabaseMessage databaseMessage;

    public SessionDatabase(Properties properties) {
        super(properties);
        databaseMessage = new DatabaseMessage();
        this.properties = properties;
    }

    public String setSession(String userName) {
        UserDatabase userDatabase = new UserDatabase(properties);
        try {
            User user = userDatabase.getUser(userName, true);
            this.removeSession(Integer.toString(user.getUserID()), false);

            String[] expireTime = generateExpireTime();
            String token = generateToken();

            String insetQuery = "INSERT INTO sessions (sessionToken, userID, expireDate, expireTime) VALUES (?, ?, ?, ?)";
            super.startConnection();
            super.runInsertUpdateQuery(insetQuery, new Object[]{token, user.getUserID(), expireTime[0], expireTime[1]}, "INSERT");

            databaseMessage.printGeneral("DATABASE", "Session set for \"" + userName + "\"", 50);
            super.closeConnection();
            return token;

        } catch (Throwable throwable) {
            databaseMessage.printWarning("Database failed to set session.        User \"" + userName + "\" already has a session", 50);
            super.closeConnection();
            return "";
        }

    }

    private String[] generateExpireTime() {
        LocalDateTime time = LocalDateTime.now().plusDays(1);
        String expireDate = (Integer.toString(time.getYear()) + "-" + Integer.toString(time.getMonthValue()) + "-" + Integer.toString(time.getDayOfMonth()));
        String expireTime = (Integer.toString(time.getHour()) + ":" + Integer.toString(time.getMinute()) + ":" + Integer.toString(time.getSecond()));
        String[] expire =  {expireDate, expireTime};

        return expire;
    }

    private String generateToken() {

        StringBuilder tokenBuilder = new StringBuilder(64);
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz!@#$%^&*()_+-={}[]:<>?,./";

        for (int i = 0; i < 64; i++) {
            int index = (int)(characters.length()* Math.random());
            tokenBuilder.append(characters.charAt(index));
        }

        return tokenBuilder.toString();
    }

    public boolean checkSession(String token) {
        if(!super.getConnectionStatus()) {
            super.startConnection();
        }
        String select = "SELECT * FROM sessions WHERE sessionToken = \"" + token + "\"";
        try {
            ResultSet result = super.runSelectQuery(select);

            if(result.next()) {
                boolean returnValue = false;

                String expireTimeString = result.getString("expireDate") + " " + result.getString("expireTime");
                Calendar expireCalender = getTokenExpireTime(expireTimeString);
                Calendar currentCalender = Calendar.getInstance();

                if(expireCalender.compareTo(currentCalender) == 1) {
                    returnValue = true;
                }

                super.closeConnection();
                return returnValue;

            } else {
                super.closeConnection();
                return false;
            }

        } catch (Throwable throwable) {
            super.closeConnection();
            return false;
        }
    }

    private Calendar getTokenExpireTime(String timeString) throws ParseException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime expireTime = LocalDateTime.parse(timeString, dateTimeFormatter);
        Calendar calendarExpire = Calendar.getInstance();
        calendarExpire.set(Calendar.YEAR, expireTime.getYear());
        calendarExpire.set(Calendar.MONTH, expireTime.getMonthValue());
        calendarExpire.set(Calendar.DAY_OF_YEAR, expireTime.getDayOfYear());
        calendarExpire.set(Calendar.HOUR_OF_DAY, expireTime.getHour());
        calendarExpire.set(Calendar.MINUTE, expireTime.getMinute());
        calendarExpire.set(Calendar.SECOND, expireTime.getSecond());

        return  calendarExpire;
    }

    public boolean removeSession(String value, boolean token) {

        String deleteSQL;

        if(token) {
            deleteSQL = "DELETE FROM sessions WHERE sessionToken = \"" + value + "\"";
        } else {
            deleteSQL = "DELETE FROM sessions WHERE userID = " + value + "";
        }

        try {
            super.startConnection();
            super.runDelete(deleteSQL);
            super.closeConnection();
            return true;
        } catch (Throwable throwable) {
            super.closeConnection();
            return false;
        }
    }

    public User getUserFromSession(String session) {
        String sqlSelect = "SELECT * FROM sessions WHERE sessionToken = \"" + session + "\"";
        try {
            ResultSet resultSet = super.runSelectQuery(sqlSelect);

            UserDatabase userDatabase = new UserDatabase(properties);


            User user = userDatabase.getUser(resultSet.getString("userID"), false);
            return user;

        } catch (Throwable throwable) {
            return new User();
        }
    }

}
