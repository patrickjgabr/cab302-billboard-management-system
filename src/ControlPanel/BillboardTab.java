package ControlPanel;
import Shared.Billboard;
import Shared.Message;
import Shared.BillboardToImage;
import Viewer.GenerateBillboardFromXML;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import static ControlPanel.CustomFont.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Flow;

public class BillboardTab{
    private JTable table;
    private ArrayList<Billboard> billboards;
    private JPanel pane;
    private Client client;
    private String token;
    private String username;
    private JLabel preview;
    private int selected;
    private JPanel information;


    public BillboardTab(JTabbedPane mainPane, ArrayList<Integer> permissions, Client client, String token, String username){
        this.client = client;
        this.username = username;
        this.token = token;
        this.pane = new JPanel();
        this.selected = -1;
        pane.setLayout(new GridBagLayout());
        setupBillboardsTable();
        setupDetails();
        updateTable();
        mainPane.addTab("Billboard", pane);
    }

    public void updateTable(){
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        this.billboards = (ArrayList<Billboard>) client.sendMessage(new Message(token).requestBillboards()).getData();
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        for (Billboard billboard : billboards) {
            model.addRow(new Object[]{
                    "<html><b>" + billboard.getName() + "</b><br>ID: " + billboard.getBillboardID() + "<br>Created by: " + billboard.getCreatorName() + "</html>"});
        }
    }

