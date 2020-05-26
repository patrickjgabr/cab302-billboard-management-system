package Viewer;
import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;

import ControlPanel.Client;
import Shared.*;

import static java.lang.Thread.sleep;

public class Viewer {



   public static  void main (String[] args) throws IOException {
       Billboard current = new Billboard();
       Client client = new Client();
       Message requestSched = new Message().getScheduleViewer();
       JFrame viewer = new JFrame("Viewer");                       //initialising jFrame viewer
       viewer.setExtendedState(JFrame.MAXIMIZED_BOTH);                 //setting up viewer to be full screen borderless
       viewer.setUndecorated(true);

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

       while (true){
           System.out.println("REFRESHED");
           try{
               Message recieved = client.sendMessage(requestSched);
               current = (Billboard) recieved.getData();

           }catch (Exception e){
               current = new Billboard("ROOT", "Error Billboard", "" , "Error connecting to server","","","Retrying in 15","");
           }

           JPanel image = new BillboardToImage(current, 400, 400).toJPanel();

           viewer.getContentPane().add(image);
           viewer.pack();
           viewer.setVisible(true);
           try {
               sleep(15000);
           } catch (InterruptedException e) {
               System.exit(0);
           }
       }
    }
}



