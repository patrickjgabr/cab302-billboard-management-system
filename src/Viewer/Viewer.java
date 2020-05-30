package Viewer;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Scanner;

import ControlPanel.Client;
import Shared.*;

import static java.lang.Thread.sleep;

public class Viewer {
    public static Billboard noBillboardScheduled = new Billboard("ROOT", "Sched Error Billboard", "" , "No Billboard Scheduled","#FFFFFF","#000000","Retrying in 15","");
    public static Billboard errorConnectingServer = new Billboard("ROOT", "Server Error Billboard", "" , "Error connecting to server","#000000","","Retrying in 15","");

   public static  void main (String[] args) {
       boolean shouldSleep = true;
       Billboard current = errorConnectingServer;
       Client client = new Client();
       Message requestSched = new Message().getScheduleViewer();
       JFrame viewer = new JFrame("Viewer");                       //initialising jFrame viewer
       viewer.setExtendedState(JFrame.MAXIMIZED_BOTH);                  //setting up viewer to be full screen borderless
       viewer.setUndecorated(true);

       viewer.addMouseListener(new MouseAdapter() {                     //Action listener listening for mouse clicks to close window on click
           @Override
           public void mouseClicked(MouseEvent e) {
               System.exit(0); }});

       KeyListener listener = new KeyAdapter() {                        //Action listener listening for escape key presses to close window on key press
           @Override
           public void keyReleased(KeyEvent e) {
               if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
                   System.exit(0); }}};
       viewer.addKeyListener(listener);

       ArrayList<String> marioData = new ArrayList<>();
       try {
           Scanner fileScanner = new Scanner(new File("externalResources/mario.txt"));
           for (int i =0;i<7;i++){
               marioData.add(fileScanner.nextLine());
           }

       } catch (FileNotFoundException e) {
           e.printStackTrace();
       }

       //142 ms per frame
       //

       while (true){
           shouldSleep = true;
           current = errorConnectingServer;                             //if message fails to send because of a server error, billboard to display is error connecting billboard.
           System.out.println("REFRESHED");
           try{
               Message received = client.sendMessage(requestSched);     //request schedule from client
               if(received.getCommunicationID() == 201) {
                   current = noBillboardScheduled;                      //if error code 201 appears, no billboard scheduled will display
               } else if (received.getCommunicationID() == 200) {
                   current = (Billboard) received.getData();            //otherwise billboard to display is the reply from the message
               }
           } catch (Exception e){
               System.out.println("FAIL");
           }
           if(current == errorConnectingServer){
               shouldSleep = false;

               int counter=0;
               for(int i=105;i>0;i--){
                   Billboard errorCountdown = new Billboard("ROOT", "Server Error Billboard", marioData.get(counter) , "Error connecting to server","#000000","",("Retrying in " + i/7 +" seconds..."),"");
                   JPanel image = new BillboardToImage(errorCountdown, 400, 400).toJPanel();
                   viewer.getContentPane().add(image);
                   viewer.pack();
                   viewer.setVisible(true);
                   try { sleep(140); }
                   catch (InterruptedException e) { e.printStackTrace(); }
                   counter++;
                   if(counter==7){counter=0;}
               }
           }
            if(shouldSleep){
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
}




