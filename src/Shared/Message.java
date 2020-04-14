package Shared;

import Shared.Session;

import java.io.Serializable;
import java.util.List;
import java.util.TreeMap;

/**
 * Class used to create message objects which are passwed between each of the Networked components
 */
public class Message implements Serializable {

    private Integer communicationID;
    private Session session;
    private Object data;

    /**
     * Blank constuctor which can be used to create a blank CommunicationPacket
     */
    public Message() {

    }

    public Message(Session session) {
        this.session = session;
    }

    public void requestUser(String username) {
        this.communicationID = 10;
        this.data = username;
    }

    public void requestBillboards() {
        this.communicationID = 20;

    }
    public void createBillboard(Billboard billboard) {
        this.communicationID = 21;
        this.data = billboard;
    }

    public void updateBillboard(Billboard billboard) {
        this.communicationID = 22;
        this.data = billboard;
    }
    public void requestUsers() {
        this.communicationID = 30;
    }
    public void createUsers(User user) {
        this.communicationID = 31;
        this.data = user;
    }
    public void updateUser(User user) {
        this.communicationID = 32;
        this.data = user;
    }
    public void requestSchedule() {
        this.communicationID = 40;
    }

    /**
    need to decide how to schedule a billboard

    public void ScheduleBillboard(Session session, Billboard billboard) {
        this.communicationID = 41;
        this.data = billboard;
    }
    **/

    public void updateScheduledBillboard(Session session, Billboard billboard) {
        this.communicationID = 42;
        this.data = billboard;
    }


    /**
     * Returns the session information of the Control Panel
     * @return Session object which contains session information about the Control Panel
     */
    public Session getSession() {
        return session;
    }

    /**
     * Sets the session inforamtion of the communication packet
     * @param session Session object for the Control Panel
     */
    public void setSession(Session session) {
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
}
