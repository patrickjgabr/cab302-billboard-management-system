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
import java.util.ArrayList;
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
    Icon yes = new ImageIcon("externalResources/confirm.png");
    Icon no = new ImageIcon("externalResources/deny.png");

    public UserManagementTab(JTabbedPane mainPane, ArrayList<Integer> permissions, Client client,  String token) {
        this.client = client;
        this.token = token;
        this.pane = new JPanel();
        this.perms = permissions;
        pane.setLayout(new GridBagLayout());
        setupUserTable();
        updateTable();
        setupDetails();
        mainPane.addTab("User Management", pane);
    }

    public void updateTable(){
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        this.users = (ArrayList<User>) client.sendMessage(new Message(token).requestUsers()).getData();
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        for (User user : users) {
            model.addRow(new Object[]{
                    "<html><h2> &nbsp " + user.getUserName() + "&nbsp &nbsp | &nbsp &nbsp ID: " + user.getUserID() + "</h2></html>"});
        }
    }

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
        pane.add(scrollPane, GUI.generateGBC(0,1,2,1,0,1,GridBagConstraints.VERTICAL, 0, GridBagConstraints.WEST));
    }

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
            }
        });

        JButton editButton = new JButton("Edit");
        editButton.setFont(buttons);
        editButton.setBackground(buttonCol);
        editButton.setBorder(new LineBorder(softBlue, 2, true));


        JButton deleteButton = new JButton("Delete");
        deleteButton.setFont(buttons);
        deleteButton.setBackground(buttonCol);
        deleteButton.setBorder(new LineBorder(softBlue, 2, true));

        editButton.setEnabled(false);
        deleteButton.setEnabled(false);

        this.information = new JPanel();
        information.setLayout(new BoxLayout(information, BoxLayout.PAGE_AXIS));
        information.add(new JLabel("Select a user to view permissions."));
        pane.add(information, GUI.generateGBC(2,1,1,2,1,1,GridBagConstraints.HORIZONTAL,18,GridBagConstraints.NORTHWEST));

        ListSelectionModel rowSelected = table.getSelectionModel();             //setup list selection model to listen for a selection of the table
        rowSelected.addListSelectionListener(e -> {
            if (!rowSelected.isSelectionEmpty() && perms.get(3) == 1){          //check if row is selected and user has correct permissions
                int selected = rowSelected.getMinSelectionIndex();

                editButton.addActionListener(ee -> {
                    User created = new UserManagementOptions().editUser(users.get(selected));
                });

                deleteButton.addActionListener(ee -> {
                    client.sendMessage(new Message(token).deleteUser(users.get(selected)));
                });

                editButton.setEnabled(true);
                deleteButton.setEnabled(true);
                editButton.setText("Edit '" + users.get(selected).getUserName() + "'");
                deleteButton.setText("Delete '" + users.get(selected).getUserName() + "'");

                information.removeAll();
                JLabel name = new JLabel("<html>" + users.get(selected).getUserName() +"<html>");
                name.setFont(username);
                name.setPreferredSize(new Dimension(500,50));
                name.setAlignmentX( Component.LEFT_ALIGNMENT );

                JLabel userID = new JLabel("<html><h2>User ID: " + users.get(selected).getUserID() +"</h2><html>");
                userID.setFont(userIDfont);
                userID.setPreferredSize(new Dimension(200,50));
                userID.setAlignmentX( Component.LEFT_ALIGNMENT );

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

        JPanel topButtons = new JPanel(new GridLayout(1,3,10,5));
        topButtons.setBorder(new EmptyBorder(10, 5, 10, 5));
        topButtons.setPreferredSize(new Dimension(350, 50));
        topButtons.add(createButton);
        topButtons.add(editButton);
        topButtons.add(deleteButton);
        pane.add(topButtons, GUI.generateGBC(0,0,2,1,0,0,0, 5, GridBagConstraints.WEST));
    }
    public Icon getPermissionsIcon(Integer permission){
        if (permission == 1){
            return yes;
        }
        else{
            return no; } }

    /*
    public void setupUserManagementTable() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Username");
        model.addColumn("Password");
        model.addColumn("Create User");
        model.addColumn("Delete User");
        model.addColumn("Schedule Billboard");
        model.addColumn("Edit Users");

        this.table = new JTable(model);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 8;
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(lightGray);
        pane.add(scrollPane, c);                   //add table to pane - 1st row out of 2 in the grid layout.
        //------------------------------------Table Created --------------------------------------------------------//
    }

    private void setTableFeatures() {
        table.setRowSelectionAllowed(true);
        table.setCellSelectionEnabled(false);
        table.setRowHeight(40);
        table.setSelectionBackground(new Color(0, 74, 127));
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        table.setIntercellSpacing(new Dimension(10, 20));
        table.setFont(tableContentsF);                                      //table contents font (16px Comic sans)
        table.getTableHeader().setBackground(softBlue);                     //set table header colour
        table.getTableHeader().setOpaque(false);
        table.getTableHeader().setFont(tableHeader);
        table.setDefaultEditor(Object.class, null);
    }

    public void updateTable(){
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        this.users = (ArrayList<User>) client.sendMessage(new Message(token).requestUsers()).getData();

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        for (User users : users) {
            model.addRow(new Object[]{
                    users.getUserID(),
                    users.getUserName(),
                    users.getUserPassword(),
                    users.getPermission().get(0),
                    users.getPermission().get(1),
                    users.getPermission().get(2),
                    users.getPermission().get(3)});
        }
    }

    //-----------------------------------------------------------------
    //Buttons still need to be implemented but works with everything else
    //-----------------------------------------------------------------

/*    public void setupButtons() {
        //button to be placed at grid space (0,1)
        JButton createButton = new JButton("New User");                        //button to be placed at grid space (2,1)
        JButton editButton = new JButton();                  //button to be placed at grid space (3,1)


        JLabel selectedRow = new JLabel();

        createButton.setFont(buttons);

        editButton.setFont(buttons);
        editButton.setVisible(false);


        selectedRow.setFont(tableContentsF);

        ListSelectionModel rowSelected = table.getSelectionModel();             //setup list selection model to listen for a selection of the table
        rowSelected.addListSelectionListener(e -> {
            if (!rowSelected.isSelectionEmpty()){
                int selected = rowSelected.getMinSelectionIndex();
                editButton.setVisible(true);
                editButton.setText("Edit " + users.get(selected).getUserName());
            }
        });







        editButton.addActionListener(e -> {
            if (!Objects.equals(editButton.getText(), "")){
                int selected = rowSelected.getMinSelectionIndex();
                Billboard created = BillboardOptions.BillboardEditor(users.get(selected));
                if(created != null) {
                    Client client = new Client();
                    Message reply = client.sendMessage(new Message().updateUser(created));
                    updateTable();
                }
            }
            else JOptionPane.showMessageDialog(null, "Please select a user first.");
        });

        createButton.addActionListener(e -> {
            Billboard created = BillboardOptions.BillboardEditor();
            if(created != null) {
                users.add(created); //replace with send to server
                updateTable();
            }
        });




        pane.add(createButton,GUI.newButtonConstraints(0,0));                  //place button 2 at (2,1)
                       //place button 1 at (0,1)

        pane.add(editButton,GUI.newButtonConstraints(3,0));                   //place button 2 at (1,1)
        pane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    }*/

}
