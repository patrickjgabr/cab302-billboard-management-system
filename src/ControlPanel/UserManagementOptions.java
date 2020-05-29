package ControlPanel;

import Shared.User;

import javax.swing.*;
import java.awt.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class UserManagementOptions {
    private JTextField name;
    private JTextField userPassword;
    private JCheckBox p0 = new JCheckBox("Create Billboards");
    private JCheckBox p1 = new JCheckBox("Edit Billboards");
    private JCheckBox p2 = new JCheckBox("Schedule Billboards");
    private JCheckBox p3 = new JCheckBox("Edit Users");


    public UserManagementOptions() {

    }

     /**
     * user editor function for creating a new user
     * @return user object created based on text responses to be sent to the database.
     */
    public User newUser() {
        name = new JTextField();
        name.setPreferredSize(new Dimension(200, 20));
        name.setEditable(true);
        userPassword = new JTextField();
        userPassword.setPreferredSize(new Dimension(200,20));
        return UserEditorGUI(new User());
    }

     /**
     * user editor function that takes a user object - used for updating a user object in the database
     * @param user user object to be updated.
     * @return updated user object
     */
    public User editUser(User user) {
        name = new JTextField(user.getUserName());
        name.setPreferredSize(new Dimension(200, 20));
        name.setEditable(false);
        userPassword = new JTextField(user.getUserPassword());
        userPassword.setPreferredSize(new Dimension(200,20));
        p0 = new JCheckBox("Create Billboards");
        if(user.getPermission().get(0) == 1 ){p0.setSelected(true);}
        p1 = new JCheckBox("Edit Billboards");
        if(user.getPermission().get(1) == 1 ){p1.setSelected(true);}
        p2 = new JCheckBox("Schedule Billboards");
        if(user.getPermission().get(2) == 1 ){p2.setSelected(true);}
        p3 = new JCheckBox("Edit Users");
        if(user.getPermission().get(3) == 1 ){p3.setSelected(true);p3.setEnabled(false);}
        return UserEditorGUI(user);
    }

    private User UserEditorGUI(User user) {
        JPanel myPanel = new JPanel();
        myPanel.setLayout(new GridBagLayout());
        myPanel.add(new JLabel("User Name: "), GUI.generateGBC(0,0,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(name, GUI.generateGBC(1,0,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(new JLabel("Password: "), GUI.generateGBC(0,1,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(userPassword, GUI.generateGBC(1,1,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(new JLabel("Permissions: "), GUI.generateGBC(0,2,2,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(p0, GUI.generateGBC(0,3,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(p1, GUI.generateGBC(1,3,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(p2, GUI.generateGBC(0,4,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(p3, GUI.generateGBC(1,4,1,1,1,1,0,5,GridBagConstraints.WEST));

        int result = JOptionPane.showConfirmDialog(null, myPanel, "New User", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            if(name.getText().equals("") || userPassword.getText().equals("")){
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),"Both a Username and Password are required", "Missing Fields",JOptionPane.WARNING_MESSAGE);
            }else{
                ArrayList<Integer> permissions = new ArrayList<>();
                if (p0.isSelected()){
                    permissions.add(1);
                }else {
                    permissions.add(0); }
                if (p1.isSelected()){
                    permissions.add(1);
                }else {
                    permissions.add(0); }
                if (p2.isSelected()){
                    permissions.add(1);
                }else {
                    permissions.add(0); }
                if (p3.isSelected()){
                    permissions.add(1);
                }else {
                    permissions.add(0); }

                try {
                    MessageDigest passwordHash = MessageDigest.getInstance("SHA-256");
                    passwordHash.update(userPassword.getText().getBytes());
                    byte [] byteArray = passwordHash.digest();

                    StringBuilder sb = new StringBuilder();
                    for (byte b : byteArray) {
                        sb.append(String.format("%02x", b & 0xFF));
                    }
                    String hashed = sb.toString();
                    user.setUserName(name.getText());
                    user.setUserPassword(hashed);
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
