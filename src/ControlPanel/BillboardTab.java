package ControlPanel;
import Shared.Billboard;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import static ControlPanel.CustomFont.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.security.cert.Extension;
import java.util.ArrayList;
import java.util.Objects;

public class BillboardTab{
    private JTable table;
    private ArrayList<Billboard> billboards;
    private JPanel pane;

    public BillboardTab(JTabbedPane mainPane, boolean[]permissions, ArrayList<Billboard> billboards){
        this.billboards = billboards;
        this.pane = new JPanel();                                                           //first tab
        pane.setLayout(new GridBagLayout());
        setupBillboardsTable();
        setTableFeatures();
        updateTable();
        setupButtons();
        mainPane.addTab("Billboard", pane);
    }



    public void setupBillboardsTable() {
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

    private void setTableFeatures(){
        table.setRowSelectionAllowed(true);
        table.setCellSelectionEnabled(false);
        table.setRowHeight(40);
        table.setSelectionBackground(new Color(0,74,127));
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

    public void updateTable(){
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

    public void setupButtons() {
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
        exportButton.setVisible(false);
        selectedRow.setFont(tableContentsF);

        ListSelectionModel rowSelected = table.getSelectionModel();             //setup list selection model to listen for a selection of the table
        rowSelected.addListSelectionListener(e -> {
            if (!rowSelected.isSelectionEmpty()){
                int selected = rowSelected.getMinSelectionIndex();
                editButton.setVisible(true);
                previewButton.setVisible(true);
                exportButton.setVisible(true);
                exportButton.setText("Export " + billboards.get(selected).getName());
                editButton.setText("Edit " + billboards.get(selected).getName());
                previewButton.setText("Preview " + billboards.get(selected).getName());
            }
        });

        previewButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "Not yet implemented."));




        importButton.addActionListener(e -> {
            try {
                fileSelection();
            } catch (ParserConfigurationException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (SAXException ex) {
                ex.printStackTrace();
            }
        });

        editButton.addActionListener(e -> {
            if (!Objects.equals(editButton.getText(), "")){
                int selected = rowSelected.getMinSelectionIndex();
                Billboard created = BillboardOptions.BillboardEditor(billboards.get(selected));
                if(created != null) {
                    billboards.add(created); //replace with send to server
                    updateTable();
                }
            }
            else JOptionPane.showMessageDialog(null, "Please select a billboard first.");
        });

        createButton.addActionListener(e -> {
            Billboard created = BillboardOptions.BillboardEditor();
            if(created != null) {
                billboards.add(created); //replace with send to server
                updateTable();
            }
        });
        pane.add(createButton,GUI.newButtonConstraints(0,0));                  //place button 2 at (2,1)
        pane.add(previewButton,GUI.newButtonConstraints(4,0));                 //place button 1 at (0,1)
        pane.add(importButton,GUI.newButtonConstraints(1,0));
        pane.add(exportButton,GUI.newButtonConstraints(2,0));
        pane.add(editButton,GUI.newButtonConstraints(3,0));                   //place button 2 at (1,1)
        pane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    }


    private void fileSelection() throws ParserConfigurationException, IOException, SAXException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("*.xml", "xml"));
        int result = fileChooser.showOpenDialog(pane);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("PogChamp");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(selectedFile);
            doc.getDocumentElement().normalize();
            System.out.println("Billboard: "  + doc.getElementsByTagName("billboard").item(0).getAttributes().getNamedItem("background").getNodeValue());
            System.out.println("Message Colour: "  + doc.getElementsByTagName("message").item(0).getAttributes().getNamedItem("colour").getNodeValue());
            System.out.println("Message : "  + doc.getElementsByTagName("message").item(0).getTextContent());
            System.out.println("Picture URL: "  + doc.getElementsByTagName("picture").item(0).getAttributes().getNamedItem("url").getNodeValue());
            System.out.println("Information Colour: "  + doc.getElementsByTagName("information").item(0).getAttributes().getNamedItem("colour").getNodeValue());
            System.out.println("Information : "  + doc.getElementsByTagName("information").item(0).getTextContent());
        }

    }

}