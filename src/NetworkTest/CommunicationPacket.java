import java.io.Serializable;
import java.util.TreeMap;

public class CommunicationPacket implements Serializable {

    private Integer communicationID;
    private TreeMap<String, String> information;

    public CommunicationPacket(TreeMap<String, String> information, Integer communicationID) {
        this.communicationID = communicationID;
        this.information = information;
    }

    public Integer getCommunicationID() {
        return communicationID;
    }

    public void setCommunicationID(Integer communicationID) {
        this.communicationID = communicationID;
    }

    public TreeMap<String, String> getInformation() {
        return information;
    }

    public void setInformation(TreeMap<String, String> information) {
        this.information = information;
    }
}
