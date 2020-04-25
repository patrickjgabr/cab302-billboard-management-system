package Shared;

import java.io.*;
import java.util.ArrayList;

public class User implements Serializable {

    private String userName;
    private String userPassword;
    private ArrayList<Integer> permission;
    private Integer userID;
    private String salt;

    /**
     * v1
     * Constructs and initalizes a User object
     * @param userName The Users username
     * @param userPassword The Hashed password of a User
     * @param permission The array of Integers representing the permissions of the User with a 1 or 0.
     * @param userID The Users ID
     */

    public User(String userName, String userPassword, ArrayList<Integer> permission, Integer userID, String salt)  {
        this.userName = userName;
        this.userPassword = userPassword;
        this.permission = permission;
        this.userID = userID;
        this.salt = salt;
    }
    public User(byte[] bytes){
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
            Object user = inputStream.readObject();
            User createdUser = (User) user;
            this.userName = createdUser.userName;
            this.userPassword = createdUser.userPassword;
            this.permission = createdUser.permission;
            this.userID = createdUser.userID;
            this.salt = createdUser.salt;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public User() {

    }

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
     * Returns the Users salt value
     * @return Users salt value
     */

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
     * @param permission array of 1s and 0s representing the Users new permissions
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

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public void setUserPassword(String userPassword) {this.userPassword = userPassword;}

    /**
     * Returns byte array for a user object which can be sent and stored in the database
     * @param user user object to be converted to byte array.
     * @return Byte array
     */
    public static byte[] userToByte(User user) {
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        ObjectOutputStream objOutput = null;
        byte[] data;
        try {
            objOutput = new ObjectOutputStream(byteOutput);
            objOutput.writeObject(user);
            data = byteOutput.toByteArray();
        } catch (IOException e) {
            data = new byte[]{0};
        }
        return data;
    }

    public String getSalt() {
        return salt;
    }
}

