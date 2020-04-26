package Server.Database;

import Server.Database.Database;
import Server.Server;
import Shared.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ScheduleDatabase extends Database {

    private ResultSet resultSet;
    private LocalDate localDate;
    private SimpleDateFormat dateFormatHourMinSec = new SimpleDateFormat("HH:mm:ss");
    private SimpleDateFormat dateFormatAll = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public ScheduleDatabase(Properties properties) {
        super(properties);
        localDate = LocalDate.now();

    }

    public ArrayList<Event> getSchedule() {
        getDatabaseData();
        return formatSchedule();
    }

    private void getDatabaseData() {
        String currentDate = DateTimeFormatter.ofPattern("yyyy/MM/dd").format(localDate);
        String weekDate = DateTimeFormatter.ofPattern("yyyy/MM/dd").format(localDate.plus(1, ChronoUnit.WEEKS));
        String sqlSelect = "select * from schedule where date >= \"" + currentDate + "\" and date <= \"" + weekDate + "\" ORDER BY date, inputDate DESC, startTime";
        resultSet = super.runSelectQuery(sqlSelect);
    }

    private ArrayList<Event> formatSchedule() {
        ArrayList<Event> schedule = new ArrayList<>();
        try {
            while(resultSet.next()) {
                ArrayList<Date[]> eventTimes = getEventTimes(resultSet);
                Integer billboardID = resultSet.getInt("billboardID");

                for (Date[] eventPair: eventTimes) {
                    Event newEvent;
                    if (schedule.size() > 0) {
                        newEvent = generateEvent(schedule, billboardID, eventPair);
                    } else {
                        newEvent = new Event(billboardID, eventPair[0], eventPair[1]);
                    }
                    schedule.add(newEvent);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return schedule;
    }

    private Event generateEvent(ArrayList<Event> schedule, Integer billboardID, Date[]eventPair) {
        Event event = new Event();
        event.setBillboardID(billboardID);

        for (Event scheduled: schedule) {

            boolean breakCondition = false;

            if (eventPair[0].compareTo(scheduled.getStartTime()) >= 0 && eventPair[0].compareTo(scheduled.getEndTime()) <= 0) {
                event.setStartTime(scheduled.getEndTime());
                breakCondition = true;
            } else {
                event.setStartTime(eventPair[0]);
            }

            if (eventPair[1].compareTo(scheduled.getStartTime()) >= 0 && eventPair[1].compareTo(scheduled.getEndTime()) <= 0) {
                event.setEndTime(scheduled.getStartTime());
                breakCondition = true;
            } else {
                event.setEndTime(eventPair[1]);
            }

            if (breakCondition) {
                break;
            }
        }
        return event;
    }

    private ArrayList<Date[]> getEventTimes(ResultSet result) {
        ArrayList<Date[]> eventTimes = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        Calendar durationInterval = Calendar.getInstance();
        Date[] eventInformation = getEventInformation(result);

        calendar.setTime(eventInformation[0]);

        while(calendar.getTime().compareTo(eventInformation[1]) < 0) {
            Date[] eventPair = new Date[2];

            eventPair[0] = calendar.getTime();
            durationInterval.setTime(eventInformation[2]);

            calendar.add(Calendar.HOUR_OF_DAY, durationInterval.get(Calendar.HOUR_OF_DAY));
            calendar.add(Calendar.MINUTE, durationInterval.get(Calendar.MINUTE));

            if(calendar.getTime().compareTo(eventInformation[1]) > 0) {
                eventPair[1] = eventInformation[1];
            } else {
                eventPair[1] = calendar.getTime();
            }

            eventTimes.add(eventPair);

            durationInterval.setTime(eventInformation[3]);
            calendar.add(Calendar.HOUR_OF_DAY, durationInterval.get(Calendar.HOUR_OF_DAY));
            calendar.add(Calendar.MINUTE, durationInterval.get(Calendar.MINUTE));
        }
        return eventTimes;
    }

    private Date[] getEventInformation(ResultSet result) {
        Date[] returnValue = new Date[4];
        try {
            returnValue[0] = dateFormatAll.parse((result.getString("date")+ " " +result.getString("startTime")));
            returnValue[1] = dateFormatAll.parse((result.getString("date")+ " " +result.getString("endTime")));
            returnValue[2] = dateFormatHourMinSec.parse(result.getString("duration"));
            returnValue[3] =  dateFormatHourMinSec.parse(result.getString("interval"));
        } catch (ParseException | SQLException e) {
            e.printStackTrace();
        }
        return returnValue;
    }
}
