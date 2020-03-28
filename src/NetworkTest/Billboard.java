import com.sun.source.tree.Tree;

import java.io.Serializable;
import java.util.TreeMap;

public class Billboard implements Serializable {

    private String name;
    private Integer creatorID;
    private String imageUrl;
    private String messageText;
    private String messageTextColour;
    private String backgroundColour;
    private String informationText;
    private String informationTextColour;
    private TreeMap<String, String> billboardExport;

    public Billboard(TreeMap<String, String> billboardInformation) {
        this.name = billboardInformation.get("Name");
        this.creatorID = Integer.parseInt(billboardInformation.get("CreatorID"));
        this.imageUrl = billboardInformation.get("ImageUrl");
        this.messageText = billboardInformation.get("MessageText");
        this.messageTextColour = billboardInformation.get("MessageTextColour");
        this.backgroundColour = billboardInformation.get("BackgroundColour");
        this.informationText = billboardInformation.get("InformationText");
        this.informationTextColour = billboardInformation.get("InformationTextColour");
        this.billboardExport = billboardInformation;
    }

    public String generateXML() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<billboard background=\"#" + backgroundColour + "\">\n" +
                "<message colour=\"#" + messageTextColour + "\">" + messageTextColour + "</message>\n" +
                "<picture url=\"" + imageUrl + "\"/>\n" +
                "<information colour=\"#" + informationTextColour + "\">" + informationText + "</information>\n" +
                "<creator ID=\"" + creatorID + "\">" +
                "</billboard>";
    }

    public String getName() {
        return name;
    }

    public String getBackgroundColour() {
        return backgroundColour;
    }

    public String getInformationText() {
        return informationText;
    }

    public String getInformationTextColour() {
        return informationTextColour;
    }

    public String getPictureLink() {
        return imageUrl;
    }

    public Integer getCreatorID() {
        return creatorID;
    }

    public String getMessageText() { return messageText; }

    public String getMessageTextColour() { return messageTextColour; }

    public TreeMap<String, String> getBillboardExport() { return billboardExport; }
}
