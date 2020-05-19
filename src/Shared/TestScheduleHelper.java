package Shared;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TestScheduleHelper {

    private Calendar calendar;
    private User user;
    private Event event;
    private Scheduled scheduled;
    private ArrayList<Scheduled> schedule;
    private ArrayList<Event> events;
    private ArrayList<Billboard> billboards;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {

    }

    @Test
    public void TestGenerateEvents() {
        assertThrows(Exception.class, () -> {
            ArrayList<Integer> permissions = new ArrayList<>();
            permissions.add(1);
            permissions.add(1);
            permissions.add(1);
            permissions.add(1);
            ArrayList<Billboard> billboards = new ArrayList<>();
            User user = new User("test", "password", permissions, 1, "testsalt" );
            billboards.add(new Billboard("creatorName", "name", "imageUrl", "msgText", "", "#000FFF", "infoText", ""));
            ArrayList<Scheduled> schedule = new ArrayList<>();
            schedule.add(new Scheduled(user.getUserID(), billboards.get(0).getBillboardID(), ScheduleHelper.DateTime(0,0,0,0), ScheduleHelper.DateTime(0,1,0,0), 60, new int[]{0,0,0}));
            schedule.add(new Scheduled(user.getUserID(), billboards.get(0).getBillboardID(), ScheduleHelper.DateTime(1,0,0,0), ScheduleHelper.DateTime(1,1,0,0), 60, new int[]{1,3,30}));
            ScheduleHelper.GenerateEvents(schedule);
        });

    }

    @Test
    //billboard scheduled to start and end on different days will throw an exception.
    public void TestGenerateEventsInvalid() {
        assertThrows(Exception.class, () -> {
            ArrayList<Integer> permissions = new ArrayList<>();
            permissions.add(1);
            permissions.add(1);
            permissions.add(1);
            permissions.add(1);
            ArrayList<Billboard> billboards = new ArrayList<>();
            User user = new User("test", "password", permissions, 1, "testsalt" );
            billboards.add(new Billboard("creatorName", "name", "imageUrl", "msgText", "", "#000FFF", "infoText", ""));
            ArrayList<Scheduled> schedule = new ArrayList<>();
            schedule.add(new Scheduled(user.getUserID(), billboards.get(0).getBillboardID(), ScheduleHelper.DateTime(0,0,0,0), ScheduleHelper.DateTime(1,1,0,0), 60, new int[]{0,0,0}));
            schedule.add(new Scheduled(user.getUserID(), billboards.get(0).getBillboardID(), ScheduleHelper.DateTime(1,0,0,0), ScheduleHelper.DateTime(1,1,0,0), 60, new int[]{0,0,0}));
            ScheduleHelper.GenerateEvents(schedule);
        });
    }

    @Test
    public void TestGenerateEventsNonScheduled() {
        assertThrows(Exception.class, () -> {
            ArrayList<Scheduled> emptyschedule = new ArrayList<>();
            ArrayList<Event> events = ScheduleHelper.GenerateEvents(emptyschedule);
        });

    }

    @Test
    public void TestConstructDateTime() throws Exception {
        Calendar expectedobject = new GregorianCalendar();
        expectedobject.set(Calendar.DAY_OF_WEEK, 1);
        expectedobject.set(Calendar.HOUR_OF_DAY, 7);
        expectedobject.set(Calendar.MINUTE, 30);
        expectedobject.set(Calendar.SECOND, 0);
        expectedobject.set(Calendar.MILLISECOND, 0);
        expectedobject.set(Calendar.AM_PM, Calendar.AM);
        Calendar object = ScheduleHelper.DateTime(1,7,30,0);
        assertEquals(expectedobject, object);
    }

    @Test
    public void TestConstructTimeDayInvalid() {
        assertThrows(Exception.class, () -> {
            Calendar result = ScheduleHelper.DateTime(-10,40,-13,3);
        });
    }

    @Test
    public void TestConstructTimeDayEdgeCase() throws Exception {
        Calendar expectedobject = new GregorianCalendar();
        expectedobject.set(Calendar.DAY_OF_WEEK, 300);
        expectedobject.set(Calendar.HOUR_OF_DAY, 64);
        expectedobject.set(Calendar.MINUTE, 122);
        expectedobject.set(Calendar.SECOND, 0);
        expectedobject.set(Calendar.MILLISECOND, 0);
        expectedobject.set(Calendar.AM_PM, Calendar.PM);
        Calendar object = ScheduleHelper.DateTime(300, 64,122,1);
        assertEquals(expectedobject, object);
    }

    @Test
    //Checks if an exception is thrown if the period is neither 0 or 1
    public void TestConstructTimeDayInvalidPeriod() throws Exception {
        assertThrows(Exception.class, () -> {
            Calendar result = ScheduleHelper.DateTime(3,1,30,3);
        });
    }

}