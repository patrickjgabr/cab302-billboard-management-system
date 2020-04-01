package ControlPanel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

public class ControlPanelGUI {
    public static void main(String[] args){
        drawPanel();
    }
    static Font title = new Font("Boulder", Font.BOLD, 36);
    static Font Tabs = new Font("Comic Sans", Font.BOLD, 16);
    static Font tableContents = new Font("Comic Sans", Font.PLAIN, 18);
    static Font tableHeader = new Font("Comic Sans", Font.ITALIC, 22);


    public static void drawPanel(){
        setLook();                      //method hidden down below to avoid clutter (requires lots of exception catches)
        JFrame f = new JFrame();
        f.setPreferredSize(new Dimension(920, 620));
        f.setLocation(600,250);     //near middle of screen

        //BorderLayout border = new BorderLayout(10,10);

        JTabbedPane pane = new JTabbedPane();
        pane.setFont(Tabs);
        JPanel panel1 = new JPanel();                           //first tab
        panel1.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
        panel1.setLayout(new GridLayout(3, 1));
        panel1.setBackground(new Color(102,102,102));
        panel1.add(new JLabel("     List Billboards")).setFont(title);
        String[][] testTable = {
                {"  Sample BB", "  Harry", "10/08/2019","" },
                {"  Sample BB", "  Harry", "10/08/2019","" },
                {"  Sample BB", "  Harry", "10/08/2019","" },
                {"  Test number 2", "  Jeff", "05/08/2019","" },
                {"  Test number 2", "  Jeff", "05/08/2019","" },
                {"  Test number 2", "  Jeff", "05/08/2019","" }
        };
        String[] columns = {"Billboard","Author", "Date","\t\t\t"};
        JTable table = new JTable(testTable, columns);
        table.setRowHeight(40);
        table.setIntercellSpacing(new Dimension(10, 20));        //##
        table.setFont(tableContents);
        table.getTableHeader().setBackground(Color.GRAY);
        table.getTableHeader().setOpaque(false);
        table.getTableHeader().setFont(tableHeader);
        table.setEnabled(false);                //uneditable
        panel1.add(new JScrollPane(table));

        JPanel panel2 = new JPanel();                           //second tab
        JPanel panel3 = new JPanel();                           //third tab


        panel2.add(new JLabel("this is gonna be the schedule billboards area"));
        panel2.setFont(tableContents);
        panel3.add(new JLabel( "this is where the edit users section will be"));

        pane.add("List Billboards", panel1);
        pane.add("Schedule Billboards", panel2);
        pane.add("Edit Users", panel3);

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
