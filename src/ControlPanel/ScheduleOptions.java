package ControlPanel;

import Shared.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Objects;

public class ScheduleOptions {
    private String username;
    private Client client;
    private String token;
    private String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private String[] periods = {"AM", "PM"};
    private ArrayList<Billboard> billboards;
    private String[] hours = {"12","1","2","3","4","5","6","7","8","9","10","11"};
    private JCheckBox daily = new JCheckBox("Daily");
    private JCheckBox hourly = new JCheckBox("Hourly ");
    private JCheckBox minutely = new JCheckBox("Every ");
    private ArrayList<String> rawbillboards  = new ArrayList<>();
    private JComboBox<Object> billboardsList;
    private ArrayList<String> rawminutes = new ArrayList<>();
    private JComboBox<Object> minutes;
    private ArrayList<String> rawdurationminutes = new ArrayList<>();
    private JComboBox<Object> intervalminutes;
    private JComboBox<Object> durationminutes;
    private JComboBox<String> day = new JComboBox<>(days);
    private JComboBox<String> period = new JComboBox<>(periods);
    private JComboBox<String> hour = new JComboBox<>(hours);
    private JComboBox<Object> intervals;

    /**
     * Instantiates all the elements required for the Schedule Options window.
     * @param client Client object used to send message requests to the server.
     * @param username name reference.
     * @param token unique token for message requests.
     */

    public ScheduleOptions(Client client, String username, String token) {
        this.client = client;
        this.token = token;
        this.username = username;
        this.billboards = (ArrayList<Billboard>) client.sendMessage(new Message(token).requestBillboards()).getData();
        for (Billboard x : billboards) {
            rawbillboards.add(x.getName());
        }
        this.billboardsList = new JComboBox<>(rawbillboards.toArray());;
        for (int x = 0; x <60; x++) {
            rawminutes.add(String.format("%02d", x));
        }
        this.minutes = new JComboBox<>(rawminutes.toArray());

        for (int x = 1; x <=60; x++) {
            rawdurationminutes.add(Integer.toString(x));
        }
        this.intervalminutes = new JComboBox<>(rawminutes.toArray());
        this.durationminutes = new JComboBox<>(rawdurationminutes.toArray());


        period.setSelectedItem("AM");
        period.setPreferredSize(new Dimension(30,20));

        ArrayList<String> rawIntervals = new ArrayList<>();
        for (int x = 1; x <=10; x++) {
            rawIntervals.add(Integer.toString(x));
        }
        this.intervals = new JComboBox<>(rawIntervals.toArray());
        ButtonGroup checkBoxGroup = new ButtonGroup();
        checkBoxGroup.add(daily);
        checkBoxGroup.add(hourly);
        checkBoxGroup.add(minutely);

    }
    public Scheduled newSchedule() {
        return ScheduleEditorGUI(new Scheduled());
    }

    public Scheduled editSchedule(Scheduled scheduled) {
        billboardsList.setSelectedItem(scheduled.getBillboardName());
        switch(scheduled.getDay()) {
            case 7:
                day.setSelectedItem("Saturday");

                break;
            case 1:
                day.setSelectedItem("Sunday");

                break;
            case 2:
                day.setSelectedItem("Monday");

                break;
            case 3:
                day.setSelectedItem("Tuesday");

                break;
            case 4:
                day.setSelectedItem("Wednesday");

                break;
            case 5:
                day.setSelectedItem("Thursday");

                break;
            case 6:
                day.setSelectedItem("Friday");
                break;
        }
        
        hour.setSelectedItem("" + scheduled.getStartTime()/60);
        minutes.setSelectedItem(""+ scheduled.getStartTime()% 60);
        if(scheduled.getStartTime() >= 720) {
            period.setSelectedItem("PM");
        }
        else {
            period.setSelectedItem("AM");
        }
        durationminutes.setSelectedItem("" + scheduled.getDuration());

        if (scheduled.getInterval(0) == 1) {
            daily.setSelected(true);
        }
        if (scheduled.getInterval(0) == 2) {
            hourly.setSelected(true);
            intervals.setSelectedItem("" + scheduled.getInterval(1));
        }
        if (scheduled.getInterval(0) == 3) {
            minutely.setSelected(true);
            intervals.setSelectedItem("" + scheduled.getInterval(1));
            intervalminutes.setSelectedItem("" + scheduled.getInterval(2));
        }
        return ScheduleEditorGUI(scheduled);
    }




