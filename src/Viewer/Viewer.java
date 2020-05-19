package Viewer;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import Shared.*;

public class Viewer {
   public static  void main (String[] args) {
        JFrame viewer = new JFrame("Viewer");
        File xml = new File("externalResources/18.xml");
        Billboard test = GenerateBillboardFromXML.XMLToBillboard(xml, "HARRY");
        //JPanel image = new BillboardToImage(test, 1280, 720).Generate();
        viewer.setExtendedState(JFrame.MAXIMIZED_BOTH);
        viewer.setUndecorated(true);
        viewer.getContentPane().add(image);
        viewer.pack();

        viewer.setVisible(true);
        //keyPressed();
    }
    private static void keyPressed(KeyEvent event) {

    }
}



