package ControlPanel;

import Shared.Session;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class UserAuthentication extends JFrame {
    private JButton submit;
    private JFrame frame;
    private JTextField username;
    private JTextField password;


    public UserAuthentication() {
        this.frame = new JFrame();
        JLabel user_label = new JLabel();
        user_label.setText("User Name :");
        this.username = new JTextField();
        JLabel password_label = new JLabel();
        password_label.setText("Password :");
        this.password = new JPasswordField();
        this.submit = new JButton("SUBMIT");
        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.add(user_label);
        panel.add(username);
        panel.add(password_label);
        panel.add(password);
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
    }
    public String getUsername() {
        return username.getText();
    }
    public String getPassword() {
        return password.getText();
    }
    public JButton getSubmit() {
        return submit;
    }

    public JFrame getFrame() {
        return frame;
    }
}
