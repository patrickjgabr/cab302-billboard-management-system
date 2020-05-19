package ControlPanel;



import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
            MessageDigest passwordHash;
            try {
                passwordHash = MessageDigest.getInstance("SHA-256");
                passwordHash.update(password.getText().getBytes());
                byte [] byteArray = passwordHash.digest();
                StringBuilder sb = new StringBuilder();
                for (byte b : byteArray) {
                    sb.append(String.format("%02x", b & 0xFF));
                }
                String hashed = sb.toString();
                System.out.println("hashed" + hashed);
            return hashed;
        } catch (NoSuchAlgorithmException e) {return "";}
    }

    public JButton getSubmit() {
        return submit;
    }

    public JFrame getFrame() {
        return frame;
    }
}
