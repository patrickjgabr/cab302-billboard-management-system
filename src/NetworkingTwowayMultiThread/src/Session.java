import java.util.ArrayList;

public class Session {

    private Integer userID;
    private String sessionToken;
    private ArrayList<Boolean> permission;

    public Session(Integer userID, String sessionToken, ArrayList<Boolean> permission) {
        this.userID = userID;
        this.sessionToken = sessionToken;
        this.permission = permission;
    }

    public Integer getUserName() {
        return userID;
    }

    public void setUserName(Integer userID) {
        this.userID = userID;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public ArrayList<Boolean> getPermission() {
        return permission;
    }

    public void setPermission(ArrayList<Boolean> permission) {
        this.permission = permission;
    }
}