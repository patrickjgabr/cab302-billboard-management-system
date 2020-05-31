package Shared;

import java.io.Serializable;

/**
 * Message class provides a storage structure for data of any type required to be networked between components including the Server, Control Panel and Viewer.
 * The communicationID property of the Message class are used to indicate the type or status of message being networked between the components.
 * By indicating the request type and or status in the communicationID receiving entities are able to handle the associated data and session, and return a response if required.
 */
public class Message implements Serializable {

    private Integer communicationID;
    private String session;
    private Object data;

    /**
     * Default constructor used to instantiate a Message Object with no properties
     */
    public Message() {
        this.communicationID = 0;
    }

    /**
     * Constructor used to instantiate a Message Object with a current session
     * @param session Session token
     */
    public Message(String session) {
        this.session = session;
    }

    /**
     * Sets the Message Object data to a String array containing the username and password with communicationID 10
     * @param username Users username
     * @param password Users password (Hashed)
     * @return Message Object
     */
    public Message loginUser(String username, String password) {
        this.communicationID = 10;
        this.data = new String[]{username,password};
        return this;
    }

    /**
     * Sets the Message Object data to a String containing the clients token with communicationID 11
     * @param token Clients token
     * @return Message Object
     */
    public Message logoutUser(String token) {
        this.communicationID = 11;
        this.data = token;
        return this;
    }

    /**
     * Sets Message Object to have communicationID 20
     * @return Message Object
     */
    public Message requestBillboards() {
        this.communicationID = 20;
        return this;
    }

    /**
     * Sets the Message Object data to a Billboard Object to be added into the database with communicationID 11
     * @param billboard Billboard Object to be added into the database
     * @return Message Object
     */
    public Message createBillboard(Billboard billboard) {
        this.communicationID = 21;
        this.data = billboard;
        return this;
    }

    /**
     * Sets the Message Object data to a Billboard Object to be update in the database with communicationID 22
     * @param billboard Billboard Object to be updated in the database
     * @return Message Object
     */
    public Message updateBillboard(Billboard billboard) {
        this.communicationID = 22;
        this.data = billboard;
        return this;
    }

    /**
     * Sets the Message Object data to a Billboard Object to be removed from the database with communicationID 23
     * @param billboard Billboard Object to be removed from the database
     * @return Message Object
     */
    public Message deleteBillboard(Billboard billboard) {
        this.communicationID = 23;
        this.data = billboard;
        return this;
    }

    /**
     * Sets the Message Object to have communicationID 30
     * @return Message Object
     */
    public Message requestUsers() {
        this.communicationID = 30;
        return this;
    }

    /**
     * Sets the Message Object data to a User Object to be added into the database with communicationID 31
     * @param user User Object to be added into the database
     * @return Message Object
     */
    public Message createUsers(User user) {
        this.communicationID = 31;
        this.data = user;
        return this;
    }

    /**
     *Sets the Message Object data to a User Object to update in the database with communicationID 32
     * @param user User Object to be updated in the database
     * @return Message Object
     */
    public Message updateUser(User user) {
        this.communicationID = 32;
        this.data = user;
        return this;
    }

    /**
     *Sets the Message Object data to a User Object to be removed from the database with communicationID 33
     * @param user User Object to be removed from the database
     * @return Message Object
     */
    public Message deleteUser(User user) {
        this.communicationID = 33;
        this.data = user;
        return this;
    }

    /**
     *Sets the Message Object data to a String array of the username and password to be updated in the database with communicationID 34
     * @param username Current username
     * @param password New password
     * @return Message Object
     */
    public Message updatePassword(String username, String password) {
        this.communicationID = 34;
        this.data= new String[]{username,password};
        return this;
    }

    /**
     *Sets the Message Object to have a communicationID 40
     * @return Message Object
     */
    public Message requestSchedule() {
        this.communicationID = 40;
        return this;
    }

    /**
     *Sets the Message Object data to a Scheduled Object to be added into the database with communicationID 41
     * @param scheduled Scheduled Object to be added into the database
     * @return Message Object
     */
    public Message scheduleBillboard(Scheduled scheduled) {
        this.communicationID = 41;
        this.data = scheduled;
        return this;
    }

    /**
     *Sets the Message Object data to a Scheduled Object to be updated in the database with communicationID 42
     * @param scheduled Scheduled Object to be update in the database
     * @return Message Object
     */
    public Message updateSchedule(Scheduled scheduled) {
        this.communicationID = 42;
        this.data = scheduled;
        return this;
    }

    /**
     *Sets the Message Object data to a Scheduled Object to be removed from the database with communicationID 43
     * @param scheduled Scheduled Object to be removed from the database
     * @return Message Object
     */
    public Message deleteSchedule(Scheduled scheduled) {
        this.communicationID = 43;
        this.data = scheduled;
        return this;
    }

    /**
     *Sets the Message Object to have communicationID 50
     * @return Message Object
     */
    public Message getScheduleViewer() {
        this.communicationID = 50;
        return this;
    }

    /**
     *Returns the Messages session token
     * @return Message session token
     */
    public String getSession() {
        return session;
    }

    /**
     *Sets the Message Object to have the given session
     * @param session New session token
     */
    public void setSession(String session) {
        this.session = session;
    }

    /**
     *Returns the Messages data (Variable type)
     * @return Message data (Variable type)
     */
    public Object getData() {
        return data;
    }

    /**
     *Sets the Message Object data to a given Object
     * @param data New data Object
     */
    public void setData(Object data) { this.data = data; }


    /**
     *Returns the Messages communicationID
     * @return Message communicationID
     */
    public Integer getCommunicationID() {
        return communicationID;
    }

    /**
     *Sets the Message Object communicationID to be the given data
     * @param communicationID New communicationID
     */
    public void setCommunicationID(Integer communicationID) {this.communicationID = communicationID;}
}
