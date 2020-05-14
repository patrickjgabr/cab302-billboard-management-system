package Shared;

import java.sql.Time;
import java.util.Date;

public class Event {

    private int eventID;
    private Time startTime;
    private Time endTime;
    private int day;

    public Event (int eventID, Time startTime, Time endTime) {
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

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }
}
