package ControlPanel;

import Shared.Billboard;
import Shared.BillboardToImage;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Base64;
import java.util.Scanner;


/**
 * The BillboardOptions class is used to create or modify billboards
 */
public class BillboardOptions {
    private Billboard billboard = new Billboard();
    private JTextField billboardName = new JTextField();
    private JButton showPreview = new JButton("Preview");
    private JButton importFile = new JButton("Import");
    private JTextField imgSRC = new JTextField();
    private JTextField messageText = new JTextField();
    private JColorChooser messageColourPicker = new JColorChooser();
    private JColorChooser backgroundColourPicker = new JColorChooser();
    private JTextField infoText = new JTextField();
    private JColorChooser infoColourPicker = new JColorChooser();
    private JLabel preview = new JLabel("");
    private JPanel panel = new JPanel();
    private String blank;

    /**
     * Setup BillboardOptions object. Required to create or edit billboard.
     *
     * @param username username of user editing/creating billboard.
     */
    public BillboardOptions(String username) {

        //set Defaults
        billboard.setName("");
        billboard.setMessageText("");
        billboard.setBillboardID(0);
        billboard.setBackgroundColour("#ffffff");
        billboard.setInformationTextColour("#000000");
        billboard.setMessageTextColour("#000000");
        billboard.setInformationText("");
        billboard.setImageUrl("");
        billboard.setCreatorName(username);
        preview.setIcon(new BillboardToImage(billboard, 352, 240).toImageIcon());

        //load base64 image from file
        try {
            Scanner fileScanner = new Scanner(new File("externalResources/blank.txt"));
            blank = fileScanner.nextLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        showPreview.addActionListener(e -> {
            try {
                billboard.setImageUrl(imgSRC.getText());
                preview.setIcon(new BillboardToImage(billboard, 352, 240).toImageIcon());
            } catch (Exception ex) {
                imgSRC.setText("");
                billboard.setImageUrl(blank);
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Image/Data invalid", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        importFile.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            FileNameExtensionFilter png = new FileNameExtensionFilter("*.png", "png");
            FileNameExtensionFilter bmp = new FileNameExtensionFilter("*.bmp", "bmp");
            FileNameExtensionFilter jpeg = new FileNameExtensionFilter("*.jpeg", "jpeg");
            FileNameExtensionFilter jpg = new FileNameExtensionFilter("*.jpg", "jpg");
            fileChooser.addChoosableFileFilter(png);
            fileChooser.addChoosableFileFilter(bmp);
            fileChooser.addChoosableFileFilter(jpeg);
            fileChooser.addChoosableFileFilter(jpg);
            int result = fileChooser.showOpenDialog(JOptionPane.getRootFrame());
            if (result == JFileChooser.APPROVE_OPTION) {
                imgSRC.setText(encodeImage(fileChooser.getSelectedFile()));
            }
        });

        messageText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                billboard.setMessageText(messageText.getText());
                if (!imgSRC.getText().equals("")) {
                    billboard.setImageUrl(blank);
                }
                preview.setIcon(new BillboardToImage(billboard, 352, 240).toImageIcon());
                billboard.setImageUrl(imgSRC.getText());

            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                billboard.setMessageText(messageText.getText());
                if (!imgSRC.getText().equals("")) {
                    billboard.setImageUrl(blank);
                }
                preview.setIcon(new BillboardToImage(billboard, 352, 240).toImageIcon());
                billboard.setImageUrl(imgSRC.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                billboard.setMessageText(messageText.getText());
                if (!imgSRC.getText().equals("")) {
                    billboard.setImageUrl(blank);
                }
                preview.setIcon(new BillboardToImage(billboard, 352, 240).toImageIcon());
                billboard.setImageUrl(imgSRC.getText());
            }
        });

        infoText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                billboard.setInformationText(infoText.getText());
                if (!imgSRC.getText().equals("")) {
                    billboard.setImageUrl(blank);
                }
                preview.setIcon(new BillboardToImage(billboard, 352, 240).toImageIcon());
                billboard.setImageUrl(imgSRC.getText());
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                billboard.setInformationText(infoText.getText());
                if (!imgSRC.getText().equals("")) {
                    billboard.setImageUrl(blank);
                }
                preview.setIcon(new BillboardToImage(billboard, 352, 240).toImageIcon());
                billboard.setImageUrl(imgSRC.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                billboard.setInformationText(infoText.getText());
                if (!imgSRC.getText().equals("")) {
                    billboard.setImageUrl(blank);
                }
                preview.setIcon(new BillboardToImage(billboard, 352, 240).toImageIcon());
                billboard.setImageUrl(imgSRC.getText());
            }
        });

        imgSRC.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                billboard.setImageUrl(imgSRC.getText());
                if (!imgSRC.getText().equals("")) {
                    billboard.setImageUrl(blank);
                }
                preview.setIcon(new BillboardToImage(billboard, 352, 240).toImageIcon());
                billboard.setImageUrl(imgSRC.getText());
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                billboard.setImageUrl(imgSRC.getText());
                if (!imgSRC.getText().equals("")) {
                    billboard.setImageUrl(blank);
                }
                preview.setIcon(new BillboardToImage(billboard, 352, 240).toImageIcon());
                billboard.setImageUrl(imgSRC.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                billboard.setImageUrl(imgSRC.getText());
                if (!imgSRC.getText().equals("")) {
                    billboard.setImageUrl(blank);
                }
                preview.setIcon(new BillboardToImage(billboard, 352, 240).toImageIcon());
                billboard.setImageUrl(imgSRC.getText());
            }
        });

        infoColourPicker.getSelectionModel().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent arg0) {
                billboard.setInformationTextColour(rgbToHex(infoColourPicker.getColor().getRGB()));
                if (!imgSRC.getText().equals("")) {
                    billboard.setImageUrl(blank);
                }
                preview.setIcon(new BillboardToImage(billboard, 352, 240).toImageIcon());
                billboard.setImageUrl(imgSRC.getText());
            }
        });

        backgroundColourPicker.getSelectionModel().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent arg0) {
                billboard.setBackgroundColour(rgbToHex(backgroundColourPicker.getColor().getRGB()));
                if (!imgSRC.getText().equals("")) {
                    billboard.setImageUrl(blank);
                }
                preview.setIcon(new BillboardToImage(billboard, 352, 240).toImageIcon());
                billboard.setImageUrl(imgSRC.getText());
            }
        });

        messageColourPicker.getSelectionModel().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent arg0) {
                billboard.setMessageTextColour(rgbToHex(messageColourPicker.getColor().getRGB()));
                if (!imgSRC.getText().equals("")) {
                    billboard.setImageUrl(blank);
                }
                preview.setIcon(new BillboardToImage(billboard, 352, 240).toImageIcon());
                billboard.setImageUrl(imgSRC.getText());
            }
        });

    }

    private static String encodeImage(File image) {
        String base64Encoded = "";
        try (FileInputStream inputStream = new FileInputStream(image)) {
            byte[] imageBytes = new byte[(int) image.length()];
            inputStream.read(imageBytes);
            base64Encoded = Base64.getEncoder().encodeToString(imageBytes);
        } catch (FileNotFoundException e) {
            System.out.println("Error selecting image file. Please ensure it is available and not corrupt.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return base64Encoded;
    }

    /**
     * Instantiates the billboard editor with new Billboard object
     *
     * @return returns billboard object with edited fields.
     */
    public Billboard newBillboard() {
        return BillboardEditorGUI();
    }

    /**
     * Instantiates the billboard editor with existing billboard
     *
     * @param billboard Billboard object used to obtain parameters to edit a billboard
     * @return returns edited billboard object with new fields.
     */
    public Billboard editBillboard(Billboard billboard) {
        this.billboard.setBillboardID(billboard.getBillboardID());
        this.billboard.setCreatorName(billboard.getCreatorName());
        billboardName.setText(billboard.getName());
        billboardName.setEditable(false);
        imgSRC.setText(billboard.getImageUrl());
        messageText.setText(billboard.getMessageText());
        if (!billboard.getMessageTextColour().equals("")) {
            messageColourPicker.setColor(Color.decode(billboard.getMessageTextColour()));
        }
        if (!billboard.getBackgroundColour().equals("")) {
            backgroundColourPicker.setColor(Color.decode(billboard.getBackgroundColour()));
        }
        infoText.setText(billboard.getInformationText());
        if (!billboard.getInformationTextColour().equals("")) {
            infoColourPicker.setColor(Color.decode(billboard.getInformationTextColour()));
        }
        return BillboardEditorGUI();
    }

    private String rgbToHex(int rgb) {
        String messageColourHex = Integer.toHexString(rgb & 0xffffff);
        return '#' + messageColourHex;
    }

    //Creates the Billboard Editor GUI based on the private variables within the current Billboard Options object.
    private Billboard BillboardEditorGUI() {

        //Render GUI components
        panel.setLayout(new GridBagLayout());
        panel.add(new JLabel("Billboard Name: "), GUI.generateGBC(0, 0, 3, 1, 1, 1, 0, 5, GridBagConstraints.WEST));
        billboardName.setPreferredSize(new Dimension(400, 20));
        panel.add(billboardName, GUI.generateGBC(0, 1, 3, 1, 1, 1, 0, 5, GridBagConstraints.WEST));
        panel.add(new JLabel("Image Source: "), GUI.generateGBC(0, 2, 3, 1, 1, 1, 0, 5, GridBagConstraints.WEST));
        imgSRC.setPreferredSize(new Dimension(250, 20));
        panel.add(importFile, GUI.generateGBC(0, 3, 1, 1, 0, 1, 0, 5, GridBagConstraints.WEST));
        panel.add(imgSRC, GUI.generateGBC(1, 3, 1, 1, 0, 1, 0, 5, GridBagConstraints.WEST));
        panel.add(showPreview, GUI.generateGBC(2, 3, 1, 1, 0, 1, 0, 5, GridBagConstraints.WEST));
        panel.add(new JLabel("Message Text: "), GUI.generateGBC(0, 4, 3, 1, 1, 1, 0, 5, GridBagConstraints.WEST));
        messageText.setPreferredSize(new Dimension(400, 20));
        panel.add(messageText, GUI.generateGBC(0, 5, 3, 1, 1, 1, 0, 5, GridBagConstraints.WEST));
        panel.add(new JLabel("Info Text: "), GUI.generateGBC(0, 6, 3, 1, 1, 1, 0, 5, GridBagConstraints.WEST));
        infoText.setPreferredSize(new Dimension(400, 20));
        panel.add(infoText, GUI.generateGBC(0, 7, 3, 1, 1, 1, 0, 5, GridBagConstraints.WEST));
        preview.setPreferredSize(new Dimension(352, 240));
        panel.add(preview, GUI.generateGBC(5, 0, 1, 11, 1, 1, 0, 5, GridBagConstraints.WEST));
        panel.add(new JLabel("Background Colour: "), GUI.generateGBC(3, 0, 1, 1, 1, 1, 0, 5, GridBagConstraints.WEST));
        panel.add(backgroundColourPicker, GUI.generateGBC(3, 1, 1, 8, 1, 1, 0, 5, GridBagConstraints.WEST));
        backgroundColourPicker.setPreferredSize(new Dimension(450, 280));
        panel.add(new JLabel("Message Colour: "), GUI.generateGBC(0, 9, 3, 1, 1, 1, 0, 5, GridBagConstraints.WEST));
        panel.add(messageColourPicker, GUI.generateGBC(0, 10, 3, 1, 3, 1, 0, 5, GridBagConstraints.WEST));
        messageColourPicker.setPreferredSize(new Dimension(450, 280));
        panel.add(new JLabel("Info Colour: "), GUI.generateGBC(3, 9, 1, 1, 1, 1, 0, 5, GridBagConstraints.WEST));
        panel.add(infoColourPicker, GUI.generateGBC(3, 10, 1, 7, 1, 1, 0, 5, GridBagConstraints.WEST));
        infoColourPicker.setPreferredSize(new Dimension(450, 280));


        //create dialogue window containing Billboard Options UI elements.
        String[] options = new String[2];
        options[0] = "Submit";
        options[1] = "Cancel";
        int result = JOptionPane.showOptionDialog(null, panel, "Billboard Editor", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);

        if (result == 0) {
            billboard.setName(billboardName.getText());
            //error check billboard before sending to server
            try {
                new BillboardToImage(billboard, 480, 360).toImageIcon();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Unable to generate billboard. Invalid Properties.", "Invalid Billboard Properties", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            return billboard;
        }

        return null;
    }
}
