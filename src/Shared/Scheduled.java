package Shared;

import java.awt.image.BufferedImage;
import java.util.Calendar;

public class Scheduled {
    private int id;
    private String name;
    private String creator;
    private Calendar startTime;
    private Calendar endTime;
    private int duration;
    //private BufferedImage preview; Preview image later

    public Scheduled(int id, String name, String creator, Calendar startTime, Calendar endTime, int duration){
        this.id = id;
        this.name = name;
        this.creator = creator;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCreator() {
        return creator;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public int getDuration() {
        return duration;
    }
}
