import java.io.Serializable;
import java.util.List;
import java.util.TreeMap;

/**
 * Class used to create message objects which are passwed between each of the Networked components
 */

public class CommunicationPacket implements Serializable {

    private Integer communicationID;
    private Session session;
    private Object data;
    private boolean bool;

    /**
     * Blank constuctor which can be used to create a blank CommunicationPacket
     */
    public CommunicationPacket() {

    }

    /**
     * Constructor used to create a communication packet for sending Billboard objects across a Network
     * @param billboard Billboard object being sent across the network
     */
    public CommunicationPacket(Billboard billboard) {
        this.communicationID = 10;
        this.data = billboard;
    }

    /**
     * Constructor used to create a communication packet for sending an List of Billboards objects across a Network
     * @param billboards List of Billboard objects being sent across the network
     */
    public CommunicationPacket(List<Billboard> billboards) {
        this.communicationID = 20;
        this.data = billboards;
    }
    public CommunicationPacket(boolean bool) {
        this.communicationID = 30;
        this.bool = bool;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Object getData() {
        return data;
    }

    public boolean getBool() {
        return bool;
    }
}
