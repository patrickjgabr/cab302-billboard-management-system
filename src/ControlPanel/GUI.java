package ControlPanel;

import javax.swing.*;
import java.awt.*;

public class GUI {
    public static JFrame SetupFrame() {
        setLook();
        JFrame frame = new JFrame();
        frame.setPreferredSize(new Dimension(1080, 620));
        return frame;
    }

    private static void setLook() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            System.out.println("Error setting GUI look and feel");
        }
    }

    public static GridBagConstraints generateGBC(int gx, int gy, int gw, int gh, int wx, int wy, int fill, int inset, int anchor) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gx;
        gbc.gridy = gy;
        gbc.gridwidth = gw;
        gbc.gridheight = gh;
        gbc.weightx = wx;
        gbc.weighty = wy;
        gbc.fill = fill;
        gbc.insets = new Insets(inset,inset,inset,inset);
        gbc.anchor = anchor;
        return gbc;
    }

    public static void ServerDialogue(int ID, String message) {
        if (ID == 200) {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),message, "Server Response ID: " + ID,JOptionPane.INFORMATION_MESSAGE);
        }
        else {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),"Server Error: "+ ID + " Please contact Administrator." , "Server Response ID: " + ID,JOptionPane.WARNING_MESSAGE);
        }
    }
}
