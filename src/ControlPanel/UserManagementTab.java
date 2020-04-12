package ControlPanel;

import Shared.Billboard;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static ControlPanel.CustomFont.lightGray;

public class UserManagementTab {
    private JPanel pane;
    public UserManagementTab(JTabbedPane mainPane){
        this.pane = new JPanel();
        mainPane.addTab("User Management", pane);
    }
}
