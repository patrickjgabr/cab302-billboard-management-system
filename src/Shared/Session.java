package Shared;

import java.util.ArrayList;

public class Session {
    /**
     * v1
    */
    private String userName;
    private String sessionToken;
    private ArrayList<Boolean> permission;

    public Session(String sessionToken, ArrayList<Boolean> permission) {
        this.sessionToken = sessionToken;
        this.permission = permission;
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

    public ArrayList<Boolean> getPermission() {
        return permission;
    }

    public void setPermission(ArrayList<Boolean> permission) {
        this.permission = permission;
    }
}
