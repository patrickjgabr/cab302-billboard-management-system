package Shared;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ScheduleHelper {
    public static int[] CalculateStart (int day, int hour, int minute, int period) {
        int[] calendar = new int[2];
        calendar[0] = day;
        if (period ==1) {
            calendar[1] = 720;
        }
        calendar[1] = calendar[1] + (hour *60) + minute;
        return calendar;
    }

    public static ArrayList<Event> GenerateEvents (ArrayList<Scheduled> schedule) {
        ArrayList<Event> events = new ArrayList<>();
        for (Scheduled x : schedule) {
            if (x.getInterval(0) == 0 ){
                events.add(new Event(x.getID(), x.getDay(), x.getStartTime(),x.getDuration(), x.getCreatorName(),x.getBillboardName()));
            }
            if (x.getInterval(0) == 1 ){
                int starttime = x.getStartTime();
                for(int i = 1; i <= 7; i++){
                    events.add(new Event(x.getID(), i, x.getStartTime(),x.getDuration(), x.getCreatorName(),x.getBillboardName()));
                }
            }
            if (x.getInterval(0) == 2 ){
                int starttime = x.getStartTime();
                for(int i = 0; i < x.getInterval(1); i++){
                    events.add(new Event(x.getID(), x.getDay(), starttime,x.getDuration(), x.getCreatorName(),x.getBillboardName()));
                    starttime += 60;
                }
            }
            if (x.getInterval(0) == 3 ){
                int starttime = x.getStartTime();
                for(int i = 0; i < x.getInterval(1); i++){
                    events.add(new Event(x.getID(), x.getDay(),starttime,x.getDuration(), x.getCreatorName(),x.getBillboardName()));
                    starttime += x.getInterval(2);
                }
            }
        }
        return events;
    }
}