    public void setupBillboardsTable() {
        DefaultTableModel model = new DefaultTableModel() {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model.addColumn("Billboard");
        this.table = new JTable(model);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
        table.setRowHeight(90);
        table.setTableHeader(null);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(250,0));
        pane.add(scrollPane, GUI.generateGBC(0,1,1,1,0,1,GridBagConstraints.VERTICAL, 0, GridBagConstraints.NORTHWEST));
    }
    public void setupDetails() {
        JButton createButton = new JButton("Create");
        JButton importButton = new JButton("Import");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton exportButton = new JButton("Export");
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        exportButton.setEnabled(false);
        JPanel TopButtons = new JPanel(new GridLayout(1,5,5,5));
        createButton.addActionListener(e -> {
            Billboard created = BillboardOptions.BillboardEditor(username);
            if(created != null) {
                client.sendMessage(new Message(token).createBillboard(created));
                updateTable();
            }
        });
        importButton.addActionListener(e -> {
            try {
                fileSelection();
            } catch (ParserConfigurationException | IOException | SAXException ex) {
                ex.printStackTrace();
            }
        });

        TopButtons.add(createButton);
        TopButtons.add(importButton);
        TopButtons.add(editButton);
        TopButtons.add(deleteButton);
        TopButtons.add(exportButton);
        pane.add(TopButtons, GUI.generateGBC(0,0,2,1,0,0,0, 5, GridBagConstraints.WEST));
        this.preview = new JLabel("");
        GridBagConstraints previewGBC = GUI.generateGBC(2,0,1,2,0,0,GridBagConstraints.BOTH,0,GridBagConstraints.NORTHWEST);
        previewGBC.insets = new Insets(0,0,0,18);
        pane.add(preview, previewGBC);
        this.information = new JPanel();
        information.setLayout(new BoxLayout(information, BoxLayout.PAGE_AXIS));
        information.add(new JLabel("Choose a billboard."));
        pane.add(information, GUI.generateGBC(1,1,1,2,1,1,GridBagConstraints.HORIZONTAL,18,GridBagConstraints.NORTHWEST));



        ListSelectionModel rowSelected = table.getSelectionModel();
        rowSelected.addListSelectionListener(e -> {
            if (!rowSelected.isSelectionEmpty()){
                this.selected = rowSelected.getMinSelectionIndex();
                editButton.setEnabled(true);
                exportButton.setEnabled(true);
                deleteButton.setEnabled(true);
                this.preview.setIcon(new BillboardToImage(billboards.get(selected),pane.getWidth()-600,(int)((pane.getWidth()-600)/1.77)).toImageIcon()); ;
                information.removeAll();
                JLabel title = new JLabel("<html><h1>" + billboards.get(selected).getName() +"</h1><html>");
                title.setPreferredSize(new Dimension(200,30));
                title.setAlignmentX( Component.LEFT_ALIGNMENT );
                information.add(title);
                information.add(new JLabel("<html><h3> Created by: " + billboards.get(selected).getCreatorName() +"</h3><html>"));
                JTextField field = new JTextField();
                field.setText(billboards.get(selected).getPictureLink());
                field.setPreferredSize(new Dimension(1,20));
                field.setEditable(false);
                field.setAlignmentX( Component.LEFT_ALIGNMENT );
                JTextField field2 = new JTextField();
                field2.setText(billboards.get(selected).getMessageText());
                field2.setEditable(false);
                field2.setAlignmentX( Component.LEFT_ALIGNMENT );
                JTextField field3 = new JTextField();
                field3.setText(billboards.get(selected).getInformationText());
                field3.setEditable(false);
                field3.setAlignmentX( Component.LEFT_ALIGNMENT );
                JTextField field4 = new JTextField();
                field4.setText(billboards.get(selected).getBackgroundColour());
                field4.setEditable(false);
                field4.setAlignmentX( Component.LEFT_ALIGNMENT );
                JTextField field5 = new JTextField();
                field5.setText(billboards.get(selected).getMessageTextColour());
                field5.setEditable(false);
                field5.setAlignmentX( Component.LEFT_ALIGNMENT );
                JTextField field6 = new JTextField();
                field6.setText(billboards.get(selected).getInformationTextColour());
                field6.setEditable(false);
                field6.setAlignmentX( Component.LEFT_ALIGNMENT );
                information.add(new JLabel("URL/Data:"));
                information.add(field);
                information.add(Box.createVerticalStrut(10));
                information.add(new JLabel("Message:"));
                information.add(field2);
                information.add(Box.createVerticalStrut(10));
                information.add(new JLabel("Information:"));
                information.add(field3);
                information.add(Box.createVerticalStrut(10));
                information.add(new JLabel("Background Colour:"));
                information.add(field4);
                information.add(Box.createVerticalStrut(10));
                information.add(new JLabel("Message Text Colour:"));
                information.add(field5);
                information.add(Box.createVerticalStrut(10));
                information.add(new JLabel("Information Text Colour:"));
                information.add(field6);
                information.add(Box.createVerticalStrut(10));
                pane.validate();
                pane.repaint();
            }
        });

        pane.addComponentListener(new ComponentAdapter()
        {
            public void componentResized(ComponentEvent evt) {
                if(selected >-1) {
                    preview.setIcon(new BillboardToImage(billboards.get(selected),pane.getWidth()-600,(int)((pane.getWidth()-600)/1.77)).toImageIcon());
                    pane.validate();
                    pane.repaint();
                }
            }
        });

        editButton.addActionListener(e -> {
            if (!Objects.equals(editButton.getText(), "")){
                selected = rowSelected.getMinSelectionIndex();
                Billboard created = BillboardOptions.BillboardEditor(username, billboards.get(selected));
                if(created != null) {
                    client.sendMessage(new Message(token).updateBillboard(created));
                    updateTable();
                    information.removeAll();
                    preview.setIcon(null);
                    information.add(new JLabel("Choose a billboard."));
                    pane.validate();
                    pane.repaint();
                }
            }
            else JOptionPane.showMessageDialog(null, "Please select a billboard first.");
        });

        deleteButton.addActionListener(e -> {
            if (!Objects.equals(editButton.getText(), "")){
                int selected = rowSelected.getMinSelectionIndex();
                Billboard delete = billboards.get(selected);
                if(delete != null) {
                    client.sendMessage(new Message(token).deleteBillboard(delete));
                    updateTable();
                }
            }
            else JOptionPane.showMessageDialog(null, "Please select a billboard first.");
        });

        exportButton.addActionListener(e -> {
            JFileChooser f = new JFileChooser();
            f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            f.showSaveDialog(null);
            System.out.println(f.getSelectedFile());
        });



    }


    private void fileSelection() throws ParserConfigurationException, IOException, SAXException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("*.xml", "xml"));
        int result = fileChooser.showOpenDialog(pane);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            Billboard selected = GenerateBillboardFromXML.XMLToBillboard(selectedFile, selectedFile.getName(), username);
            client.sendMessage(new Message(token).createBillboard(selected));
            updateTable();
        }
            /*
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
             */
    }

}