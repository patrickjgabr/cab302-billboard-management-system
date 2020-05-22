package Shared;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ScheduleHelper {
    public static Calendar DateTime (int day, int hour, int minute, int period) {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_WEEK, day);
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0); // seconds always 0
        if (period == 0 ){
            calendar.set(Calendar.AM_PM, Calendar.AM);
        }
        if (period == 1 ){
            calendar.set(Calendar.AM_PM, Calendar.PM);
        }
        return calendar;
    }

    public static ArrayList<Event> GenerateEvents (ArrayList<Scheduled> schedule) {
        ArrayList<Event> events = new ArrayList<>();
        for (Scheduled x : schedule) {
            if (x.getInterval(0) == 0 ){
                events.add(new Event(x.getID(), x.getStartTime(),x.getDuration()));
            }
            if (x.getInterval(0) == 1 ){
                Calendar starttime = x.getStartTime();
                for(int i = 0; i < 7; i++){
                    starttime.set(Calendar.DAY_OF_WEEK,i);
                    events.add(new Event(x.getID(), starttime,x.getDuration()));
                }
            }
            if (x.getInterval(0) == 2 ){
                Calendar starttime = x.getStartTime();
                for(int i = 0; i < x.getInterval(1); i++){
                    events.add(new Event(x.getID(), starttime,x.getDuration()));
                    starttime.add(Calendar.HOUR,x.getInterval(2));
                }
            }
            if (x.getInterval(0) == 3 ){
                Calendar starttime = x.getStartTime();
                for(int i = 0; i < x.getInterval(1); i++){
                    events.add(new Event(x.getID(), starttime,x.getDuration()));
                    starttime.add(Calendar.MINUTE,x.getInterval(2));
                }
            }
        }
        return events;
    }
}