package Shared;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ScheduleHelper {
    public static Calendar DateTime (int day, int hour, int minute, int period) {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_WEEK, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
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
            if (x.getInterval(1) == 0 ){
                events.add(new Event(x.getID(), x.getStartTime(),x.getEndTime()));
            }
            if (x.getInterval(1) == 1 ){
                Calendar starttime = x.getStartTime();
                Calendar endtime = x.getEndTime();
                for(int i = 0; i < 7; i++){
                    starttime.set(Calendar.DAY_OF_WEEK,i);
                    endtime.set(Calendar.DAY_OF_WEEK,i);
                    events.add(new Event(x.getID(), starttime,endtime));
                }
            }
            if (x.getInterval(1) == 2 ){
                Calendar starttime = x.getStartTime();
                Calendar endtime = x.getEndTime();
                for(int i = 0; i < x.getInterval(2); i++){
                    events.add(new Event(x.getID(), starttime,endtime));
                    starttime.add(Calendar.HOUR,x.getInterval(3));
                    endtime.add(Calendar.HOUR,x.getInterval(3));
                }
            }
            if (x.getInterval(1) == 3 ){
                Calendar starttime = x.getStartTime();
                Calendar endtime = x.getEndTime();
                for(int i = 0; i < x.getInterval(2); i++){
                    events.add(new Event(x.getID(), starttime,endtime));
                    starttime.add(Calendar.MINUTE,x.getInterval(3));
                    endtime.add(Calendar.MINUTE,x.getInterval(3));
                }
            }
        }
        return events;
    }
}