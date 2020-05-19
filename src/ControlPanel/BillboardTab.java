package ControlPanel;
import Shared.Billboard;
import Shared.Message;
import Shared.BillboardToImage;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import static ControlPanel.CustomFont.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class BillboardTab{
    private JTable table;
    private ArrayList<Billboard> billboards;
    private JPanel pane;
    private Client client;
    private String token;
    private String username;

    public BillboardTab(JTabbedPane mainPane, ArrayList<Integer> permissions, Client client, String token, String username){
        this.client = client;
        this.username = username;
        this.token = token;
        this.billboards = billboards;
        this.pane = new JPanel();                                                      //first tab
        pane.setLayout(new GridBagLayout());
        setupTopButtons();
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

    public void setupTopButtons() {
        Button createButton = new Button("Create");
        Button importButton = new Button("Import");
        JPanel TopButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));

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
            } catch (ParserConfigurationException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (SAXException ex) {
                ex.printStackTrace();
            }
        });

        TopButtons.add(createButton);
        TopButtons.add(importButton);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        pane.add(TopButtons,gbc);


    }

    public void setupBillboardsTable() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Billboard");
        this.table = new JTable(model);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
        table.setRowHeight(90);
        table.setTableHeader(null);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.weighty =1;
        gbc.weightx =0;
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(300,0));
        pane.add(scrollPane, gbc);
    }
    public void setupDetails() {

        JLabel test = new JLabel("Select a Billboard");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.gridheight= 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        pane.add(test, gbc);



        ListSelectionModel rowSelected = table.getSelectionModel();             //setup list selection model to listen for a selection of the table
        rowSelected.addListSelectionListener(e -> {
            if (!rowSelected.isSelectionEmpty()){
                int selected = rowSelected.getMinSelectionIndex();

                test.setText(billboards.get(selected).getName());
            }
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