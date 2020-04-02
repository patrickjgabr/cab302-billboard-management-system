package ControlPanel;

import Shared.Billboard;
import Shared.TestCase;

import javax.swing.*;
import java.util.ArrayList;

public class ControlPanel {
    public static void main(String[] args) {
        ArrayList<Billboard> billboards = TestCase.Billboards(); //load test billboards
        JFrame frame = GUI.SetupFrame(); //init gui
        JTabbedPane pane = new JTabbedPane(); //init tabs
        JPanel billboardsPane = GUI.billboards(); //init billboard pane
        JPanel schedulePane = GUI.schedule(); //init schedule pane
        JPanel userManagementPane = GUI.userManagement(); //init userManagement pane
        billboardsPane = BillboardTab.SetupBillboardsPane(billboardsPane);
        BillboardTab.SetupBillboardsTable(billboardsPane, billboards);
        pane.addTab("Billboards", billboardsPane);
        pane.addTab("Schedule", schedulePane);
        pane.addTab("User Management", userManagementPane);
        frame.getContentPane().add(pane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
