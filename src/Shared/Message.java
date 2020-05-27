package Shared;

import java.io.Serializable;

/**
 * Class used to create message objects which are passwed between each of the Networked components
 */
public class Message implements Serializable {

    private Integer communicationID;
    private String session;
    private Object data;

    /**
     * Blank constuctor which can be used to create a blank CommunicationPacket
     */
    public Message() {
        this.communicationID = 0;
    }

    public Message(String session) {
        this.session = session;
    }

    public Message loginUser(String username, String password) {
        this.communicationID = 10;
        this.data = new String[]{username,password};
        return this;
    }

    public Message logoutUser(String token) {
        this.communicationID = 11;
        this.data = token;
        return this;
    }

    public Message requestBillboards() {
        this.communicationID = 20;
        return this;
    }

    public Message createBillboard(Billboard billboard) {
        this.communicationID = 21;
        this.data = billboard;
        return this;
    }

    public Message updateBillboard(Billboard billboard) {
        this.communicationID = 22;
        this.data = billboard;
        return this;
    }

    public Message deleteBillboard(Billboard billboard) {
        this.communicationID = 23;
        this.data = billboard;
        return this;
    }

    public Message requestUsers() {
        this.communicationID = 30;
        return this;
    }
    public Message createUsers(User user) {
        this.communicationID = 31;
        this.data = user;
        return this;
    }
    public Message updateUser(User user) {
        this.communicationID = 32;
        this.data = user;
        return this;
    }
    public Message requestSchedule() {
        this.communicationID = 40;
        return this;
    }
    public Message scheduleBillboard(Scheduled scheduled) {
        this.communicationID = 41;
        this.data = scheduled;
        return this;
    }

    /**
    need to decide how to schedule a billboard

    public void ScheduleBillboard(Session session, Billboard billboard) {
        this.communicationID = 41;
        this.data = billboard;
    }
    **/


    /**
    public Message updateScheduledBillboard(Session session, Billboard billboard) {
        this.communicationID = 42;
        this.data = billboard;
        return this;
    }
     */


    /**
     * Returns the session information of the Control Panel
     * @return Session object which contains session information about the Control Panel
     */
    public String getSession() {
        return session;
    }

    /**
     * Sets the session inforamtion of the communication packet
     * @param session Session object for the Control Panel
     */
    public void setSession(String session) {
        this.session = session;
    }

    /**
     * Returns the data which the communication packet contains
     * @return  A large number of different Classes that all extend Object
     */

    public Object getData() {
        return data;
    }

    public void setData(Object data) { this.data = data; }


    public Integer getCommunicationID() {
        return communicationID;
    }

    public void setCommunicationID(Integer communicationID) {this.communicationID = communicationID;}
}