    private  Scheduled ScheduleEditorGUI(Scheduled scheduled){
        JPanel myPanel = new JPanel(new GridBagLayout());
        JFrame f = new JFrame();
        intervalminutes.setEnabled(false);
        intervals.setEnabled(false);
        billboardsList.setPreferredSize(new Dimension(150,20));
        hour.setPreferredSize(new Dimension(50,20));
        minutes.setPreferredSize(new Dimension(50,20));
        period.setPreferredSize(new Dimension(50,20));
        intervalminutes.setPreferredSize(new Dimension(50,20));
        durationminutes.setPreferredSize(new Dimension(50,20));
        myPanel.add(new JLabel("Billboard ID/Name: "), GUI.generateGBC(0,0,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(billboardsList, GUI.generateGBC(1,0,3,1,1,1,GridBagConstraints.HORIZONTAL,5,GridBagConstraints.WEST));
        myPanel.add(new JLabel("Day: "), GUI.generateGBC(0,1,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(day, GUI.generateGBC(1,1,6,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(new JLabel("Start Time: "), GUI.generateGBC(0,2,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(hour, GUI.generateGBC(1,2,1,1,0,0,GridBagConstraints.HORIZONTAL,5,GridBagConstraints.WEST));
        myPanel.add(minutes, GUI.generateGBC(2,2,1,1,0,0,0,5,GridBagConstraints.WEST));
        myPanel.add(period, GUI.generateGBC(3,2,1,1,0,0,0,5,GridBagConstraints.WEST));
        myPanel.add(new JLabel("Duration (minutes): "), GUI.generateGBC(0,3,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(durationminutes, GUI.generateGBC(1,3,2,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(new JLabel("Interval (Optional) "), GUI.generateGBC(0,4,7,1,1,1,0,5,GridBagConstraints.CENTER));
        myPanel.add(daily, GUI.generateGBC(0,5,1,1,0,1,GridBagConstraints.HORIZONTAL,5,GridBagConstraints.WEST));
        myPanel.add(hourly, GUI.generateGBC(0,6,1,1,0,1,GridBagConstraints.HORIZONTAL,5,GridBagConstraints.WEST));
        myPanel.add(minutely, GUI.generateGBC(0,7,1,1,0,1,GridBagConstraints.HORIZONTAL,5,GridBagConstraints.WEST));
        myPanel.add(intervalminutes, GUI.generateGBC(1,7,1,1,1,1,GridBagConstraints.HORIZONTAL,5,GridBagConstraints.WEST));
        myPanel.add(new JLabel(" Minutes."), GUI.generateGBC(2,7,1,1,1,1,GridBagConstraints.HORIZONTAL,5,GridBagConstraints.WEST));
        myPanel.add(new JLabel("Interval Count: "), GUI.generateGBC(0,8,1,1,1,1,GridBagConstraints.HORIZONTAL,5,GridBagConstraints.WEST));
        myPanel.add(intervals, GUI.generateGBC(1,8,1,2,1,1,GridBagConstraints.HORIZONTAL,5,GridBagConstraints.WEST));
        f.add(myPanel);

        //Button listeners for interval options
        daily.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    intervalminutes.setEnabled(false);
                    intervals.setEnabled(false);
                }
            }
        });
        hourly.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    intervalminutes.setEnabled(false);
                    intervals.setEnabled(true);
                }
            }
        });
        minutely.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    intervalminutes.setEnabled(true);
                    intervals.setEnabled(true);
                }
            }
        });

        //create dialogue window containing Billboard Options UI elements.
        String[] options = new String[2];
        options[0] = "Submit";
        options[1] = "Cancel";
        int result = JOptionPane.showOptionDialog(null, myPanel, "Schedule Billboard", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);
        if (result == JOptionPane.YES_OPTION) {
            Scheduled newschedule = createSchedule(scheduled);
            if (newschedule != null) {
                return newschedule;
            }
            else {
                JOptionPane.showConfirmDialog(null, "Duration exceeds interval duration", "Error", JOptionPane.DEFAULT_OPTION,JOptionPane.PLAIN_MESSAGE);
            }
        }
        return null;


    }

