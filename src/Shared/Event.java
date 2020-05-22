package Shared;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

public class Event {

    private int eventID;
    private int startTime;
    private int endTime;
    private int day;


    public Event (int eventID, Calendar startTime, int duration) {
        this.eventID = eventID;
        this.day = startTime.get(Calendar.DAY_OF_WEEK);
        this.startTime = toMinutes(startTime);
        this.endTime = toMinutes(startTime) + duration;
    }

    public Event() {

    }

    private int toMinutes(Calendar time){
        int minutes = (
                time.get(Calendar.MINUTE) +
                time.get(Calendar.HOUR_OF_DAY) * 60);
        return minutes;
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
