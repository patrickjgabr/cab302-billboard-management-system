package ControlPanel;
import Server.Database.SessionDatabase;
import Shared.Billboard;
import Shared.User;
import Shared.Message;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import static ControlPanel.CustomFont.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.IOException;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static ControlPanel.CustomFont.*;
import static ControlPanel.CustomFont.tableHeader;

public class UserManagementTab {
    private JTable table;
    private ArrayList<User> users;
    private ArrayList<Integer> perms;
    private  JPanel pane;
    private Client client;
    private String token;
    private JPanel information;
    private int selected;
    private String username;
    Icon yes = new ImageIcon("externalResources/confirm.png");
    Icon no = new ImageIcon("externalResources/deny.png");

    /**
     * method for setting private variable values based on parameters given from control panel.
     * @param mainPane tabbed pane for adding all swing elements to. Essentially the frame.
     * @param permissions array list of permission integers for the user currently logged in.
     * @param client Client that is being used in session.
     * @param token token of logged in user.
     * @param username name of logged in user.
     */
    public UserManagementTab(JTabbedPane mainPane, ArrayList<Integer> permissions, Client client,  String token, String username) {
        this.client = client;
        this.token = token;
        this.pane = new JPanel();
        this.perms = permissions;
        this.username = username;
        pane.setLayout(new GridBagLayout());
        setupUserTable();
        updateTable();
        setupDetails();
        mainPane.addTab("User Management", pane);
    }

    /**
     * method to be called when a change is made to the user table and it requires updating. e.g. after edit, add or delete user is called.
     */
    public void updateTable(){
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        if (perms.get(3).equals(1)) {
            this.users = (ArrayList<User>) client.sendMessage(new Message(token).requestUsers()).getData();
        }
        else {
            this.users = new ArrayList<>();
        }
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        for (User user : users) {
            model.addRow(new Object[]{
                    "<html><h2> &nbsp " + user.getUserName() + "&nbsp &nbsp | &nbsp &nbsp ID: " + user.getUserID() + "</h2></html>"});
        }
    }

