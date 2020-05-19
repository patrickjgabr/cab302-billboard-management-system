package Shared;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

public class Event {

    private int eventID;
    private Calendar startTime;
    private Calendar endTime;


    public Event (int eventID, Calendar startTime, Calendar endTime) {
        this.eventID = eventID;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Event() {

    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
    }
}
