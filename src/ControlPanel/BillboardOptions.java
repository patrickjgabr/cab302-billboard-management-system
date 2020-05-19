package ControlPanel;

import Shared.Billboard;

import javax.swing.*;
import java.awt.*;

public class BillboardOptions {
    //public static void main (String[]args){
    //   MultiInputOptionPane();
    //}
    public static Billboard BillboardEditor(String username) {

        JTextField billboardName = new JTextField();
        JTextField imgSRC = new JTextField();
        JTextField messageText = new JTextField();
        JTextField messageColour = new JTextField();
        JTextField backgroundColour = new JTextField();
        JTextField infoText = new JTextField();
        JTextField infoColour = new JTextField();
        return BillboardEditorGUI(username, billboardName,imgSRC,messageText,messageColour,backgroundColour,infoText,infoColour);
    }

    public static Billboard BillboardEditor(String username, Billboard billboard) {
        JTextField billboardName = new JTextField(billboard.getName());
        JTextField imgSRC = new JTextField(billboard.getPictureLink());
        JTextField messageText = new JTextField(billboard.getMessageText());
        JTextField messageColour = new JTextField(billboard.getMessageTextColour());
        JTextField backgroundColour = new JTextField(billboard.getBackgroundColour());
        JTextField infoText = new JTextField(billboard.getInformationText());
        JTextField infoColour = new JTextField(billboard.getInformationTextColour());
        return BillboardEditorGUI(username,billboardName,imgSRC,messageText,messageColour,backgroundColour,infoText,infoColour);
    }

    private static Billboard BillboardEditorGUI(String username,
                                                JTextField billboardName,
                                                JTextField imgSRC,
                                                JTextField messageText,
                                                JTextField messageColour,
                                                JTextField backgroundColour,
                                                JTextField infoText,
                                                JTextField infoColour){
        JPanel myPanel = new JPanel();
        myPanel.setLayout(new GridLayout(7,1));
        myPanel.add(new JLabel("Billboard Name: "));
        myPanel.add(billboardName);
        myPanel.add(Box.createHorizontalStrut(5)); // a spacer
        myPanel.add(new JLabel("Image Source: "));
        myPanel.add(imgSRC);
        myPanel.add(Box.createHorizontalStrut(5)); // a spacer
        myPanel.add(new JLabel("Message Text: "));
        myPanel.add(messageText);
        myPanel.add(Box.createHorizontalStrut(5)); // a spacer
        myPanel.add(new JLabel("Message Colour: "));
        myPanel.add(messageColour);
        myPanel.add(Box.createHorizontalStrut(5)); // a spacer
        myPanel.add(new JLabel("Background Colour: "));
        myPanel.add(backgroundColour);
        myPanel.add(Box.createHorizontalStrut(5)); // a spacer
        myPanel.add(new JLabel("Info Text: "));
        myPanel.add(infoText);
        myPanel.add(Box.createHorizontalStrut(5)); // a spacer
        myPanel.add(new JLabel("Info Colour: "));
        myPanel.add(infoColour);
        myPanel.add(Box.createHorizontalStrut(5)); // a spacer

        int result = JOptionPane.showConfirmDialog(null, myPanel, "Please Enter Billboard options", JOptionPane.YES_NO_CANCEL_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            return new Billboard(username,billboardName.getText(), imgSRC.getText(), messageText.getText(), messageColour.getText(), backgroundColour.getText(), infoText.getText(), infoColour.getText());
        }
        return null;
    }
}
