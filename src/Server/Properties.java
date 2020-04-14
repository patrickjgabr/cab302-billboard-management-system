package Server;

public class Properties {

    private String databaseURL;
    private String user;
    private String password;

    public Properties(String databaseURL, String user, String password) {
        this.databaseURL = databaseURL;
        this.user = user;
        this.password = password;
    }

    public String getDatabaseURL() {
        return databaseURL;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
