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

    public ArrayList<Scheduled> getSchedule() throws Throwable {
        getDatabaseData();

        ArrayList<Scheduled> returnList = new ArrayList<Scheduled>();
        return  returnList;
    }

    private void getDatabaseData() throws Throwable {
        String currentDate = DateTimeFormatter.ofPattern("yyyy/MM/dd").format(localDate);
        String weekDate = DateTimeFormatter.ofPattern("yyyy/MM/dd").format(localDate.plus(1, ChronoUnit.WEEKS));
        String sqlSelect = "select * from schedule ORDER BY scheduleID, inputDate DESC";
        resultSet = super.runSelectQuery(sqlSelect);
    }


}
