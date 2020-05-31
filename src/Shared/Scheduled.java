package Shared;
import java.io.*;

/**
 * The Scheduled class provides a storage structure for Schedule Object which need to be networked between components including the Server, Control Panel and Viewer.
 * This class provides a highly pliable method of instantiating, storing and editing Schedule's and their properties through its set of constructors and getter setter methods.
 */
public class Scheduled implements Serializable{
    private int ID;
    private Integer billboardID;
    private String billboardName;
    private Integer creatorID;
    private String creatorName;
    private int start;
    private int day;
    private int duration;
    private int[] interval;

    /**
     * Constructor which instantiates a Scheduled Object containing the input data
     * @param creatorID Users userID of Scheduled Creator
     * @param creatorName Users username of Scheduled Creator
     * @param billboardID Billboards billboardID that's Scheduled
     * @param billboardName Billboards billboardName that's Scheduled
     * @param start Scheduled start time in minutes (0 - 1440)
     * @param duration Scheduled duration time in minutes
     * @param interval Scheduled interval
     */
    public Scheduled(Integer creatorID, String creatorName, Integer billboardID, String billboardName, int[] start, int duration, int[] interval){
        this.billboardID = billboardID;
        this.creatorID = creatorID;
        this.billboardName = billboardName;
        this.creatorName = creatorName;
        this.day = start[0];
        this.start = start[1];
        this.duration = duration;
        this.interval = interval;
    }

    /**
     * Constructor which instantiates a Scheduled Object from a byte array containing the relevant Scheduled properties.
     * This method is designed to recreate a Scheduled Object from the output of the getByteArray method
     * @param bytes Billboard Object as a byte array
     */
    public Scheduled(byte[] bytes) {
        try {
            //Instantiate a ObjectInputStream using the given byte array
            ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));

            //Read the given object
            Object s = inputStream.readObject();

            //Cast the Object to Scheduled
            Scheduled schedule = (Scheduled) s;

            //Assign the properties of Scheduled to the properties of this
            this.billboardID = schedule.billboardID;
            this.creatorID = schedule.creatorID;
            this.day = schedule.day;
            this.start = schedule.start;
            this.duration = schedule.duration;
            this.interval = schedule.interval;
            this.billboardName = schedule.billboardName;
            this.creatorName = schedule.creatorName;

        } catch (IOException | ClassNotFoundException e) {

        }
    }

    /**
     * Default constructor used to instantiate a Scheduled Object with no properties
     */
    public Scheduled() {

    }

    /**
     *
     * @return
     */
    public int getID() {
        return ID;
    }

    /**
     *
     * @param ID
     */
    public void setID(int ID) {
        this.ID = ID;
    }

    /**
     *
     * @return
     */
    public Integer getBillboardID() {
        return billboardID;
    }

    /**
     *
     * @param billboardID
     */
    public void setBillboardID(Integer billboardID) {
        this.billboardID = billboardID;
    }

    /**
     *
     * @return
     */
    public Integer getCreatorID() {
        return creatorID;
    }

    /**
     *
     * @param creatorID
     */
    public void setCreatorID(Integer creatorID) {
        this.creatorID = creatorID;
    }

    /**
     *
     * @return
     */
    public int getDay() {
        return day;
    }

    /**
     *
     * @param type
     * @return
     */
    public int getInterval(int type) {
        return interval[type];
    }

    /**
     *
     * @return
     */
    public int getStartTime() {
        return start;
    }

    /**
     *
     * @return
     */
    public int getDuration() {
        return duration;
    }

    /**
     *
     * @return
     */
    public String getBillboardName() {
        return billboardName;
    }

    /**
     *
     * @return
     */
    public String getCreatorName() {
        return creatorName;
    }

    /**
     *
     * @param day
     */
    public void setDay(int day) {
        this.day = day;
    }

    /**
     *
     * @param duration
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     *
     * @param interval
     */
    public void setInterval(int[] interval) {
        this.interval = interval;
    }

    /**
     *
     * @param start
     */
    public void setStart(int start) {
        this.start = start;
    }

    /**
     *
     * @param billboardName
     */
    public void setBillboardName(String billboardName) {
        this.billboardName = billboardName;
    }

    /**
     *
     * @param creatorName
     */
    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    /**
     *
     * @return
     */
    public byte [] getByteArray(){
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        ObjectOutputStream objOutput;
        byte [] data;
        try {
            objOutput = new ObjectOutputStream(byteOutput);
            objOutput.writeObject(this);
            data = byteOutput.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            data = new byte[]{0};
        }

        return data;
    }
}



