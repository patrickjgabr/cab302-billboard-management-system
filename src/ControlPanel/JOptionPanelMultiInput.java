package ControlPanel;

import Shared.Billboard;

import javax.swing.*;
import java.awt.*;

class JOptionPaneMultiInput {
    public static void main (String[]args){
        System.out.println(MultiInputOptionPane().getInformationText());
        System.out.println(MultiInputOptionPane().getBackgroundColour());
        System.out.println(MultiInputOptionPane().getName());
        System.out.println(MultiInputOptionPane().getMessageText());
    }
    public static Billboard MultiInputOptionPane() {

        JTextField billboardName = new JTextField();
        JTextField imgSRC = new JTextField();
        JTextField messageText = new JTextField();
        JTextField messageColour = new JTextField();
        JTextField backgroundColour = new JTextField();
        JTextField infoText = new JTextField();
        JTextField infoColour = new JTextField();

        JPanel myPanel = new JPanel();
        myPanel.setLayout(new GridLayout(7,1));
        myPanel.add(new JLabel("Billboard Name: "));
        myPanel.add(billboardName);
        //myPanel.add(Box.createHorizontalStrut(15)); // a spacer
        myPanel.add(new JLabel("Image Source: "));
        myPanel.add(imgSRC);
        myPanel.add(new JLabel("Message Text: : "));
        myPanel.add(messageText);
        //myPanel.add(Box.createHorizontalStrut(15)); // a spacer
        myPanel.add(new JLabel("Message Colour: "));
        myPanel.add(messageColour);
        myPanel.add(new JLabel("Background Colour"));
        myPanel.add(backgroundColour);
        //myPanel.add(Box.createHorizontalStrut(15)); // a spacer
        myPanel.add(new JLabel("Info Text: "));
        myPanel.add(infoText);
        //myPanel.add(Box.createHorizontalStrut(15)); // a spacer
        myPanel.add(new JLabel("Info Colour: "));
        myPanel.add(infoColour);

        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Please Enter Billboard options", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            return new Billboard(billboardName.getText(), imgSRC.getText(), messageText.getText(), messageColour.getText(), backgroundColour.getText(), infoText.getText(), infoColour.getText());
        }
        return null;
    }
}