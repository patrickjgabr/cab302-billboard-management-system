package Shared;

import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Scheduled implements Serializable{
    private int ID;
    private Integer billboardID;
    private Integer creatorID;
    private int start;
    private int day;
    private int duration;
    private int[] interval;

    public Scheduled(Integer creatorID, Integer billboardID, int[] start, int duration, int[] interval){
        this.billboardID = billboardID;
        this.creatorID = creatorID;
        this.day = start[0];
        this.start = start[1];
        this.duration = duration;
        this.interval = interval;
    }

    public Scheduled(byte[] bytes) {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
            Object s = inputStream.readObject();
            Scheduled schedule = (Scheduled) s;


            this.billboardID = schedule.billboardID;
            this.creatorID = schedule.creatorID;
            this.day = schedule.day;
            this.start = schedule.start;
            this.duration = schedule.duration;
            this.interval = schedule.interval;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
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

    public int getDay() {
        return day;
    }

    public int getInterval(int type) {
        return interval[type];
    }



    public int getStartTime() {
        return start;
    }


    public int getDuration() {
        return duration;
    }



    public byte [] getByteArray(){
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        ObjectOutputStream objOutput = null;
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



