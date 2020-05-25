package ControlPanel;

import Shared.Billboard;
import Shared.Message;
import Shared.Scheduled;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;

public class ScheduleOptions {
    public static Scheduled ScheduleEditor(Client client, String username, String token) {
        ArrayList<Billboard> billboards = (ArrayList<Billboard>) client.sendMessage(new Message(token).requestBillboards()).getData();
        ArrayList<String> rawbillboards  = new ArrayList<>();
        for (Billboard x : billboards) {
            rawbillboards.add(x.getName());
        }
        JComboBox<Object> billboardsList = new JComboBox<>(rawbillboards.toArray());
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        JComboBox<String> day = new JComboBox<>(days);
        JTextField startTime = new JTextField();
        JTextField duration = new JTextField();
        JCheckBox daily = new JCheckBox("Daily");
        JCheckBox hourly = new JCheckBox("Every ");
        JCheckBox minutely = new JCheckBox("Every ");
        return ScheduleEditorGUI(new Scheduled(),username, billboardsList,day, startTime, duration, daily,hourly,minutely);
    }



    private static Scheduled ScheduleEditorGUI(Scheduled scheduled,
                                               String username,
                                               JComboBox<Object> billboardName,
                                               JComboBox<String> day,
                                               JTextField startTime,
                                               JTextField duration,
                                               JCheckBox daily,
                                               JCheckBox hourly,
                                               JCheckBox minutely){
        JPanel myPanel = new JPanel(new GridBagLayout());
        JFrame f = new JFrame();
        myPanel.add(new JLabel("Billboard name: "), GUI.generateGBC(0,0,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(billboardName, GUI.generateGBC(1,0,6,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(new JLabel("Day: "), GUI.generateGBC(0,1,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(day, GUI.generateGBC(1,1,6,1,1,1,GridBagConstraints.HORIZONTAL,5,GridBagConstraints.WEST));
        myPanel.add(new JLabel("Start Time: "), GUI.generateGBC(0,2,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(new JLabel("Hour: "), GUI.generateGBC(1,2,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(new JComboBox<>(), GUI.generateGBC(2,2,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(new JLabel("Minute: "), GUI.generateGBC(3,2,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(new JComboBox<>(), GUI.generateGBC(4,2,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(new JLabel("Period: "), GUI.generateGBC(5,2,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(new JComboBox<>(), GUI.generateGBC(6,2,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(new JLabel("Duration (minutes): "), GUI.generateGBC(0,3,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(new JComboBox<>(), GUI.generateGBC(1,3,2,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(new JLabel("Interval (Optional) "), GUI.generateGBC(0,4,7,1,1,1,0,5,GridBagConstraints.CENTER));
        myPanel.add(daily, GUI.generateGBC(0,5,1,1,0,1,0,5,GridBagConstraints.WEST));
        myPanel.add(hourly, GUI.generateGBC(0,6,1,1,0,1,0,5,GridBagConstraints.WEST));
        myPanel.add(new JComboBox<>(), GUI.generateGBC(1,6,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(new JLabel(" Hours."), GUI.generateGBC(2,6,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(minutely, GUI.generateGBC(0,7,1,1,0,1,GridBagConstraints.HORIZONTAL,5,GridBagConstraints.WEST));
        myPanel.add(new JComboBox<>(), GUI.generateGBC(1,7,1,1,1,1,GridBagConstraints.HORIZONTAL,5,GridBagConstraints.WEST));
        myPanel.add(new JLabel(" Minutes."), GUI.generateGBC(2,7,1,1,1,1,GridBagConstraints.HORIZONTAL,5,GridBagConstraints.WEST));
        myPanel.add(new JButton("Submit"), GUI.generateGBC(3,8,2,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(new JButton("Cancel"), GUI.generateGBC(5,8,2,1,1,1,0,5,GridBagConstraints.WEST));
        f.add(myPanel);
        Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        myPanel.setBorder(padding);
        f.pack();
        f.setTitle("Schedule Billboard");
        f.setResizable(false);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        return null;
    }
}