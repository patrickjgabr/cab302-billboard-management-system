package ControlPanel;
import Shared.Billboard;
import Shared.User;
import Viewer.BillboardToImage;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static ControlPanel.CustomFont.*;
import static ControlPanel.CustomFont.tableHeader;

public class UserManagementTab {
    private JTable table;
    private ArrayList<User> users;
    private JPanel pane;

    public UserManagementTab(JTabbedPane mainPane, ArrayList<Integer> permissions, ArrayList<User> users) {
        this.users = users;
        this.pane = new JPanel();
        pane.setLayout(new GridBagLayout());
        setupUserManagementTable();
        setTableFeatures();


        mainPane.addTab("User Management", pane);
    }

    public void setupUserManagementTable() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Username");
        model.addColumn("Password");
        model.addColumn("Create User");
        model.addColumn("Delete User");
        model.addColumn("Change Password");
        model.addColumn("Assign Role");
        model.addColumn("Create Billboard");
        model.addColumn("Edit Billboard");
        model.addColumn("View Billboard");
        model.addColumn("View Schedule");
        model.addColumn("Edit Schedule");

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

/*    public void updateTable(){
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        for (User users : users) {
            model.addRow(new Object[]{
                    users.getUserID(),
                    users.getUserName(),
                    users.getUserPassword(),
                    users.getPermission().get(0),
                    users.getPermission().get(1),
                    users.getPermission().get(2),
                    users.getPermission().get(3),
                    users.getPermission().get(4),
                    users.getPermission().get(5),
                    users.getPermission().get(6),
                    users.getPermission().get(7),
                    users.getPermission().get(8)});
        }
    }*/

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
