package Shared;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ScheduleHelper {
    public static int[] CalculateStart (int day, int hour, int minute, int period) {
        int[] calendar = new int[2];
        calendar[0] = day;
        if (period ==0) {
            calendar[1] = 0;
        }
        if (period ==1) {
            calendar[1] = 720;
        }
        calendar[1] += (hour *60) + minute;
        return calendar;
    }

    public static ArrayList<Event> GenerateEvents (ArrayList<Scheduled> schedule) {
        ArrayList<Event> events = new ArrayList<>();
        for (Scheduled x : schedule) {
            System.out.println(x.getStartTime());
            if (x.getInterval(0) == 0 ){
                events.add(new Event(x.getID(), x.getDay(), x.getStartTime(),x.getDuration()));
            }
            if (x.getInterval(0) == 1 ){
                int starttime = x.getStartTime();
                for(int i = 0; i < 7; i++){
                    events.add(new Event(x.getID(), i, x.getStartTime(),x.getDuration()));
                }
            }
            if (x.getInterval(0) == 2 ){
                System.out.println("Here");
                int starttime = x.getStartTime();
                for(int i = 0; i < x.getInterval(1); i++){
                    System.out.println(x.getID()+ " " + x.getDay()+ " " +  starttime+ " " + x.getDuration());
                    events.add(new Event(x.getID(), x.getDay(), starttime,x.getDuration()));
                    starttime += 60;
                }
            }
            if (x.getInterval(0) == 3 ){
                int starttime = x.getStartTime();
                for(int i = 0; i < x.getInterval(1); i++){
                    events.add(new Event(x.getID(), x.getDay(),starttime,x.getDuration()));
                    starttime += x.getInterval(2);
                }
            }
        }
        return events;
    }
}