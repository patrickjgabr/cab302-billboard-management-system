package ControlPanel;

import Shared.Billboard;
import Shared.BillboardToImage;

import javax.swing.*;
import java.awt.*;

public class BillboardOptions {
    private JTextField billboardName = new JTextField();
    private JTextField imgSRC = new JTextField();
    private JTextField messageText = new JTextField();
    private JColorChooser messageColourPicker = new JColorChooser();
    private JColorChooser backgroundColourPicker = new JColorChooser();
    private JTextField infoText = new JTextField();
    private JColorChooser infoColourPicker = new JColorChooser();
    private String username;

    /**
     * Setup BillboardOptions object. Required to create or edit billboard.
     * @param username username of user editing/creating billboard.
     */
    public BillboardOptions(String username) {
        this.username = username;
    }

    /**
     * Instantiates the billboard editor with new Billboard object
     * @return returns billboard object with edited fields.
     */
    public Billboard newBillboard() {
        return BillboardEditorGUI(new Billboard());
    }


    /**
     * Instantiates the billboard editor with existing billboard
     * @param billboard Billboard object used to obtain parameters to edit a billboard
     * @return returns edited billboard object with new fields.
     */

    public Billboard editBillboard(Billboard billboard) {
        billboardName = new JTextField(billboard.getName());
        billboardName.setEditable(false);
        imgSRC = new JTextField(billboard.getPictureLink());
        messageText = new JTextField(billboard.getMessageText());
        messageColourPicker.setColor(Color.decode(billboard.getMessageTextColour()));
        backgroundColourPicker.setColor(Color.decode(billboard.getBackgroundColour()));
        infoText = new JTextField(billboard.getInformationText());
        infoColourPicker.setColor(Color.decode(billboard.getInformationTextColour()));
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
        myPanel.add(new JLabel("Info Text: "), GUI.generateGBC(0,6,1,1,1,1,0,5,GridBagConstraints.WEST));
        infoText.setPreferredSize(new Dimension(400,20));
        myPanel.add(infoText, GUI.generateGBC(0,7,1,1,1,1,0,5,GridBagConstraints.WEST));

        myPanel.add(new JLabel("Background Colour: "), GUI.generateGBC(1,0,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(backgroundColourPicker, GUI.generateGBC(1,1,1,7,1,1,0,5,GridBagConstraints.WEST));
        backgroundColourPicker.setPreferredSize(new Dimension(450, 250));

        myPanel.add(new JLabel("Message Colour: "), GUI.generateGBC(0,8,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(messageColourPicker, GUI.generateGBC(0,9,1,1,1,1,0,5,GridBagConstraints.WEST));
        messageColourPicker.setPreferredSize(new Dimension(450, 250));

        myPanel.add(new JLabel("Info Colour: "), GUI.generateGBC(1,8,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(infoColourPicker, GUI.generateGBC(1,9,1,7,1,1,0,5,GridBagConstraints.WEST));
        infoColourPicker.setPreferredSize(new Dimension(450, 250));


        int result = JOptionPane.showConfirmDialog(null, myPanel, "Please Enter Billboard options", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            String messageColourHex = Integer.toHexString(messageColourPicker.getColor().getRGB() & 0xffffff);
            messageColourHex = '#' + messageColourHex;

            String backgroundColourHex = Integer.toHexString(backgroundColourPicker.getColor().getRGB() & 0xffffff);
            backgroundColourHex = '#' + backgroundColourHex;

            String infoColourHex = Integer.toHexString(infoColourPicker.getColor().getRGB() & 0xffffff);
            infoColourHex = '#' + infoColourHex;

            Billboard billboard2 = new Billboard(username,billboardName.getText(), imgSRC.getText(), messageText.getText(), messageColourHex, backgroundColourHex, infoText.getText(), infoColourHex);
            billboard2.setBillboardID(billboard.getBillboardID());

            //error check billboard before sending to server
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
