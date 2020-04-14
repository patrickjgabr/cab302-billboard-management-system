package ControlPanel;

import Shared.Billboard;
import Shared.Message;
import Shared.Session;
import Shared.TestCase;
import static ControlPanel.CustomFont.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ControlPanel {
    private static void ShowControlPanel() {



        ArrayList<Billboard> billboards = TestCase.billboards();
        JFrame frame = GUI.SetupFrame();
        JTabbedPane pane = new JTabbedPane();
        BillboardTab billboardsPane = new BillboardTab(pane, billboards); // need to add permissions
        UserManagementTab schedulePane = new UserManagementTab(pane);
        ScheduleTab userManagementPane = new ScheduleTab(pane);
        pane.setFont(tabs);
        frame.getContentPane().add(pane);
        frame.pack();
        frame.setTitle("Control Panel");
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        UserAuthentication session = new UserAuthentication();
        session.getSubmit().addActionListener(e -> {
            try {
                Client client = new Client("127.0.0.1", 8080);
                Message login = new Message().requestUser(session.getUsername(), session.getPassword());
                System.out.println("Logged in via server");
                Message reply = client.sendMessage(login);
                session.getFrame().setVisible(false);
                ShowControlPanel();
            } catch (Exception error) {
                System.out.println("login failed");
            }
        });



    }
}
