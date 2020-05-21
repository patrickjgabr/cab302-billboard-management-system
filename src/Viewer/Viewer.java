package Viewer;
import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import Shared.*;

public class Viewer {

    public static Billboard errorBillboard = new Billboard("ROOT", "Error Billboard", "" , "Error connecting to server","","","Retrying in 15","");

   public static  void main (String[] args) throws IOException {
        JFrame viewer = new JFrame("Viewer");                       //initialising jFrame viewer
        //File xml = new File("externalResources/18.xml");
        //Billboard test = GenerateBillboardFromXML.XMLToBillboard(xml, "Test");

        JPanel image = new BillboardToImage(errorBillboard, 1920, 1080).toJPanel();
        viewer.getContentPane().add(image);

        viewer.setExtendedState(JFrame.MAXIMIZED_BOTH);                 //setting up viewer to be full screen borderless
        viewer.setUndecorated(true);
        viewer.pack();
        viewer.setVisible(true);

        viewer.addMouseListener(new MouseAdapter() {                //Action listener listening for mouse clicks to close window on click
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(0); }});

        KeyListener listener = new KeyAdapter() {                   //Action listener listening for escape key presses to close window on key press
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
                    System.exit(0); }}};
        viewer.addKeyListener(listener);
    }
}



