package ControlPanel;

import javax.swing.*;
import java.awt.*;

public class GUI {
    public static JFrame SetupFrame() {
        setLook();                      //method hidden down below to avoid clutter (requires lots of exception catches)
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

    public static JPanel billboards() {
        JPanel panel = new JPanel();
        return panel;
    }
    public static JPanel schedule() {
        JPanel panel = new JPanel();
        return panel;
    }
    public static JPanel userManagement() {
        JPanel panel = new JPanel();
        return panel;
    }
}
