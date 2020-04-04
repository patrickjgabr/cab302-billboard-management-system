package ControlPanel;

import Shared.User;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static ControlPanel.CustomFont.lightGray;

public class UserTab {
    public static JPanel SetupUserPane() {
        JPanel panel = new JPanel();                                                           //first tab
        panel.setBorder(BorderFactory.createEmptyBorder(30,20,15,20));
        panel.setLayout(new GridLayout(1,2));
        panel.setBackground(lightGray);
        return panel;
    }
    public static void SetupUsersTable(ArrayList<User> user){
        String[] columns = {"Username"};     //Column headers

    }
}
