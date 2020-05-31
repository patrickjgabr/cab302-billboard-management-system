package ControlPanel;

import Shared.User;

import javax.swing.*;
import java.awt.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class UserManagementOptions {
    private JTextField name;
    private JPasswordField userPassword;
    private JCheckBox p0 = new JCheckBox("Create Billboards");
    private JCheckBox p1 = new JCheckBox("Edit Billboards");
    private JCheckBox p2 = new JCheckBox("Schedule Billboards");
    private JCheckBox p3 = new JCheckBox("Edit Users");

    /**
     * initialise UserManagementOptions object
     */
    public UserManagementOptions() {
    }


    /**
     * create a new user with blank fields
     *
     * @return user object
     */
    public User newUser() {
        name = new JTextField();
        userPassword = new JPasswordField();
        return UserEditorGUI(new User());
    }

    /**
     * Method used to edit existing users. When passed a new user in params constructs a new user, otherwise edits existing user attributes.
     * @param user User object passed to the method for editing existing values - use new User() to create new user.
     * @return User object with specified name, password and permissions.
     */
    public User editUser(User user) {
        name = new JTextField(user.getUserName());
        name.setEditable(false);
        userPassword = new JPasswordField();
        p0 = new JCheckBox("Create Billboards");
        if (user.getPermission().get(0) == 1) {
            p0.setSelected(true);
        }
        p1 = new JCheckBox("Edit Billboards");
        if (user.getPermission().get(1) == 1) {
            p1.setSelected(true);
        }
        p2 = new JCheckBox("Schedule Billboards");
        if (user.getPermission().get(2) == 1) {
            p2.setSelected(true);
        }
        p3 = new JCheckBox("Edit Users");
        if (user.getPermission().get(3) == 1) {
            p3.setSelected(true);
            p3.setEnabled(true);
        }
        return UserEditorGUI(user);
    }


    /**
     * create GUI to edit a user
     *
     * @param user user object to be edited.
     * @return user object
     */
    private User UserEditorGUI(User user) {
        JPanel myPanel = new JPanel();
        myPanel.setLayout(new GridBagLayout());
        myPanel.add(new JLabel("User Name: "), GUI.generateGBC(0, 0, 1, 1, 1, 1, 0, 5, GridBagConstraints.WEST));
        name.setPreferredSize(new Dimension(200, 20));
        myPanel.add(name, GUI.generateGBC(1, 0, 1, 1, 1, 1, 0, 5, GridBagConstraints.WEST));
        myPanel.add(new JLabel("New Password: "), GUI.generateGBC(0, 1, 1, 1, 1, 1, 0, 5, GridBagConstraints.WEST));
        userPassword.setPreferredSize(new Dimension(200, 20));
        myPanel.add(userPassword, GUI.generateGBC(1, 1, 1, 1, 1, 1, 0, 5, GridBagConstraints.WEST));
        myPanel.add(new JLabel("Permissions: "), GUI.generateGBC(0, 2, 2, 1, 1, 1, 0, 5, GridBagConstraints.WEST));
        myPanel.add(p0, GUI.generateGBC(0, 3, 1, 1, 1, 1, 0, 5, GridBagConstraints.WEST));
        myPanel.add(p1, GUI.generateGBC(0, 4, 1, 1, 1, 1, 0, 5, GridBagConstraints.WEST));
        myPanel.add(p2, GUI.generateGBC(0, 5, 1, 1, 1, 1, 0, 5, GridBagConstraints.WEST));
        myPanel.add(p3, GUI.generateGBC(0, 6, 1, 1, 1, 1, 0, 5, GridBagConstraints.WEST));


        //create dialogue window containing Billboard Options UI elements.
        String[] options = new String[2];
        options[0] = "Submit";
        options[1] = "Cancel";
        int result = JOptionPane.showOptionDialog(null, myPanel, "User Editor", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);

        if (result == JOptionPane.YES_OPTION) {
            if (name.getText().equals("") && userPassword.getText().equals("")) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Both a Username and Password are required", "Missing Fields", JOptionPane.WARNING_MESSAGE);
            } else {
                ArrayList<Integer> permissions = new ArrayList<>();
                if (p0.isSelected()) {
                    permissions.add(1);
                } else {
                    permissions.add(0);
                }
                if (p1.isSelected()) {
                    permissions.add(1);
                } else {
                    permissions.add(0);
                }
                if (p2.isSelected()) {
                    permissions.add(1);
                } else {
                    permissions.add(0);
                }
                if (p3.isSelected()) {
                    permissions.add(1);
                } else {
                    permissions.add(0);
                }

                try {
                    if (!userPassword.getText().equals("") && !(userPassword.getText() == null)) {
                        MessageDigest passwordHash = MessageDigest.getInstance("SHA-256");
                        passwordHash.update(userPassword.getText().getBytes());
                        byte[] byteArray = passwordHash.digest();

                        StringBuilder sb = new StringBuilder();
                        for (byte b : byteArray) {
                            sb.append(String.format("%02x", b & 0xFF));
                        }
                        String hashed = sb.toString();
                        user.setUserPassword(hashed);
                    }
                    user.setUserName(name.getText());
                    user.setPermission(permissions);
                    return user;
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
