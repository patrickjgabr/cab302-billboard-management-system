package Shared;

import java.io.*;
import java.util.ArrayList;

/**
 * The User class provides a storage structure for User objects which need to be networked between components including the Server, Control Panel and Viewer.
 * This class provides a highly pliable method of instantiating, storing and editing Users and their properties through its set of constructors and getter setter methods.
 */
public class User implements Serializable {

    private String userName;
    private String userPassword;
    private ArrayList<Integer> permission;
    private Integer userID;
    private String salt;

    /**
     * Constructor which instantiates a User Object containing the input data
     * @param userName User username
     * @param userPassword User hashed password
     * @param permission User permissions ArrayList
     * @param userID User ID
     */
    public User(String userName, String userPassword, ArrayList<Integer> permission, Integer userID, String salt)  {
        this.userName = userName;
        this.userPassword = userPassword;
        this.permission = permission;
        this.userID = userID;
        this.salt = salt;
    }

    /**
     * Constructor which instantiates a User Object from a byte array containing the relevant User properties.
     * This method is designed to recreate a Users Object from the output of the getByteArray method
     * @param bytes User Object as a byte array
     */
    public User(byte[] bytes){
        try {
            //Instantiate a ObjectInputStream using the given byte array
            ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));

            //Read the given object
            Object user = inputStream.readObject();

            //Cast the Object to User
            User createdUser = (User) user;

            //Assign the properties of User to the properties of this
            this.userName = createdUser.getUserName();
            this.userPassword = createdUser.getUserPassword();
            this.permission = createdUser.getPermission();
            this.userID = createdUser.getUserID();
            this.salt = createdUser.getSalt();

        } catch (IOException | ClassNotFoundException e) {

        }
    }

    /**
     * Default constructor used to instantiate a User Object with no properties
     */
    public User() {

    }

    /**
     * Returns the Users username
     * @return User username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the userName of the User Object to the given value
     * @param userName New username
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Returns the Users hashed password
     * @return Hashed User password
     */
    public String getUserPassword() {
        return userPassword;
    }

    /**
     * Returns the Users permissions
     * @return ArrayList<Integer> of the Users permissions (1 has permission 0 doesn't)
     */
    public ArrayList<Integer> getPermission() {
        return permission;
    }

    /**
     * Sets the permissions of the User Object to the given value
     * @param permission New permissions ArrayList (1 has permission 0 doesn't)
     */
    public void setPermission(ArrayList<Integer> permission) {
        this.permission = permission;
    }

    /**
     * Returns the Users ID
     * @return User ID
     */
    public Integer getUserID() {
        return userID;
    }

    /**
     * Sets the userID of the User Object to the given value
     * @param userID New userID
     */
    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public void setUserPassword(String userPassword) {this.userPassword = userPassword;}

    /**
     * Returns a byte array representing the User Object
     * @return User byte array
     */
    public byte[] getByteArray() {
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        ObjectOutputStream objOutput = null;
        byte[] data;
        try {
            objOutput = new ObjectOutputStream(byteOutput);
            objOutput.writeObject(this);
            data = byteOutput.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            data = new byte[]{0};
        }
        return data;
    }

    /**
     * Returns the Users salt
     * @return User salt
     */
    public String getSalt() {
        return salt;
    }

    /**
     * Sets the salt of the User Object to the given value
     * @param salt New salt
     */
    public void setSalt(String salt) {this.salt = salt;}
}

