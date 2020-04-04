import java.util.ArrayList;

public class Session {
    /**
     * v1
    */
    private String userName;
    private String sessionToken;
    private ArrayList<Boolean> permission;
    private String IP;

    public Session(String userName, String sessionToken, ArrayList<Boolean> permission, String IP) {
        this.userName = userName;
        this.sessionToken = sessionToken;
        this.permission = permission;
        this.IP = IP;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public ArrayList<Boolean> getPermission() {
        return permission;
    }

    public void setPermission(ArrayList<Boolean> permission) {
        this.permission = permission;
    }
}
