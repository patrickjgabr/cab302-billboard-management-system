package ControlPanel;
import Shared.Billboard;
import static ControlPanel.CustomFont.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Objects;

public class BillboardTab{
    public static JPanel SetupBillboardsPane() {
        JPanel panel = new JPanel();                                                           //first tab
        panel.setLayout(new GridBagLayout());
        //panel.setBackground(lightGray);
        return panel;
    }

    public static JTable SetupBillboardsTable(JPanel pane, ArrayList<Billboard> billboards) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Billboard");
        model.addColumn("Author");
        model.addColumn("Image URL");
        model.addColumn("Message Text");
        model.addColumn("Message Colour");
        model.addColumn("Background Colour");
        model.addColumn("Info Text");
        model.addColumn("Info Colour");
        JTable table = new JTable(model);
        setTableFeatures(table);                            //set table font, layout, size, colour etc.
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
        return table;
    }

    public static void setButtonLook(JButton b){
        b.setBorder(BorderFactory.createLineBorder(Color.black, 3));        //set button border, font, colours etc.
        b.setFont(buttons);
        b.setForeground(softBlue);
    }
    public static void setTableFeatures(JTable table){
        table.setRowSelectionAllowed(true);
        table.setCellSelectionEnabled(false);
        table.setRowHeight(40);
        table.setSelectionBackground(new Color(0,74,127));
        //table.setSelectionForeground(Color.black);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        table.getColumnModel().getColumn(0).setMaxWidth(35);    //set column 0 to max 35 wide (doesn't need to be big)
        table.setIntercellSpacing(new Dimension(10, 20));
        table.setFont(tableContentsF);                                      //table contents font (16px Comic sans)
        table.getTableHeader().setBackground(softBlue);                     //set table header colour
        table.getTableHeader().setOpaque(false);
        table.getTableHeader().setFont(tableHeader);
        table.setDefaultEditor(Object.class, null);
    }

    public static void updateTable(JTable table, ArrayList<Billboard> billboards){
        int i = 0;
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        for (Billboard billboard : billboards) {
            model.addRow(new Object[]{
                    Integer.toString(i),
                    billboard.getName(),
                    "TBA",
                    billboard.getPictureLink(),
                    billboard.getMessageText(),
                    billboard.getMessageTextColour(),
                    billboard.getBackgroundColour(),
                    billboard.getInformationText(),
                    billboard.getBackgroundColour()});
            i++;
        }
    }

    public static void setupButtons(JTable table, JPanel pane, ArrayList<Billboard> billboards) {
        JButton previewButton = new JButton("Preview Billboard");                    //button to be placed at grid space (0,1)
        JButton createButton = new JButton("New Billboard");                        //button to be placed at grid space (2,1)
        JButton editButton = new JButton();                  //button to be placed at grid space (3,1)
        JButton importButton = new JButton("Import XML");
        JButton exportButton = new JButton("Export XML");
        JLabel selectedRow = new JLabel();
        previewButton.setFont(buttons);
        createButton.setFont(buttons);
        importButton.setFont(buttons);
        exportButton.setFont(buttons);
        editButton.setFont(buttons);
        editButton.setVisible(false);
        previewButton.setVisible(false);
        selectedRow.setFont(tableContentsF);

        ListSelectionModel rowSelected = table.getSelectionModel();             //setup list selection model to listen for a selection of the table
        rowSelected.addListSelectionListener(e -> {
            if (!rowSelected.isSelectionEmpty()){
                //String thng = JOptionPane.showInputDialog(null, "Enter1: ");

                int selected = rowSelected.getMinSelectionIndex();
                editButton.setVisible(true);
                previewButton.setVisible(true);
                editButton.setText("Edit " + billboards.get(selected).getName());
                previewButton.setText("Preview " + billboards.get(selected).getName());
            }
        });

        previewButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "Not yet implemented."));
        editButton.addActionListener(e -> {
            if (!Objects.equals(editButton.getText(), "")){
                JOptionPane.showMessageDialog(null, "Not yet implemented.");
            }
            else JOptionPane.showMessageDialog(null, "Please select a billboard first.");
        });

        createButton.addActionListener(e -> {
            Billboard created = JOptionPaneMultiInput.MultiInputOptionPane();
            if(created != null) {
                billboards.add(created);
                updateTable(table, billboards);
            }
        });
        pane.add(createButton,GUI.newButtonConstraints(0,0));                  //place button 2 at (2,1)
        pane.add(previewButton,GUI.newButtonConstraints(3,0));                 //place button 1 at (0,1)
        pane.add(importButton,GUI.newButtonConstraints(1,0));
        pane.add(exportButton,GUI.newButtonConstraints(2,0));
        pane.add(editButton,GUI.newButtonConstraints(4,0));                   //place button 2 at (1,1)
        pane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    }

}