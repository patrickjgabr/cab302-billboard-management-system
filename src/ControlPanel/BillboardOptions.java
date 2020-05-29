package ControlPanel;

import Shared.Billboard;
import Shared.BillboardToImage;

import javax.swing.*;
import java.awt.*;

public class BillboardOptions {
    private JTextField billboardName = new JTextField();
    private JTextField imgSRC = new JTextField();
    private JTextField messageText = new JTextField();
    private JTextField messageColour = new JTextField();
    private JTextField backgroundColour = new JTextField();
    private JTextField infoText = new JTextField();
    private JTextField infoColour = new JTextField();
    private String username;


    public BillboardOptions(String username) {
        this.username = username;

    }

    public Billboard newBillboard() {
        return BillboardEditorGUI(new Billboard());
    }

    public Billboard editBillboard(Billboard billboard) {
        billboardName = new JTextField(billboard.getName());
        billboardName.setEditable(false);
        imgSRC = new JTextField(billboard.getPictureLink());
        messageText = new JTextField(billboard.getMessageText());
        messageColour = new JTextField(billboard.getMessageTextColour());
        backgroundColour = new JTextField(billboard.getBackgroundColour());
        infoText = new JTextField(billboard.getInformationText());
        infoColour = new JTextField(billboard.getInformationTextColour());
        return BillboardEditorGUI(billboard);
    }

    private Billboard BillboardEditorGUI(Billboard billboard){
        JPanel myPanel = new JPanel();
        myPanel.setLayout(new GridBagLayout());
        myPanel.add(new JLabel("Billboard Name: "),GUI.generateGBC(0,0,1,1,1,1,0,5,GridBagConstraints.WEST));
        billboardName.setPreferredSize(new Dimension(400,20));
        myPanel.add(billboardName, GUI.generateGBC(0,1,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(new JLabel("Image Source: "), GUI.generateGBC(0,2,1,1,1,1,0,5,GridBagConstraints.WEST));
        imgSRC.setPreferredSize(new Dimension(400,20));
        myPanel.add(imgSRC, GUI.generateGBC(0,3,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(new JLabel("Message Text: "), GUI.generateGBC(0,4,1,1,1,1,0,5,GridBagConstraints.WEST));
        messageText.setPreferredSize(new Dimension(400,20));
        myPanel.add(messageText, GUI.generateGBC(0,5,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(new JLabel("Message Colour: "), GUI.generateGBC(0,6,1,1,1,1,0,5,GridBagConstraints.WEST));
        messageColour.setPreferredSize(new Dimension(60,20));
        myPanel.add(messageColour, GUI.generateGBC(0,7,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(new JLabel("Background Colour: "), GUI.generateGBC(0,8,1,1,1,1,0,5,GridBagConstraints.WEST));
        backgroundColour.setPreferredSize(new Dimension(400,20));
        myPanel.add(backgroundColour, GUI.generateGBC(0,9,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(new JLabel("Info Text: "), GUI.generateGBC(0,10,1,1,1,1,0,5,GridBagConstraints.WEST));
        infoText.setPreferredSize(new Dimension(400,20));
        myPanel.add(infoText, GUI.generateGBC(0,11,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(new JLabel("Info Colour: "), GUI.generateGBC(0,12,1,1,1,1,0,5,GridBagConstraints.WEST));
        infoColour.setPreferredSize(new Dimension(60,20));
        myPanel.add(infoColour, GUI.generateGBC(0,13,1,1,1,1,0,5,GridBagConstraints.WEST));
        int result = JOptionPane.showConfirmDialog(null, myPanel, "Please Enter Billboard options", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            Billboard billboard2 = new Billboard(username,billboardName.getText(), imgSRC.getText(), messageText.getText(), messageColour.getText(), backgroundColour.getText(), infoText.getText(), infoColour.getText());
            billboard2.setBillboardID(billboard.getBillboardID());

            try {
                new BillboardToImage(billboard2,480,360).toImageIcon();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),"Unable to generate billboard. Invalid Properties.", "Invalid Billboard Properties",JOptionPane.WARNING_MESSAGE);
                return null;
            }
            return billboard2;
        }
        return null;
    }
}
