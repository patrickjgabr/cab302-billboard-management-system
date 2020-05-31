package Viewer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import ControlPanel.Client;
import Shared.*;

import static java.lang.Thread.sleep;

public class Viewer {
    //default billboards for non-scheduled and error cases
    public static Billboard noBillboardScheduled = new Billboard("ROOT", "Sched Error Billboard", "" , "No Billboard Scheduled","#FFFFFF","#000000","Retrying in 15","");
    public static Billboard errorConnectingServer = new Billboard("ROOT", "Server Error Billboard", "" , "Error connecting to server","#000000","","Retrying in 15","");

    /**
     * Main method for running viewer. Can be run on any device and as long as the server is also running, will display scheduled billboards
     * or an error screen with a loading animation and countdown if there is no server connection. Also displays appropriate billboard
     * if none are scheduled.
     * @param args
     */
   public static void main (String[] args) {
       GraphicsEnvironment graphics = GraphicsEnvironment.getLocalGraphicsEnvironment();
       GraphicsDevice device = graphics.getDefaultScreenDevice();
       Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
       int screenWidth = (int)screenSize.getWidth();
       int screenHeight = (int)screenSize.getHeight();
       boolean shouldSleep;
       Billboard current;
       Client client = new Client();
       Message requestSched = new Message().getScheduleViewer();
       JFrame viewer = new JFrame("Viewer");                       //initialising jFrame viewer
       viewer.setExtendedState(JFrame.MAXIMIZED_BOTH);                  //setting up viewer to be full screen borderless
       viewer.setUndecorated(true);
       viewer.setResizable(false);
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

       ArrayList<String> marioData = new ArrayList<>();             //string array containing data attributes for each frame of loading mario
       try {
           Scanner fileScanner = new Scanner(new File("externalResources/mario.txt"));      //file scanner reading frames
           for (int i =0;i<7;i++){
               marioData.add(fileScanner.nextLine());
           }

       } catch (FileNotFoundException e) {
           e.printStackTrace();
       }


       while (true){                                                    //continuous while loop for viewer - sleeps for 15 seconds after retrieving billboard.
           shouldSleep = true;
           current = errorConnectingServer;                             //if message fails to send because of a server error, billboard to display is error connecting billboard.
           try{
               Message received = client.sendMessage(requestSched);     //request schedule from client
               if(received.getCommunicationID() == 201) {
                   current = noBillboardScheduled;                      //if error code 201 appears, no billboard scheduled will display
               } else if (received.getCommunicationID() == 200) {
                   current = (Billboard) received.getData();            //otherwise billboard to display is the reply from the message
               }
           } catch (Exception e){
               System.out.println("Error Connecting to server.");
           }
           if(current == errorConnectingServer){
               shouldSleep = false;                 //dont sleep when error connecting to server

               int counter=0;
               for(int i=105;i>0;i--){          //main loop mario animation
                   Billboard errorCountdown = new Billboard("ROOT", "Server Error Billboard", marioData.get(counter) , "Error connecting to server","#000000","",("Retrying in " + i/7 +" seconds..."),"");
                   JPanel image = new BillboardToImage(errorCountdown, screenWidth, screenHeight).toJPanel();
                   viewer.getContentPane().add(image);
                   viewer.pack();
                   viewer.setVisible(true);
                   try { sleep(140); }
                   catch (InterruptedException e) { e.printStackTrace(); }
                   counter++;
                   if(counter==7){counter=0;}
               }
           }
            if(shouldSleep){        //normal case
                JPanel image = new BillboardToImage(current, screenWidth, screenHeight).toJPanel();
                viewer.getContentPane().add(image);
                viewer.pack();
                device.setFullScreenWindow(viewer);
                try {
                    sleep(15000);
                } catch (InterruptedException e) {
                    System.exit(0);
                }
            }
       }
   }
}




