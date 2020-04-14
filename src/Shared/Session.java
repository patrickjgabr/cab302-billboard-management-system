package Shared;

import java.io.Serializable;
import java.util.ArrayList;

public class Session implements Serializable {
    /**
     * v1
    */
    private String userName;
    private String sessionToken;
    private ArrayList<Integer> permission;

    public Session(String sessionToken, ArrayList<Integer> permission) {
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

    public ArrayList<Integer> getPermission() {
        return permission;
    }

    public void setPermission(ArrayList<Integer> permission) {
        this.permission = permission;
    }
}
