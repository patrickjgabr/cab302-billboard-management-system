package Shared;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

/**
 * The Event class primarily provides a Object which can represent one of many possible instances of a scheduled Billboard based on a Schedule Class.
 * If a Schedule class was broken down into its individual "events" based upon its time related properties, this class represents one of the possibly many "events".
 * Subsequently this class only contains a day, startTime, endTime and eventID, referencing the "parent" Schedule objects scheduleID, to define the day and time the scheduled Billboard Object is required to display during
 */
public class Event {

    private int eventID;
    private int startTime;
    private int endTime;
    private int day;
    private String creatorName;
    private String BillboardName;


    /**
     * Default constructor used to instantiate a Event Object
     * @param eventID Associated Schedule Objects scheduleID
     * @param day Scheduled Day
     * @param startTime Scheduled Start time in minutes of the day
     * @param duration Scheduled Duration in minutes
     * @param creatorName Name of Schedule Objects creator
     * @param BillboardName Name of Schedule Objects scheduled Billboard
     */
    public Event (int eventID, int day, int startTime, int duration, String creatorName, String BillboardName) {
        this.eventID = eventID;
        this.day = day;
        this.startTime = startTime;
        this.endTime = startTime + duration;
        this.creatorName = creatorName;
        this.BillboardName = BillboardName;
    }

    /**
     * Returns the Events eventID
     * @return Events ID
     */
    public int getEventID() {
        return eventID;
    }

    /**
     * Returns the Events day between 0 and 6 (0 being Sunday and Saturday being 6)
     * @return Event day
     */
    public int getDay() {
        return day;
    }

    /**
     * Returns the Events end time between 0 and 1440
     * @return Event end time
     */
    public int getEndTime() {
        return endTime;
    }

    /**
     * Returns the Events start time between 0 and 1440
     * @return Event start time
     */
    public int getStartTime() {
        return startTime;
    }

    /**
     * Returns the name of the Event creator
     * @return Creators name
     */
    public String getCreatorName() {
        return creatorName;
    }

    /**
     * Returns the name of the Events Billboard
     * @return Billboard name
     */
    public String getBillboardName() {
        return BillboardName;
    }
}
