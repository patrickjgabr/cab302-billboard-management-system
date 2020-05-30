package Shared;

import java.io.*;

/**
 * The Billboard class provides a storage structure for Billboard objects which need to be networked between components including the Server, Control Panel and Viewer.
 * This class provides a highly pliable method to instantiating, storing and editing Billboards and their characters through its set of constructors and getter setter methods.
 */

public class Billboard implements Serializable {

    private String name;
    private Integer billboardID;
    private String creatorName;
    private String imageUrl;
    private String messageText;
    private String messageTextColour;
    private String backgroundColour;
    private String informationText;
    private String informationTextColour;
    private Integer scheduled;

    /**
     * Constructor which instantiates a Billboard Object containing the input data
     * @param creatorName User name of Billboard Creator
     * @param name Billboard Name
     * @param imageUrl Image URL or data
     * @param msgText Billboard main message text (Top)
     * @param msgColour Billboard main message colour (Top)
     * @param bgColour Billboard background colour
     * @param infoText Billboard information text (Bottom)
     * @param infoColour Billboard information text colour (Bottom)
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

    /**
     * Constructor which instantiates a Billboard Object from a byte array containing the relevant Billboard properties.
     * This method is designed to recreate Billboards from the output of the getByteArray method
     * @param bytes Billboard Object as a byte array
     */
    public Billboard(byte[] bytes){
        try {

            //Instantiate a ObjectInputStream using the given byte array
            ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));

            //Read the given object
            Object bb = inputStream.readObject();

            //Cast the Object to Billboard
            Billboard billboard = (Billboard) bb;

            //Assign the properties of Billboard to the properties of this
            this.creatorName = billboard.creatorName;
            this.name = billboard.name;
            this.imageUrl = billboard.imageUrl;
            this.messageText = billboard.messageText;
            this.messageTextColour = billboard.messageTextColour;
            this.backgroundColour = billboard.backgroundColour;
            this.informationText = billboard.informationText;
            this.informationTextColour = billboard.informationTextColour;
            this.billboardID = billboard.billboardID;
            this.scheduled = billboard.scheduled;

        } catch (IOException | ClassNotFoundException e) { }
    }

    /**
     * Default constructor used to instantiate a Billboard Object with no properties
     */
    public Billboard() {

    }

    /**
     * Generates and returns a XML String containing all of the billboards information.
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
     * Returns the Billboards name
     * @return Billboard Name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the Billboards background colour in the format #000000
     * @return Billboard background colour
     */
    public String getBackgroundColour() {
        return backgroundColour;
    }

    /**
     * Returns the Billboards information text
     * @return Billboard information text
     */
    public String getInformationText() {
        return informationText;
    }

    /**
     * Returns the Billboards information text colour in the format #000000
     * @return Billboard information text
     */
    public String getInformationTextColour() {
        return informationTextColour;
    }

    /**
     * Returns the Billboards message text
     * @return Billboard message text
     */

    public String getMessageText() { return messageText; }

    /**
     * Returns the Billboards message text colour in the format #000000
     * @return Billboard message text colour
     */
    public String getMessageTextColour() { return messageTextColour; }

    /**
     * Returns the username of the Billboards creator
     * @return Billboard creators username
     */
    public String getCreatorName() { return creatorName; }

    /**
     * Returns the Billboards billboardID
     * @return Billboard billboardID
     */
    public Integer getBillboardID() { return billboardID; }

    /**
     * Returns the schedule status of the Billboard
     * @return Billboard schedule status (1 scheduled)
     */
    public Integer getScheduled() {
        return scheduled;
    }

    /**
     *Sets the creatorName of the Billboard Object to the given value
     * @param creatorName New creator name
     */
    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    /**
     *Sets the backgroundColour of the Billboard Object to the given value
     * @param backgroundColour New backgroundColour
     */
    public void setBackgroundColour(String backgroundColour) {
        this.backgroundColour = backgroundColour;
    }

    /**
     * Returns the Billboards image link or data
     * @return Billboard picture link or data
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the imageUrl of the Billboard Object to the given value
     * @param imageUrl New imageUrl
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Sets the informationText of the Billboard Object to the given value
     * @param informationText New informationText
     */
    public void setInformationText(String informationText) {
        this.informationText = informationText;
    }

    /**
     * Sets the informationText colour of the Billboard Object to the given value
     * @param informationTextColour New informationText colour
     */
    public void setInformationTextColour(String informationTextColour) {
        this.informationTextColour = informationTextColour;
    }

    /**
     *Sets the messageText of the Billboard Object to the given value
     * @param messageText New messageText
     */
    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    /**
     * Sets messageText colour of the Billboard Object to the given value
     * @param messageTextColour New messageText colour
     */
    public void setMessageTextColour(String messageTextColour) {
        this.messageTextColour = messageTextColour;
    }

    /**
     * Sets the name of the Billboard Object to the given value
     * @param name New name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns a byte array representing the Billboard Object
     * @return Billboard byte array
     */
    public byte [] getByteArray(){

        //Instantiates a ByteArrayOutputStream, ObjectOutputStream and bye array
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        ObjectOutputStream objOutput = null;
        byte [] data;

        //Attempt to convert write this object to the ObjectOutputStream and covert it into a byte array
        try {

            //Ingratiate the ObjectOutputStream to use byteOutput
            objOutput = new ObjectOutputStream(byteOutput);

            //Write this object to it and get the resulting bytes
            objOutput.writeObject(this);
            data = byteOutput.toByteArray();
        } catch (IOException e) {
            data = new byte[]{0};
        }

        //Return byte array
        return data;
    }

    /**
     * Sets the billboardID of the Billboard Object to the given value
     * @param billboardID New billboardID
     */
    public void setBillboardID(Integer billboardID) { this.billboardID = billboardID; }

    /**
     * Sets the scheduled status of the Billboard Object to the given value (Must be 1 or 0)
     * @param scheduled New scheduled status
     */
    public void setScheduled(Integer scheduled) {

        //If the scheduled value is valid (1 or 0) then set it to the scheduled value
        if(scheduled == 0 || scheduled == 1) {
            this.scheduled = scheduled;
        }
    }
}
