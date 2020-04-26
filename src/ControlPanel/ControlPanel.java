package ControlPanel;

import Shared.Billboard;
import Shared.Message;
import Shared.TestCase;
import static ControlPanel.CustomFont.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.AreaAveragingScaleFilter;
import java.util.ArrayList;

public class ControlPanel {
    private static void ShowControlPanel(ArrayList<Integer> permissions) {
        Client client = new Client();
        Message reply = client.sendMessage(new Message().requestBillboards());
        ArrayList<Billboard> billboards = (ArrayList<Billboard>) reply.getData();
        JFrame frame = GUI.SetupFrame();
        JTabbedPane pane = new JTabbedPane();
        BillboardTab billboardsPane = new BillboardTab(pane, permissions, billboards);
        ScheduleTab userManagementPane = new ScheduleTab(pane, permissions);
        UserManagementTab schedulePane = new UserManagementTab(pane, permissions);
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
                Client client = new Client();
                Message login = new Message().loginUser(session.getUsername(), session.getPassword());
                System.out.println("Logged in via server");
                Message reply = client.sendMessage(login);
                session.getFrame().setVisible(false);
                ArrayList<Integer> permissions = (ArrayList<Integer>) reply.getData();
                ShowControlPanel(permissions);
            } catch (Exception error) {
                error.printStackTrace();
            }
        });



    }
}
