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
    private boolean bool;

    /**
     * Blank constuctor which can be used to create a blank CommunicationPacket
     */
    public Message() {

    }

    /**
     * Constructor used to create a communication packet for sending Billboard objects across a network
     * @param billboard Billboard object being sent across the network
     */
    public Message(Billboard billboard) {
        this.communicationID = 10;
        this.data = billboard;
    }

    /**
     * Constructor used to create a communication packet for sending an array of Billboards objects across a network
     * @param billboards Array of Billboard objects being sent across the network
     */
    public Message(Billboard[] billboards) {
        this.communicationID = 20;
        this.data = billboards;
    }

    /**
     * Constructor used to create a communication packet for sending User objects across a Network
     * @param user User object being sent across the network
     */
    public CommunicationPacket(User user) {
        this.communicationID = 30;
        this.data = user;
    }

    /**
     * Constructor used to create a communication packet for sending an array of User objects across a network
     * @param users Array of User objects being sent across the network
     */
    public Message(User[] users) {
        this.communicationID = 40;
        this.data = users;
    }

    /**
     * Constructor used to create a communication packet for sending the login details across a network
     * @param loginDetails Array of two Strings. String[0] = Username String[1] = Password
     */
    public Message(String[] loginDetails) {
        this.communicationID = 40;
        this.data = loginDetails;
    }

    public Message(boolean bool) {
        this.communicationID = 30;
        this.bool = bool;
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

    public boolean getBool() {
        return bool;
    }
}