    private Scheduled createSchedule(Scheduled scheduled) {
        int today = 0;
        int selectedPeriod;
        int selectedHour;
        switch(Objects.requireNonNull(day.getSelectedItem()).toString()) {
            case "Sunday":
                today = 1;
                break;
            case "Monday":
                today = 2;
                break;
            case "Tuesday":
                today = 3;
                break;
            case "Wednesday":
                today = 4;
                break;
            case "Thursday":
                today = 5;
                break;
            case "Friday":
                today = 6;
                break;
            case "Saturday":
                today = 7;
                break;
        }

        if (Objects.requireNonNull(period.getSelectedItem()).toString().equals("AM")) {
            if (Integer.parseInt((String) Objects.requireNonNull(hour.getSelectedItem())) == 12) {
                selectedHour = 0;
            }
            else {
                selectedHour = Integer.parseInt((String) Objects.requireNonNull(hour.getSelectedItem()));
            }
            selectedPeriod = 0;
        }
        else {
            if (Integer.parseInt((String) Objects.requireNonNull(hour.getSelectedItem())) == 12) {
                selectedHour = 0;
            }
            else {
                selectedHour = Integer.parseInt((String) Objects.requireNonNull(hour.getSelectedItem()));
            }
            selectedPeriod = 1;

        }


        int[] interval = new int[]{0,0,0};
        if (daily.isSelected()) {
            interval[0] = 1;
        }
        if (hourly.isSelected()) {
            interval[0] = 2;
            interval[1] = Integer.parseInt(Objects.requireNonNull(intervals.getSelectedItem()).toString());
            interval[2] = 1;
        }
        if (minutely.isSelected()) {
            interval[0] = 3;
            interval[1] = Integer.parseInt(Objects.requireNonNull(intervals.getSelectedItem()).toString());
            interval[2] = Integer.parseInt(Objects.requireNonNull(intervalminutes.getSelectedItem()).toString());
        }

        ArrayList<User> users = (ArrayList<User>) client.sendMessage(new Message(token).requestUsers()).getData();
        int creatorID = 0;
        for (User x : users) {
            if (x.getUserName().equals(username)) {
                creatorID = x.getUserID();
            }
        }

        ;
        if (minutely.isSelected() &&interval[2] < Integer.parseInt((String) Objects.requireNonNull(durationminutes.getSelectedItem()))) {

            return null;

        }
        else {
            scheduled.setCreatorID(creatorID);
            for(Billboard x : billboards) {

                if (Objects.equals(billboardsList.getSelectedItem(), x.getName())) {
                    scheduled.setCreatorName(x.getCreatorName());
                    scheduled.setBillboardName(x.getName());
                    scheduled.setBillboardID(x.getBillboardID());
                }
            }
            int[] start = ScheduleHelper.CalculateStart(today,selectedHour , Integer.parseInt((String) Objects.requireNonNull(minutes.getSelectedItem())),selectedPeriod);
            scheduled.setDay(start[0]);
            scheduled.setStart(start[1]);
            scheduled.setDuration(Integer.parseInt((String) Objects.requireNonNull(durationminutes.getSelectedItem())));
            scheduled.setInterval(interval);
            return scheduled;
        }
    }
}