package ControlPanel;

import Shared.Message;
import static ControlPanel.CustomFont.*;
import static java.lang.System.exit;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class ControlPanel {
    private static void ShowControlPanel(ArrayList<Integer> permissions, String token, Client client, String username) {
        JFrame frame = GUI.SetupFrame();        //call to setup frame method to set size and instantiate frame.
        JTabbedPane pane = new JTabbedPane();
        BillboardTab billboardsPane = new BillboardTab(pane, permissions, client, token, username);     //render billboard tab based on current user permissions
        ScheduleTab schedulePane = new ScheduleTab(pane, permissions, client, token, username);   //render schedule tab based on current user permissions
        UserManagementTab userManagementPane = new UserManagementTab(pane, permissions, client, token, username);     //render user management tab based on current user permissions
        pane.setFont(tabs);
        frame.getContentPane().add(pane);
        frame.pack();
        frame.setTitle("Control Panel      User: " + username);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {             //window listener to show confirm option on close
                int confirmed = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to exit the program?", "Exit",
                        JOptionPane.YES_NO_OPTION);
                if (confirmed == JOptionPane.YES_OPTION) {      //when closed exit gracefully
                    Client client = new Client();
                    Message signout = new Message();
                    try {
                        signout = client.sendMessage(new Message().logoutUser(token));
                    } catch (Exception ex) {
                        exit(0);
                    }
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
                Message reply = client.sendMessage(new Message().loginUser(session.getUsername(), session.getPassword()));
                if (reply.getCommunicationID() == 502) {
                    JOptionPane.showConfirmDialog(null, "Wrong login details.", "Error", JOptionPane.DEFAULT_OPTION,JOptionPane.PLAIN_MESSAGE);
                    return;
                }
                session.getFrame().setVisible(false);
                ArrayList<Integer> permissions = (ArrayList<Integer>) reply.getData();
                String token = (String) reply.getSession();
                ShowControlPanel(permissions, token, client, session.getUsername());
            } catch (Exception error) {
                error.printStackTrace();
                JOptionPane.showConfirmDialog(null, "Server not available.", error.toString(), JOptionPane.DEFAULT_OPTION,JOptionPane.PLAIN_MESSAGE);
            }
        });



    }
}
