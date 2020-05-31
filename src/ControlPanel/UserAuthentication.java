package ControlPanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class UserAuthentication extends JFrame {
    private JButton submit;
    private JFrame frame;
    private JTextField username;
    private JTextField password;

    /**
     * Method for setting up username and password entry fields and sending provided inputs to server.
     */
    public UserAuthentication() {
        //user name
        this.frame = new JFrame();
        JLabel user_label = new JLabel();
        user_label.setFont(CustomFont.login);
        user_label.setBorder(new EmptyBorder(10,10,10,0));
        user_label.setText("User Name :");
        user_label.setHorizontalAlignment(JLabel.CENTER);
        this.username = new JTextField();
        username.setFont(CustomFont.tabs);
        username.setBorder(new LineBorder(Color.gray, 2, true));
        username.setHorizontalAlignment(JTextField.CENTER);

        //password
        JLabel password_label = new JLabel();
        password_label.setFont(CustomFont.login);
        password_label.setBorder(new EmptyBorder(10,10,10,0));
        password_label.setText("Password :");
        password_label.setHorizontalAlignment(JLabel.CENTER);
        this.password = new JPasswordField();
        password.setBorder(new LineBorder(Color.gray, 2, true));
        password.setHorizontalAlignment(JTextField.CENTER);

        //submit button
        this.submit = new JButton("SUBMIT");
        submit.setBackground(CustomFont.softBlue);
        submit.setFont(CustomFont.tabs);
        submit.setFont(submit.getFont().deriveFont(Font.BOLD));
        submit.setForeground(Color.black);
        submit.setBorder(new LineBorder(CustomFont.softBlue, 2, true));

        JLabel link = new JLabel("Forgot Password");
        link.setForeground(Color.BLUE.darker());
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://www.youtube.com/watch?v=dQw4w9WgXcQ"));
                } catch (IOException | URISyntaxException ex) { ex.printStackTrace(); } }
        });


        JPanel panel = new JPanel(new GridLayout(4, 1));
        panel.add(user_label);                                                  //adding all components
        panel.add(username);
        panel.add(password_label);
        panel.add(password);
        JLabel message = new JLabel();
        panel.add(message);
        panel.add(submit);
        panel.add(link);
        panel.setBorder(new EmptyBorder(5,5,5,5));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel, BorderLayout.CENTER);
        frame.setTitle("Login");
        frame.getRootPane().setDefaultButton(submit);
        frame.setSize(350, 180);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    /**
     * gets username of logged in user
     * @return String of username
     */
    public String getUsername() {
        return username.getText();
    }

    /**
     * gets logged in users password
     * @return String containing users password
     */
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

    /**
     * get current working frame. used in UserAuthentication method
     * @return Jframe object of current working frame.
     */
    public JFrame getFrame() {
        return frame;
    }
}
