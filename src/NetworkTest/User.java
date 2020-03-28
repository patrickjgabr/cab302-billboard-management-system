import java.util.ArrayList;

public class User {

    private String userName;
    private Integer salt;
    private String userPassword;
    private ArrayList<Integer> permission;
    private Integer userID;

    public User(String userName, Integer salt, String userPassword, ArrayList<Integer> permission, Integer userID) {
        this.userName = userName;
        this.salt = salt;
        this.userPassword = userPassword;
        this.permission = permission;
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getSalt() {
        return salt;
    }

    public void setSalt(Integer salt) {
        this.salt = salt;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public ArrayList<Integer> getPermission() {
        return permission;
    }

    public void setPermission(ArrayList<Integer> permission) {
        this.permission = permission;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }
}
