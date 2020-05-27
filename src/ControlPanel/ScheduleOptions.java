package ControlPanel;

import Shared.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Objects;

public class ScheduleOptions {
    private Client client;
    private String token;
    public Scheduled ScheduleEditor(Client client, String username, String token) {
        this.client = client;
        this.token = token;
        ArrayList<Billboard> billboards = (ArrayList<Billboard>) client.sendMessage(new Message(token).requestBillboards()).getData();
        ArrayList<String> rawbillboards  = new ArrayList<>();
        for (Billboard x : billboards) {
            rawbillboards.add(x. getBillboardID() + " "+x.getName());
        }
        JComboBox<Object> billboardsList = new JComboBox<>(rawbillboards.toArray());
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        String[] periods = {"AM", "PM"};
        ArrayList<String> rawminutes = new ArrayList<>();
        for (int x = 0; x <60; x++) {
            rawminutes.add(Integer.toString(x));
        }
        String[] hours = {"12","1","2","3","4","5","6","7","8","9","10","11"};
        JComboBox<Object> minutes = new JComboBox<>(rawminutes.toArray());
        ArrayList<String> rawdurationminutes = new ArrayList<>();
        for (int x = 1; x <=60; x++) {
            rawdurationminutes.add(Integer.toString(x));
        }
        JComboBox<Object> intervalminutes = new JComboBox<>(rawminutes.toArray());
        JComboBox<Object> durationminutes = new JComboBox<>(rawdurationminutes.toArray());
        JComboBox<String> day = new JComboBox<>(days);
        JComboBox<String> period = new JComboBox<>(periods);
        period.setSelectedItem("AM");
        period.setPreferredSize(new Dimension(30,20));
        JComboBox<String> hour = new JComboBox<>(hours);
        JCheckBox daily = new JCheckBox("Daily");
        JCheckBox hourly = new JCheckBox("Hourly ");
        JCheckBox minutely = new JCheckBox("Every ");
        ArrayList<String> rawIntervals = new ArrayList<>();
        for (int x = 1; x <=10; x++) {
            rawIntervals.add(Integer.toString(x));
        }
        JComboBox<Object> intervals = new JComboBox<>(rawIntervals.toArray());

        ButtonGroup checkBoxGroup = new ButtonGroup();
        //add CheckBoxes to ButtonGroup
        checkBoxGroup.add(daily);
        checkBoxGroup.add(hourly);
        checkBoxGroup.add(minutely);
        return ScheduleEditorGUI(new Scheduled(),  username,intervals, durationminutes, intervalminutes,hour, period,minutes, billboardsList,day, daily,hourly,minutely);
    }



    private  Scheduled ScheduleEditorGUI(Scheduled scheduled,
                                               String username,
                                               JComboBox<Object> intervals,
                                               JComboBox<Object> durationminutes,
                                               JComboBox<Object> intervalminutes,
                                               JComboBox<String> hour,
                                               JComboBox<String> period,
                                               JComboBox<Object> minutes,
                                               JComboBox<Object> billboardName,
                                               JComboBox<String> day,
                                               JCheckBox daily,
                                               JCheckBox hourly,
                                               JCheckBox minutely){
        JPanel myPanel = new JPanel(new GridBagLayout());
        JFrame f = new JFrame();
        intervalminutes.setEnabled(false);
        intervals.setEnabled(false);
        myPanel.add(new JLabel("Billboard ID/Name: "), GUI.generateGBC(0,0,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(billboardName, GUI.generateGBC(1,0,3,1,1,1,GridBagConstraints.HORIZONTAL,5,GridBagConstraints.WEST));
        myPanel.add(new JLabel("Day: "), GUI.generateGBC(0,1,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(day, GUI.generateGBC(1,1,6,1,1,1,0,5,GridBagConstraints.WEST));

        hour.setPreferredSize(new Dimension(50,20));
        minutes.setPreferredSize(new Dimension(50,20));
        period.setPreferredSize(new Dimension(50,20));
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


        int result = JOptionPane.showConfirmDialog(null, myPanel, "Schedule Billboard", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            int today = 0;
            switch(Objects.requireNonNull(day.getSelectedItem()).toString()) {
                case "Sunday":
                    today = 0;
                    break;
                case "Monday":
                    today = 1;
                    break;
                case "Tuesday":
                    today = 2;
                    break;
                case "Wednesday":
                    today = 3;
                    break;
                case "Thursday":
                    today = 4;
                    break;
                case "Friday":
                    today = 5;
                    break;
                case "Saturday":
                    today = 6;
            }

            int selectedPeriod = -1;
            if (period.toString().equals("AM")) {
                selectedPeriod = 0;
            }
            else if (period.toString().equals("PM")) {
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
            int billboardID = Integer.parseInt(Objects.requireNonNull(billboardName.getSelectedItem()).toString().split(" ")[0]);;
            return new Scheduled(creatorID, billboardID, ScheduleHelper.DateTime(today, Integer.parseInt((String)hour.getSelectedItem()) , Integer.parseInt((String)minutes.getSelectedItem()),selectedPeriod),Integer.parseInt((String)durationminutes.getSelectedItem()), interval);
        }
        return null;


    }
}