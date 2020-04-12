package ControlPanel;

import Shared.Session;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class UserAuthentication extends JFrame {
    private Session session;
    private boolean valid;
    private JButton submit;
    private JFrame frame;


    public UserAuthentication() {
        this.frame = new JFrame();
        JLabel user_label = new JLabel();
        user_label.setText("User Name :");
        JTextField userName_text = new JTextField();
        JLabel password_label = new JLabel();
        password_label.setText("Password :");
        JPasswordField password_text = new JPasswordField();
        this.submit = new JButton("SUBMIT");
        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.add(user_label);
        panel.add(userName_text);
        panel.add(password_label);
        panel.add(password_text);
        JLabel message = new JLabel();
        panel.add(message);
        panel.add(submit);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel, BorderLayout.CENTER);
        frame.setTitle("Login");
        frame.setSize(300, 100);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        ArrayList<Boolean> permissions = new ArrayList<>();
        setSession(new Session("name","something", permissions));

    }



    public void setSession(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    public boolean sessionCheck() {
        return true;
    } //only return true if valid

    public JButton getSubmit() {
        return submit;
    }

    public JFrame getFrame() {
        return frame;
    }
}
