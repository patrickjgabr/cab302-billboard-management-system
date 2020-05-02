package Server.Database;

import Server.ConsoleMessage.DatabaseMessage;
import Shared.Properties;
import Shared.User;

import java.sql.ResultSet;

public class SessionDatabase extends Database {

    private Properties properties;
    private DatabaseMessage databaseMessage;

    public SessionDatabase(Properties properties) {
        super(properties);
        databaseMessage = new DatabaseMessage();
        this.properties = properties;
    }

    public String setSession(String userName) {
        super.startConnection();
        UserDatabase userDatabase = new UserDatabase(properties);
        try {
            User user = userDatabase.getUser(userName);
            String token = generateToken();

            String insetQuery = "INSERT INTO sessions (sessionToken, userID) VALUES (?, ?)";
            super.runInsertUpdateQuery(insetQuery, new Object[]{token, user.getUserID()}, "INSERT");

            databaseMessage.printGeneral("DATABASE", "Session set for \"" + userName + "\"", 50);
            super.closeConnection();
            return token;

        } catch (Throwable throwable) {
            databaseMessage.printWarning("Database failed to set session. User " + userName + "already has a session", 50);
            super.closeConnection();
            return "";
        }

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
            result.next();

            super.closeConnection();
            return true;

        } catch (Throwable throwable) {
            super.closeConnection();
            return false;
        }
    }

    public boolean removeSession(String token) {

        if(checkSession(token)) {
            String deleteSQL = "DELETE FROM sessions WHERE sessionToken = \"" + token + "\"";
            try {
                super.startConnection();
                super.runDelete(deleteSQL);
                super.closeConnection();
                return true;
            } catch (Throwable throwable) {
                super.closeConnection();
                return false;
            }
        } else {
            super.closeConnection();
            return false;
        }
    }


}
