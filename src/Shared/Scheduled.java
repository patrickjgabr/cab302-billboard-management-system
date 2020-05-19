package Shared;

import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Scheduled {
    private int ID;
    private Integer billboardID;
    private Integer creatorID;
    private Calendar startTime;
    private Calendar endTime;
    private int duration;
    private int[] interval;

    public Scheduled(Integer creatorID, Integer billboardID, Calendar startTime, Calendar endTime, int duration, int[] interval){
        this.billboardID = billboardID;
        this.creatorID = creatorID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.interval = interval;
    }

    public Scheduled() {

    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Integer getBillboardID() {
        return billboardID;
    }

    public void setBillboardID(Integer billboardID) {
        this.billboardID = billboardID;
    }

    public Integer getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(Integer creatorID) {
        this.creatorID = creatorID;
    }

    public int[] getInterval() {
        return interval;
    }

    public void setInterval(int[] interval) {
        this.interval = interval;
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

    public Time getDuration() {
        return duration;
    }

    public void setDuration(Time duration) {
        this.duration = duration;
    }
}



