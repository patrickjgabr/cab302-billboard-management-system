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

    public void requestLogin(String username) {
        this.communicationID = 10;
        this.data = username;
    }

    public void requestBillboards(Session session) {
        this.communicationID = 20;
        this.session = session;

    }
    public void createBillboard(Session session, Billboard billboard) {
        this.communicationID = 21;
        this.data = billboard;
    }

    public void updateBillboard(Session session, Billboard billboard) {
        this.communicationID = 22;
        this.data = billboard;
    }
    public void requestUsers(Session session) {
        this.communicationID = 30;
    }
    public void createUsers(Session session, User user) {
        this.communicationID = 31;
        this.data = user;
    }
    public void updateUser(Session session, User user) {
        this.communicationID = 32;
        this.data = user;
    }
    public void requestSchedule(Session session) {
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


    public Integer getCommunicationID() {
        return communicationID;
    }
}
