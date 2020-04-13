import java.util.ArrayList;

public class User {

    private String userName;
    private String userPassword;
    private ArrayList<Integer> permission;
    private Integer userID;

    /**
     * Constructs and initalizes a User object
     * @param userName The Users username
     * @param userPassword The Hashed password of a User
     * @param permission The array of Integers representing the permissions of the User with a 1 or 0.
     * @param userID The Users ID
     */

    public User(String userName, String userPassword, ArrayList<Integer> permission, Integer userID) {
        this.userName = userName;
        this.userPassword = userPassword;
        this.permission = permission;
        this.userID = userID;
    }

    public User() {}

    /**
     * Returns the Users username
     * @return Users username
     */

    public String getUserName() {
        return userName;
    }

    /**
     * Sets the Users username
     * @param userName New Users username
     */

    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Returns the Users hashed password
     * @return Users hashed password
     */

    public String getUserPassword() {
        return userPassword;
    }

    /**
     * Returns the Users permissions
     * @return ArrayList<Integer> of Users permissions represented by 1 and 0 for each persmission
     */

    public ArrayList<Integer> getPermission() {
        return permission;
    }

    /**
     * Sets the Users new permissions
     * @param permission An array of 1s and 0s representing the Users new permissions
     */

    public void setPermission(ArrayList<Integer> permission) {
        this.permission = permission;
    }

    /**
     * Returns the Users ID
     * @return Users ID
     */

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer newID) {
        this.userID = newID;
    }
}
