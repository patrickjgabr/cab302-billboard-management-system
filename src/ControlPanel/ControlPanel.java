package ControlPanel;

import Shared.Billboard;
import Shared.Message;
import Shared.TestCase;
import Shared.User;

import static ControlPanel.CustomFont.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.AreaAveragingScaleFilter;
import java.util.ArrayList;

public class ControlPanel {
    private static void ShowControlPanel(ArrayList<Integer> permissions, String token, Client client) {
        JFrame frame = GUI.SetupFrame();
        JTabbedPane pane = new JTabbedPane();
        BillboardTab billboardsPane = new BillboardTab(pane, permissions, client, token);
        ScheduleTab userManagementPane = new ScheduleTab(pane, permissions, client, token);
        UserManagementTab schedulePane = new UserManagementTab(pane, permissions, client, token);
        pane.setFont(tabs);
        frame.getContentPane().add(pane);
        frame.pack();
        frame.setTitle("Control Panel");
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int confirmed = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to exit the program?", "Exit",
                        JOptionPane.YES_NO_OPTION);

                if (confirmed == JOptionPane.YES_OPTION) {
                    Client client = new Client();
                    Message signout = client.sendMessage(new Message().logoutUser(token));
                    System.out.println("Logged out Safely");
                    System.out.println("Session ID: "+ token);
                    System.out.println("Server Response: "+ signout.getCommunicationID());
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                }
            }
        });

    }

    public static void main(String[] args) {
        UserAuthentication session = new UserAuthentication();
        session.getSubmit().addActionListener(e -> {
            try {
                Client client = new Client();
                Message login = new Message().loginUser(session.getUsername(), session.getPassword());
                System.out.println("Logged in via server");
                System.out.println(login.getData());
                System.out.println(session.getPassword());
                Message reply = client.sendMessage(login);
                session.getFrame().setVisible(false);
                ArrayList<Integer> permissions = (ArrayList<Integer>) reply.getData();
                String token = (String) reply.getSession();
                ShowControlPanel(permissions, token, client);
            } catch (Exception error) {
                error.printStackTrace();
            }
        });



    }
}
