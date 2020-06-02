package ControlPanel;

import Shared.Client;
import Shared.Message;
import Shared.Scheduled;

import static ControlPanel.CustomFont.*;
import static java.lang.System.exit;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class ControlPanel {

    private static void UserAuthentication() {
        UserAuthentication session = new UserAuthentication();
        session.getSubmit().addActionListener(e -> {
            try {
                Client client = new Client();
                Message reply = client.sendMessage(new Message().loginUser(session.getUsername(), session.getPassword()));
                if (reply.getCommunicationID() == 502) {
                    JOptionPane.showConfirmDialog(null, "Wrong login details.", "Error", JOptionPane.DEFAULT_OPTION,JOptionPane.PLAIN_MESSAGE);
                    return;
                }
                session.getFrame().dispose();
                ArrayList<Integer> permissions = (ArrayList<Integer>) reply.getData();
                String token = reply.getSession();
                ShowControlPanel(permissions, token, client, session.getUsername());
            } catch (Exception error) {
                error.printStackTrace();
                JOptionPane.showConfirmDialog(null, "Server not available.", error.toString(), JOptionPane.DEFAULT_OPTION,JOptionPane.PLAIN_MESSAGE);
            }
        });
    }


    private static void ShowControlPanel(ArrayList<Integer> permissions, String token, Client client, String username) {
        JFrame frame = GUI.SetupFrame();        //call to setup frame method to set size and instantiate frame.
        JTabbedPane pane = new JTabbedPane();
        new BillboardTab(pane, permissions, client, token, username);     //render billboard tab based on current user permissions
        new ScheduleTab(pane, permissions, client, token, username);   //render schedule tab based on current user permissions
        new UserManagementTab(pane, permissions, client, token, username);     //render user management tab based on current user permissions
        pane.setFont(tabs);
        frame.getContentPane().add(pane);

        frame.setTitle("Control Panel      User: " + username);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {             //window listener to show confirm option on close

                String[] options = new String[3];
                options[0] = "Yes";
                options[1] = "Sign Out";
                options[2] = "Cancel";
                int result = JOptionPane.showOptionDialog(null, "Are you sure you want to exit the program?", "Close Control Panel.", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);
                if(result == 0) {
                    client.sendMessage(new Message().logoutUser(token));
                    System.exit(0);
                }
                if (result == 1) {
                    frame.dispose();
                    client.sendMessage(new Message().logoutUser(token));
                    UserAuthentication();
                }

            }
        });

    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            System.out.println("Error setting GUI look and feel");
        }
        UserAuthentication();
    }
}
