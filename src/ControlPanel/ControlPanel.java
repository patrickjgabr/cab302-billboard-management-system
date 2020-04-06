package ControlPanel;

import Shared.Billboard;
import Shared.TestCase;
import static ControlPanel.CustomFont.*;
import javax.swing.*;
import java.util.ArrayList;

public class ControlPanel {
    public static void main(String[] args) {
        ArrayList<Billboard> billboards = TestCase.Billboards();        //load test billboards
        JFrame frame = GUI.SetupFrame();                                //init gui
        JTabbedPane pane = new JTabbedPane();                           //init tabs
        pane.setFont(tabs);
        JPanel billboardsPane = BillboardTab.SetupBillboardsPane();                      //init billboard pane
        JPanel schedulePane = ScheduleTab.SetupSchedulePane();                             //init schedule pane
        JPanel userManagementPane = UserManagementTab.UserManagementPane();                       //init userManagement pane
        JTable table = BillboardTab.SetupBillboardsTable(billboardsPane, billboards);
        BillboardTab.updateTable(table, billboards);
        BillboardTab.setupButtons(table, billboardsPane,billboards);
        pane.addTab("Billboards", billboardsPane);
        pane.addTab("Schedule", schedulePane);
        pane.addTab("User Management", userManagementPane);
        frame.getContentPane().add(pane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
