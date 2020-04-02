package ControlPanel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.SoftBevelBorder;

public class ControlPanelGUI {
    public static void main(String[] args){
        drawPanel();
    }
    static Font title = new Font("Boulder", Font.BOLD, 36);
    static Font Tabs = new Font("Comic Sans", Font.PLAIN, 16);
    static Font buttons = new Font("Comic Sans", Font.PLAIN, 26);
    static Font tableContentsF = new Font("Comic Sans", Font.PLAIN, 16);
    static Font tableHeader = new Font("Comic Sans", Font.ITALIC, 18);
    static Color softBlue = new Color(63,100,127);
    static Color lightGray = new Color(140,150,150);
    static Color tableLightGray = new Color(125,130,130);

    public static void drawPanel(){
        setLook();                      //method hidden down below to avoid clutter (requires lots of exception catches)
        JFrame f = new JFrame();
        f.setPreferredSize(new Dimension(1080, 620));
        f.setLocation(600,250);     //near middle of screen

        JTabbedPane pane = new JTabbedPane();
        pane.setFont(Tabs);
        JPanel panel1 = new JPanel();                           //first tab
        panel1.setBorder(BorderFactory.createEmptyBorder(30,20,10,20));

        panel1.setLayout(new GridLayout(2,1));

        panel1.setBackground(lightGray);

        String[][] tableContents = {
                {"1","  Sample BB", "  Harry", "/url/url.com","Hello World", "Black","Grey", "Hello", "White"},
                {"2","  Sample BB", "  Harry", "/url/url.com","Hello World", "Black","Grey", "Hello", "White"},
                {"3","  Sample BB", "  Harry", "/url/url.com","Hello World", "Black","Grey", "Hello", "White"},
                {"4","  Sample BB", "  Harry", "/url/url.com","Hello World", "Black","Grey", "Hello", "White"},
                {"4","  Sample BB", "  Harry", "/url/url.com","Hello World", "Black","Grey", "Hello", "White"},
                {"4","  Sample BB", "  Harry", "/url/url.com","Hello World", "Black","Grey", "Hello", "White"},
                {"4","  Sample BB", "  Harry", "/url/url.com","Hello World", "Black","Grey", "Hello", "White"}

        };

        String[] columns = {"ID","Billboard","Author", "IMG SRC","Message", "Msg Colour", "BG Colour", "Info Text", "Info Colour"};
        JTable table = new JTable(tableContents, columns);
        table.getColumnModel().getColumn(0).setMaxWidth(35);
        //table.getColumnModel().getColumn(3).setCellRenderer();
        table.setRowHeight(40);
        table.setIntercellSpacing(new Dimension(10, 20));        //##
        table.setFont(tableContentsF);
        table.getTableHeader().setBackground(softBlue);
        //table.setShowVerticalLines(true);
        table.getTableHeader().setOpaque(false);
        table.getTableHeader().setFont(tableHeader);
        table.setEnabled(false);                //uneditable

        panel1.add(new JScrollPane(table));
        JLabel l = new JLabel();
        l.setLayout(new GridLayout(2,3));
        l.add(new JLabel());                                //fill in empty grid spaces (0,0) and (0,1) and (0,2)
        l.add(new JLabel());
        l.add(new JLabel());
        JButton b = new JButton("Preview");             //placed at grid space (1,1)

        JButton b2 = new JButton("Create New");                //placed at grid space (1,2)
        b.setBorder(BorderFactory.createLineBorder(Color.black, 3));
        b2.setBorder(BorderFactory.createLineBorder(Color.black, 3));
        b.setFont(buttons);
        b2.setFont(buttons);
        b.setForeground(softBlue);
        b2.setForeground(softBlue);
        l.add(b);
        l.add(new JLabel());

        l.add(b2);
        panel1.add(l);

        JPanel panel2 = new JPanel();                           //second tab
        JPanel panel3 = new JPanel();                           //third tab

        panel2.add(new JLabel("this is gonna be the schedule billboards area"));
        panel2.setFont(tableContentsF);
        panel3.add(new JLabel( "this is where the edit users section will be"));

        ImageIcon list = new ImageIcon("Pictures/icon.png");

        pane.addTab("List Billboards", panel1);
        pane.addTab("Schedule Billboards", panel2);
        pane.addTab("Edit Users", panel3);

        pane.setEnabledAt(2, false);

        f.getContentPane().add(pane);

        f.pack();
        f.setVisible(true);
    }
    public static void setLook(){try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
        e.printStackTrace();
        System.out.println("Error setting GUI look and feel");
    }}
}
