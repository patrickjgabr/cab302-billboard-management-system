package ControlPanel;

import Shared.User;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class UserManagementOptions {
    public static User UserEditor() {
        JTextField name = new JTextField();
        JTextField userPassword = new JTextField();
        JCheckBox p0 = new JCheckBox("Create Billboards");
        JCheckBox p1 = new JCheckBox("Edit Billboards");
        JCheckBox p2 = new JCheckBox("Schedule Billboards");
        JCheckBox p3 = new JCheckBox("Edit Users");
        //ArrayList<Integer> perms = new ArrayList<Integer>();
        //String userID = "100001";

        return UserEditorGUI(new User(), name, userPassword, p0,p1,p2,p3);
    }


    public static User UserEditor(User user) {
        JTextField name = new JTextField(user.getUserName());
        JTextField userPassword = new JTextField(user.getUserPassword());
        JCheckBox p0 = new JCheckBox("Create Billboards");
        if(user.getPermission().get(0) == 1 ){p0.setSelected(true);}
        JCheckBox p1 = new JCheckBox("Edit Billboards");
        if(user.getPermission().get(1) == 1 ){p1.setSelected(true);}
        JCheckBox p2 = new JCheckBox("Schedule Billboards");
        if(user.getPermission().get(2) == 1 ){p2.setSelected(true);}
        JCheckBox p3 = new JCheckBox("Edit Users");
        if(user.getPermission().get(3) == 1 ){p3.setSelected(true);}

        return UserEditorGUI(new User(), name, userPassword, p0,p1,p2,p3);
    }

    private static User UserEditorGUI(User user, JTextField userName,
                                                JTextField userPassword,
                                                JCheckBox p0,
                                                JCheckBox p1,
                                                JCheckBox p2,
                                                JCheckBox p3
                                                ){
        JPanel myPanel = new JPanel();
        myPanel.setLayout(new GridLayout(5,2));

        myPanel.add(new JLabel("User Name: "));
        myPanel.add(userName);
        myPanel.add(Box.createHorizontalStrut(5)); // a spacer

        myPanel.add(new JLabel("Password: "));
        myPanel.add(userPassword);
        myPanel.add(Box.createHorizontalStrut(5)); // a spacer

        myPanel.add(new JLabel("Permissions: "));
        myPanel.add(new JLabel());
        myPanel.add(Box.createHorizontalStrut(5)); // a spacer

        myPanel.add(p0);
        myPanel.add(p1);
        myPanel.add(new JLabel());
        myPanel.add(p2);
        myPanel.add(p3);

        int result = JOptionPane.showConfirmDialog(null, myPanel, "New User", JOptionPane.YES_NO_CANCEL_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            if(userName.getText().equals("") || userPassword.getText().equals("")){
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

                User created = new User(userName.getText(), userPassword.getText(), permissions,100001, "unknown");
                created.setUserID(user.getUserID());
                return created;
            }
        }
        return null;
    }
}
