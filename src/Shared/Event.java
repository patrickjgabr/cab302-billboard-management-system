package Shared;

import java.util.Date;

public class Event {

    private Billboard billboard;
    private Integer billboardID;
    private Date startTime;
    private Date endTime;

    public Event (Integer billboardID, Date startTime, Date endTime) {
        this.billboardID = billboardID;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Event() {

    }

    public Billboard getBillboard() {
        return billboard;
    }

    public void setBillboard(Billboard billboard) {
        this.billboard = billboard;
    }

    public Integer getBillboardID() {
        return billboardID;
    }

    public void setBillboardID(Integer billboardID) {
        this.billboardID = billboardID;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
