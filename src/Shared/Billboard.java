package Shared;

import java.io.Serializable;
import java.util.TreeMap;

/**
 * v1
 * Class used to create billboard objects
 */

public class Billboard implements Serializable {

    private String name;
    private Integer billboardID = 0;
    private String creatorName;
    private String imageUrl;
    private String messageText;
    private String messageTextColour;
    private String backgroundColour;
    private String informationText;
    private String informationTextColour;

    /**
     * Constructs and initalizes a Billboard object
     */
    public Billboard(String creatorName, String name, String imageUrl, String msgText, String msgColour, String bgColour, String infoText, String infoColour) {
        this.creatorName = creatorName;
        this.name = name;
        this.imageUrl = imageUrl;
        this.messageText = msgText;
        this.messageTextColour = msgColour;
        this.backgroundColour = bgColour;
        this.informationText = infoText;
        this.informationTextColour = infoColour;
    }

    public Billboard() {

    }

    /**
     * Generates and returns an XML String containing all of the billboards information.
     * @return XML Billboard String
     */
    public String generateXML() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<billboard background=\"#" + backgroundColour + "\">\n" +
                "<message colour=\"#" + messageTextColour + "\">" + messageTextColour + "</message>\n" +
                "<picture url=\"" + imageUrl + "\"/>\n" +
                "<information colour=\"#" + informationTextColour + "\">" + informationText + "</information>\n" +
                "<creator Name=\"" + creatorName + "\">" +
                "</billboard>";
    }

    /**
     * Returns the billboards name
     * @return Billboards Name
     */

    public String getName() {
        return name;
    }

    /**
     * Returns the billboards background colour
     * @return Billboards background colour
     */

    public String getBackgroundColour() {
        return backgroundColour;
    }

    /**
     * Returns the billboards information text
     * @return Billboards information text
     */

    public String getInformationText() {
        return informationText;
    }

    /**
     * Returns the billboards information text colour in the format #000000
     * @return Billboards information text
     */

    public String getInformationTextColour() {
        return informationTextColour;
    }

    /**
     * Returns the billboards picture link
     * @return Billboards picture link
     */

    public String getPictureLink() {
        return imageUrl;
    }

    /**
     * Returns the ID of the user who created the billboard
     * @return Billboards creatorID
     */

    public String getMessageText() { return messageText; }

    /**
     * Returns the billboards message text colour in the format #000000
     * @return Billboards message text colour
     */

    public String getMessageTextColour() { return messageTextColour; }

    /**
     * Returns all of the information about the billboard in a TreeMap
     * @return TreeMap<String, String> where the Key and value pair represents the billboards information
     */

    public String getCreatorName() { return creatorName; }

    public Integer getBillboardID() { return billboardID; }

    public void setBillboardID(Integer billboardID) { this.billboardID = billboardID; }

    //public TreeMap<String, String> getBillboardExport() { return billboardExport; }
}
