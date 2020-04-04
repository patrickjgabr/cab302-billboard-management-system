package ControlPanel;

import javax.swing.*;
import java.awt.*;

import static ControlPanel.CustomFont.lightGray;

public class UserManagementTab {
    public static JPanel UserManagementPane() {
        JPanel panel = new JPanel();                                                           //first tab
        panel.setBorder(BorderFactory.createEmptyBorder(30,20,15,20));
        panel.setLayout(new GridLayout(2,1));
        panel.setBackground(lightGray);
        return panel;
    }
}
