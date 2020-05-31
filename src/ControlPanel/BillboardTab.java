package ControlPanel;

import Shared.Billboard;
import Shared.BillboardToImage;
import Shared.Client;
import Shared.Message;
import Viewer.GenerateBillboardFromXML;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Objects;

/**
 * BillboardTab class handles all the functionality that involves the creation, deletion and editing of the billboards.
 * This class will generate a GUI for the user to interface with the billboards stored in the server.
 */
public class BillboardTab {
    private JTable table;
    private ArrayList<Billboard> billboards;
    private JPanel pane;
    private Client client;
    private String token;
    private String username;
    private JLabel preview = new JLabel("");
    private int selected;
    private JPanel information;
    private ArrayList<Integer> permissions;
    private JButton editButton = new JButton("Edit");
    private JButton createButton = new JButton("Create");
    private JButton deleteButton = new JButton("Delete");
    private JButton exportButton = new JButton("Export");
    private JButton importButton = new JButton("Import");

    /**
     * Default constructor used to instantiate a BillboardTab object.
     *
     * @param mainPane    the parent JTabbedPane.
     * @param permissions contains an ArrayList of permissions of length 4.
     * @param client      object used to initiate communication to the server.
     * @param token       token of the currently logged in user.
     */
    public BillboardTab(JTabbedPane mainPane, ArrayList<Integer> permissions, Client client, String token, String username) {
        this.client = client;
        this.username = username;
        this.token = token;
        this.pane = new JPanel();
        this.selected = -1;
        this.permissions = permissions;
        pane.setLayout(new GridBagLayout());
        setupBillboardsTable();
        updateTable();
        setupPane();
        mainPane.addTab("Billboard", pane);
        //pane listener to update on change.
        mainPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (e.getSource() instanceof JTabbedPane) {
                    JTabbedPane panel = (JTabbedPane) e.getSource();
                    if (panel.getSelectedIndex() == 0) {
                        updateTable();
                        pane.validate();
                        pane.repaint();
                    }
                }
            }
        });
    }

    //updates the elements in the table by requesting a new list of billboards;
    private void updateTable() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        this.billboards = (ArrayList<Billboard>) client.sendMessage(new Message(token).requestBillboards()).getData();
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        for (Billboard billboard : billboards) {
            model.addRow(new Object[]{
                    "<html><b>" + billboard.getName() + "</b><br>ID: " + billboard.getBillboardID() + "<br>Created by: " + billboard.getCreatorName() + "</html>"});
        }
    }

    //Creates initial Billboard Table
    private void setupBillboardsTable() {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        model.addColumn("Billboard");
        this.table = new JTable(model);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowHeight(90);
        table.setTableHeader(null);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(250, 0));
        pane.add(scrollPane, GUI.generateGBC(0, 1, 1, 1, 0, 1, GridBagConstraints.VERTICAL, 0, GridBagConstraints.NORTHWEST));
    }


    private void setupPane() {
        //initialise top buttons panel
        JPanel menuButtons = new JPanel(new GridLayout(1, 5, 5, 5));
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        exportButton.setEnabled(false);
        createButton.setEnabled(false);
        importButton.setEnabled(false);
        if (permissions.get(0).equals(1)) {
            createButton.setEnabled(true);
            importButton.setEnabled(true);
        }
        menuButtons.add(createButton);
        menuButtons.add(importButton);
        menuButtons.add(editButton);
        menuButtons.add(deleteButton);
        menuButtons.add(exportButton);
        pane.add(menuButtons, GUI.generateGBC(0, 0, 2, 1, 0, 0, 0, 5, GridBagConstraints.WEST));

        //setup Billboard Preview
        GridBagConstraints previewGBC = GUI.generateGBC(2, 0, 1, 2, 0, 0, GridBagConstraints.BOTH, 0, GridBagConstraints.NORTHWEST);
        previewGBC.insets = new Insets(0, 0, 0, 18);
        pane.add(preview, previewGBC);

        //initialise information pane.
        this.information = new JPanel();
        information.setLayout(new GridBagLayout());
        information.add(new JLabel("Choose a billboard."));
        pane.add(information, GUI.generateGBC(1, 1, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL, 18, GridBagConstraints.NORTHWEST));

        //Billboard table option is selected.
        ListSelectionModel rowSelected = table.getSelectionModel();
        rowSelected.addListSelectionListener(e -> {
            if (!rowSelected.isSelectionEmpty()) {
                this.selected = rowSelected.getMinSelectionIndex();

                //handle buttons based on permissions
                if (billboards.get(selected).getCreatorName().equals(username) || permissions.get(1).equals(1)) {
                    if (billboards.get(selected).getScheduled() == 0 || permissions.get(1).equals(1)) {
                        editButton.setEnabled(true);
                        deleteButton.setEnabled(true);
                    }
                    exportButton.setEnabled(true);
                } else {
                    editButton.setEnabled(false);
                    exportButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                }

                //render GUI Components
                information.removeAll();
                this.preview.setIcon(new BillboardToImage(billboards.get(selected), pane.getWidth() - 600, (int) ((pane.getWidth() - 600) / 1.77)).toImageIcon());
                JLabel title = new JLabel("<html><h1>" + billboards.get(selected).getName() + "</h1><html>");
                title.setPreferredSize(new Dimension(280, 30));
                JLabel author = new JLabel("<html><h3> Created by: " + billboards.get(selected).getCreatorName() + "</h3><html>");
                author.setPreferredSize(new Dimension(280, 20));
                JTextField pictureField = new JTextField();
                pictureField.setText(billboards.get(selected).getImageUrl());
                pictureField.setPreferredSize(new Dimension(280, 20));
                pictureField.setEditable(false);
                JTextField msgText = new JTextField();
                msgText.setText(billboards.get(selected).getMessageText());
                msgText.setPreferredSize(new Dimension(280, 20));
                msgText.setEditable(false);
                JTextField infoText = new JTextField();
                infoText.setText(billboards.get(selected).getInformationText());
                infoText.setPreferredSize(new Dimension(280, 20));
                infoText.setEditable(false);
                JTextField bgColour = new JTextField();
                bgColour.setText(billboards.get(selected).getBackgroundColour());
                bgColour.setPreferredSize(new Dimension(280, 20));
                bgColour.setEditable(false);
                JTextField msgColour = new JTextField();
                msgColour.setText(billboards.get(selected).getMessageTextColour());
                msgColour.setPreferredSize(new Dimension(280, 20));
                msgColour.setEditable(false);
                JTextField infoColour = new JTextField();
                infoColour.setText(billboards.get(selected).getInformationTextColour());
                infoColour.setPreferredSize(new Dimension(280, 20));
                infoColour.setEditable(false);
                information.add(title, GUI.generateGBC(0, 0, 1, 1, 1, 1, 0, 2, GridBagConstraints.WEST));
                information.add(author, GUI.generateGBC(0, 1, 1, 1, 1, 1, 0, 5, GridBagConstraints.WEST));
                information.add(new JLabel("URL/Data:"), GUI.generateGBC(0, 2, 1, 1, 1, 1, 0, 2, GridBagConstraints.WEST));
                information.add(pictureField, GUI.generateGBC(0, 3, 1, 1, 1, 1, 0, 5, GridBagConstraints.WEST));
                information.add(new JLabel("Message:"), GUI.generateGBC(0, 4, 1, 1, 1, 1, 0, 2, GridBagConstraints.WEST));
                information.add(msgText, GUI.generateGBC(0, 5, 1, 1, 1, 1, 0, 5, GridBagConstraints.WEST));
                information.add(new JLabel("Information:"), GUI.generateGBC(0, 6, 1, 1, 1, 1, 0, 2, GridBagConstraints.WEST));
                information.add(infoText, GUI.generateGBC(0, 7, 1, 1, 1, 1, 0, 5, GridBagConstraints.WEST));
                information.add(new JLabel("Background Colour:"), GUI.generateGBC(0, 8, 1, 1, 1, 1, 0, 2, GridBagConstraints.WEST));
                information.add(bgColour, GUI.generateGBC(0, 9, 1, 1, 1, 1, 0, 5, GridBagConstraints.WEST));
                information.add(new JLabel("Message Text Colour:"), GUI.generateGBC(0, 10, 1, 1, 1, 1, 0, 2, GridBagConstraints.WEST));
                information.add(msgColour, GUI.generateGBC(0, 11, 1, 1, 1, 1, 0, 5, GridBagConstraints.WEST));
                information.add(new JLabel("Information Text Colour:"), GUI.generateGBC(0, 12, 1, 1, 1, 1, 0, 2, GridBagConstraints.WEST));
                information.add(infoColour, GUI.generateGBC(0, 13, 1, 1, 1, 1, 0, 5, GridBagConstraints.WEST));
                pane.validate();
                pane.repaint();
            }
        });

        //resize image if page is resized.
        pane.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                if (selected > -1) {
                    preview.setIcon(new BillboardToImage(billboards.get(selected), pane.getWidth() - 600, (int) ((pane.getWidth() - 600) / 1.77)).toImageIcon());
                    pane.validate();
                    pane.repaint();
                }
            }
        });


        importButton.addActionListener(e -> {
            fileSelection();
            updateTable();
            information.removeAll();
            preview.setIcon(null);
            information.add(new JLabel("Select a billboard."));
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
            exportButton.setEnabled(false);
            pane.validate();
            pane.repaint();
        });


        createButton.addActionListener(e -> {
            Billboard created = new BillboardOptions(username).newBillboard();
            if (created != null) {
                Message request = client.sendMessage(new Message(token).createBillboard(created));
                GUI.ServerDialogue(request.getCommunicationID(), "Create billboard successful.");
                updateTable();
                information.removeAll();
                preview.setIcon(null);
                information.add(new JLabel("Select a billboard."));
                editButton.setEnabled(false);
                deleteButton.setEnabled(false);
                exportButton.setEnabled(false);
                pane.validate();
                pane.repaint();
            }
        });

        editButton.addActionListener(e -> {
            if (!Objects.equals(editButton.getText(), "")) {
                selected = rowSelected.getMinSelectionIndex();
                Billboard edited = new BillboardOptions(username).editBillboard(billboards.get(selected));
                if (edited != null) {
                    Message request = client.sendMessage(new Message(token).updateBillboard(edited));
                    GUI.ServerDialogue(request.getCommunicationID(), "Edit billboard successful.");
                    updateTable();
                    information.removeAll();
                    preview.setIcon(null);
                    information.add(new JLabel("Choose a billboard."));
                    pane.validate();
                    pane.repaint();
                    editButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                    exportButton.setEnabled(false);
                }
            } else JOptionPane.showMessageDialog(null, "Please select a billboard first.");
        });

        deleteButton.addActionListener(e -> {
            if (!Objects.equals(editButton.getText(), "")) {
                selected = rowSelected.getMinSelectionIndex();
                Billboard delete = billboards.get(selected);
                if (delete != null) {
                    Message request = client.sendMessage(new Message(token).deleteBillboard(delete));
                    GUI.ServerDialogue(request.getCommunicationID(), "Delete billboard successful.");
                    updateTable();
                    information.removeAll();
                    preview.setIcon(null);
                    information.add(new JLabel("Select a user to view permissions."));
                    editButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                    exportButton.setEnabled(false);

                    pane.validate();
                    pane.repaint();
                }
            } else JOptionPane.showMessageDialog(null, "Please select a billboard first.");
        });


        exportButton.addActionListener(e -> {
            JFileChooser f = new JFileChooser();
            f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            f.showSaveDialog(null);
            System.out.println(f.getSelectedFile());
            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.newDocument();
                doc.setXmlStandalone(true);

                //create root element
                Element root = doc.createElement("billboard");
                Attr backgroundColour = doc.createAttribute("background");
                backgroundColour.setValue(billboards.get(selected).getBackgroundColour());
                root.setAttributeNode(backgroundColour);
                doc.appendChild(root);


                //export message if exists
                if (!billboards.get(selected).getMessageText().equals("")) {
                    Element message = doc.createElement("message");
                    message.appendChild(doc.createTextNode(billboards.get(selected).getMessageText()));
                    Attr messageColour = doc.createAttribute("colour");
                    messageColour.setValue(billboards.get(selected).getMessageTextColour());
                    message.setAttributeNode(messageColour);
                    root.appendChild(message);
                }


                //export picture if exists as data or url;
                if (!billboards.get(selected).getImageUrl().equals("")) {
                    Element picture = doc.createElement("picture");
                    try {
                        Attr data = doc.createAttribute("data");
                        Base64.getDecoder().decode(billboards.get(selected).getImageUrl());
                        data.setValue(billboards.get(selected).getImageUrl());
                        picture.setAttributeNode(data);
                    } catch (Exception ex) {
                        Attr url = doc.createAttribute("url");
                        url.setValue(billboards.get(selected).getImageUrl());
                        picture.setAttributeNode(url);
                    }
                    root.appendChild(picture);
                }


                //export information text if it exists
                if (!billboards.get(selected).getInformationText().equals("")) {
                    Element information = doc.createElement("information");
                    Attr infoColour = doc.createAttribute("colour");
                    infoColour.setValue(billboards.get(selected).getInformationTextColour());
                    information.setAttributeNode(infoColour);
                    information.appendChild(doc.createTextNode(billboards.get(selected).getInformationText()));
                    root.appendChild(information);
                }


                //Create XML file
                try {
                    TransformerFactory tFactory = TransformerFactory.newInstance();
                    Transformer tFormer = tFactory.newTransformer();
                    DOMSource source = new DOMSource(doc);
                    String xmlFilepath = f.getSelectedFile()
                            + "\\"
                            + billboards.get(selected).getBillboardID()
                            + " "
                            + billboards.get(selected).getName() + " "
                            + billboards.get(selected).getCreatorName() + ".xml";
                    StreamResult sResult = new StreamResult(new File(xmlFilepath));
                    tFormer.transform(source, sResult);
                    GUI.ServerDialogue(200, "Export Successful");
                } catch (Exception error) {
                    GUI.ServerDialogue(500, "");
                }
            } catch (ParserConfigurationException pce) {
                pce.printStackTrace();
            }
        });


    }

    //called when user imports a billboard.
    //imports billboard and shows editor.
    private void fileSelection() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("*.xml", "xml"));
        int result = fileChooser.showOpenDialog(pane);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            Billboard selected = GenerateBillboardFromXML.XMLToBillboard(selectedFile, selectedFile.getName(), username);
            Billboard preview = new BillboardOptions(username).editBillboard(selected);
            if (preview != null) {
                Message request = client.sendMessage(new Message(token).createBillboard(preview));
                GUI.ServerDialogue(request.getCommunicationID(), "Create billboard successful.");
            }
            updateTable();
        }

    }


}