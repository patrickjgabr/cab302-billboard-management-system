package Shared;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

public class Event {

    private int eventID;
    private int startTime;
    private int endTime;
    private int day;


    public Event (int eventID, int day, int startTime, int duration) {
        this.eventID = eventID;
        this.day = day;
        this.startTime = startTime;
        this.endTime = startTime + duration;
    }

    public Event() {

    }


    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public int getDay() {
        return day;
    }

    public int getEndTime() {
        return endTime;
    }

    public int getStartTime() {
        return startTime;
    }
}
