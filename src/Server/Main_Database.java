package Server;

import Shared.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main_Database {

    public static void main(String[] args) {

        try {
            Properties properties = new Properties();
            ScheduleDatabase scheduleDb = new ScheduleDatabase(properties);
            ArrayList<Event> schedule = scheduleDb.getSchedule();

            for (Event event: schedule) {
                System.out.println("BillboardID: " + event.getBillboardID() + " At start time: " + event.getStartTime() + " At end time: " + event.getEndTime());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
