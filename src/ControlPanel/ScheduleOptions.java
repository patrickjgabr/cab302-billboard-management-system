package ControlPanel;

import Shared.Billboard;
import Shared.Scheduled;

import javax.swing.*;
import java.awt.*;

public class ScheduleOptions {
    public static Scheduled ScheduleEditor(String username) {

        JTextField billboardName = new JTextField();
        JTextField startTime = new JTextField();
        JTextField duration = new JTextField();
        JCheckBox daily = new JCheckBox("Daily");
        JCheckBox hourly = new JCheckBox("Hourly");
        JCheckBox minutely = new JCheckBox("Minutely");
        return ScheduleEditorGUI(new Scheduled(),username, billboardName, startTime, duration, daily,hourly,minutely);
    }



    private static Scheduled ScheduleEditorGUI(Scheduled scheduled,
                                               String username,
                                               JTextField billboardName,
                                               JTextField startTime,
                                               JTextField duration,
                                               JCheckBox daily,
                                               JCheckBox hourly,
                                               JCheckBox minutely){
        JPanel myPanel = new JPanel();
        myPanel.setLayout(new GridLayout(7,1));
        myPanel.add(new JLabel("Billboard Name: "));
        myPanel.add(billboardName);
        myPanel.add(new JLabel("Start Time: "));
        myPanel.add(startTime);
        myPanel.add(new JLabel("Duration: "));
        myPanel.add(duration);
        myPanel.add(daily);
        myPanel.add(hourly);
        myPanel.add(minutely);
        int result = JOptionPane.showConfirmDialog(null, myPanel, "Please Enter Billboard options", JOptionPane.YES_NO_CANCEL_OPTION);
        return null;
    }
}