    /**
     * method to set up table displaying a list of users.
     */
    public void setupUserTable() {
        DefaultTableModel model = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        model.addColumn("Users");
        this.table = new JTable(model);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        table.setCellSelectionEnabled(true);
        table.setIntercellSpacing(new Dimension(5, 5));
        table.setSelectionBackground(buttonCol);
        table.setSelectionForeground(Color.black);
        table.setFont(tableContentsF);
        table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
        table.setRowHeight(60);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(300,0));
        pane.add(scrollPane, GUI.generateGBC(0,1,1,1,1,1,GridBagConstraints.VERTICAL, 0, GridBagConstraints.WEST));
    }

    /**
     * method for setting up majority of the 'User Management' tab. Method constructs all buttons, as well as displays all information when a user is selected.
     */
    public void setupDetails() {
        JButton createButton = new JButton("New User");


        createButton.setPreferredSize(new Dimension(50, 25));
        createButton.setFont(buttons);
        createButton.setBackground(buttonCol);
        createButton.setBorder(new LineBorder(softBlue, 2, true));
        createButton.addActionListener(e -> {
            User created = new UserManagementOptions().newUser();
            if (created != null){
                client.sendMessage(new Message(token).createUsers(created));
                updateTable();
                information.removeAll();
                information.add(new JLabel("Select a user to view permissions."));
                pane.validate();
                pane.repaint();
            }
        });
        if(perms.get(3)!=1){
            createButton.setEnabled(false);
        }
        JButton editButton = new JButton("Edit");
        editButton.setFont(buttons);
        editButton.setBackground(buttonCol);
        editButton.setBorder(new LineBorder(softBlue, 2, true));
        editButton.setEnabled(false);

        JButton deleteButton = new JButton("Delete");
        deleteButton.setFont(buttons);
        deleteButton.setBackground(buttonCol);
        deleteButton.setBorder(new LineBorder(softBlue, 2, true));
        deleteButton.setEnabled(false);



        this.information = new JPanel();
        information.setLayout(new BoxLayout(information, BoxLayout.PAGE_AXIS));

        if (perms.get(3).equals(1)) {
            createButton.setEnabled(true);
            information.add(new JLabel("Select a user to view permissions."));
        }
        else {
            createButton.setEnabled(false);
            information.add(new JLabel(" \"Edit User's\" permission required."));
        }

        pane.add(information, GUI.generateGBC(1,1,1,1,1,1,GridBagConstraints.HORIZONTAL,18,GridBagConstraints.NORTHEAST));

        editButton.addActionListener(ee -> {
            User edited = new UserManagementOptions().editUser(users.get(selected));
            client.sendMessage(new Message(token).updateUser(edited));
            updateTable();
            information.removeAll();
            information.add(new JLabel("Select a user to view permissions."));
            pane.validate();
            pane.repaint();
        });

        deleteButton.addActionListener(ee -> {
            client.sendMessage(new Message(token).deleteUser(users.get(selected)));
            updateTable();
            information.removeAll();
            information.add(new JLabel("Select a user to view permissions."));
            pane.validate();
            pane.repaint();

        });

        JButton changePassword = new JButton("Change Password");
        changePassword.setFont(buttons);
        changePassword.setBackground(buttonCol);
        changePassword.setBorder(new LineBorder(softBlue, 2, true));
        changePassword.addActionListener(ee -> {
            JPanel panel = new JPanel();
            JLabel label = new JLabel("New password:");
            JPasswordField pass = new JPasswordField(10);
            panel.add(label);
            panel.add(pass);
            String[] options = new String[]{"OK", "Cancel"};
            int option = JOptionPane.showOptionDialog(null, panel, "The title",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, options, options[1]);
            if(option == 0)
            {
                char[] password = pass.getPassword();
                if (new String(password).equals("")) {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),"Please enter a valid password.", "Empty Password",JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    MessageDigest passwordHash = MessageDigest.getInstance("SHA-256");
                    passwordHash.update(new String(password).getBytes());
                    byte [] byteArray = passwordHash.digest();

                    StringBuilder sb = new StringBuilder();
                    for (byte b : byteArray) {
                        sb.append(String.format("%02x", b & 0xFF));
                    }
                    String hashed = sb.toString();
                    client.sendMessage(new Message(token).updatePassword(username,hashed));

                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }

        });

        ListSelectionModel rowSelected = table.getSelectionModel();             //setup list selection model to listen for a selection of the table
        rowSelected.addListSelectionListener(e -> {
            if (!rowSelected.isSelectionEmpty()){          //check if row is selected and user has correct permissions
                information.removeAll();
                this.selected = rowSelected.getMinSelectionIndex();
                if(perms.get(3).equals(1)){                      //if user has edit user permissions
                    editButton.setEnabled(true);
                    if(!"root".equals(users.get(selected).getUserName())){          //AND user is not root user
                        deleteButton.setEnabled(true);                              //enable delete button
                    }
                    else {deleteButton.setEnabled(false);}                          //otherwise disable button.
                }
                editButton.setText("Edit '" + users.get(selected).getUserName() + "'");
                deleteButton.setText("Delete '" + users.get(selected).getUserName() + "'");
                JLabel name = new JLabel("<html>" + users.get(selected).getUserName() +"<html>");
                name.setFont(CustomFont.username);
                name.setPreferredSize(new Dimension(500,50));
                JLabel userID = new JLabel("<html><h2>User ID: " + users.get(selected).getUserID() +"</h2><html>");
                userID.setFont(userIDfont);
                userID.setPreferredSize(new Dimension(200,50));
                JPanel perms = new JPanel();
                perms.setLayout(new GridBagLayout());
                perms.setFont(userIDfont);
                JCheckBox createPerm = new JCheckBox("    Create Billboards");
                createPerm.setIcon(getPermissionsIcon(users.get(selected).getPermission().get(0)));
                perms.add(createPerm, GUI.generateGBC(0,0,1,1,1,1,GridBagConstraints.HORIZONTAL, 5, GridBagConstraints.WEST));
                createPerm.setFont(permissionfont);
                JCheckBox editPerm = new JCheckBox("    Edit Billboards");
                editPerm.setIcon(getPermissionsIcon(users.get(selected).getPermission().get(1)));
                perms.add(editPerm, GUI.generateGBC(0,2,1,1,1,1,GridBagConstraints.HORIZONTAL, 5, GridBagConstraints.WEST));
                editPerm.setFont(permissionfont);
                JCheckBox schedulePerm = new JCheckBox("    Schedule Billboards");
                schedulePerm.setIcon(getPermissionsIcon(users.get(selected).getPermission().get(2)));
                perms.add(schedulePerm, GUI.generateGBC(0,4,1,1,1,1,GridBagConstraints.HORIZONTAL, 5, GridBagConstraints.WEST));
                schedulePerm.setFont(permissionfont);
                JCheckBox editUserPerm = new JCheckBox("    Edit Users");
                editUserPerm.setIcon(getPermissionsIcon(users.get(selected).getPermission().get(3)));
                editUserPerm.setFont(permissionfont);
                perms.add(editUserPerm, GUI.generateGBC(0,6,1,1,1,1, GridBagConstraints.HORIZONTAL, 5, GridBagConstraints.WEST));
                perms.setPreferredSize(new Dimension(200,300));
                perms.setAlignmentX( Component.LEFT_ALIGNMENT );
                perms.setBorder(new LineBorder(buttonCol, 2, true));
                information.add(name);
                information.add(userID);
                information.add(perms);
            }
        });

        JPanel topButtons = new JPanel(new GridLayout(1,4,10,5));
        topButtons.setBorder(new EmptyBorder(10, 5, 10, 5));
        topButtons.setPreferredSize(new Dimension(600, 50));
        topButtons.add(createButton);
        topButtons.add(editButton);
        topButtons.add(deleteButton);
        topButtons.add(changePassword);
        pane.add(topButtons, GUI.generateGBC(0,0,2,1,0,0,0, 5, GridBagConstraints.WEST));
    }

    /**
     * method for determining which permission icon to use based on the users permissions. red for no permission, green if they have it.
     * @param permission either a 1 or 0 - 1 for yes, 0 for no.
     * @return icon to be displayed in checkbox.
     */
    public Icon getPermissionsIcon(Integer permission){
        if (permission == 1){
            return yes;
        }
        else{
            return no; } }
}
