package ControlPanel;



import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
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

    /**
     * method for setting up username and password entry fields and sending inputs to server.
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

        //submit buton
        this.submit = new JButton("SUBMIT");
        submit.setBackground(CustomFont.softBlue);
        submit.setFont(CustomFont.tabs);
        submit.setFont(submit.getFont().deriveFont(Font.BOLD));
        submit.setForeground(Color.black);
        submit.setBorder(new LineBorder(CustomFont.softBlue, 2, true));

        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.add(user_label);
        panel.add(username);
        panel.add(password_label);
        panel.add(password);
        JLabel message = new JLabel();
        panel.add(message);
        panel.add(submit);
        panel.setBorder(new EmptyBorder(5,5,5,5));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel, BorderLayout.CENTER);
        frame.setTitle("Login");
        frame.getRootPane().setDefaultButton(submit);
        frame.setSize(350, 150);
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
