import java.io.Serializable;
import java.util.List;
import java.util.TreeMap;

public class CommunicationPacket implements Serializable {

    private Integer communicationID;
    private Session session;
    private Object data;
    private boolean bool;

    public CommunicationPacket() {

    }

    public CommunicationPacket(Billboard billboard) {
        this.communicationID = 10;
        this.data = billboard;
    }

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
