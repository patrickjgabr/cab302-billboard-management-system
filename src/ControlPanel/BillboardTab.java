package ControlPanel;

import Shared.Billboard;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class BillboardTab {
    public static JPanel SetupBillboardsPane(JPanel pane) {
        JPanel panel1 = new JPanel();                           //first tab
        panel1.setBorder(BorderFactory.createEmptyBorder(30,20,10,20));
        panel1.setLayout(new GridLayout(2,1));
        panel1.setBackground(new Color(102,102,102));
        return panel1;
    }

    public static JPanel SetupBillboardsTable(JPanel pane, ArrayList<Billboard> billboards) {
        String[][] tableContents = new String[billboards.size()][9];
        Integer i = 0;
        for (Billboard billboard : billboards) {
            tableContents[i][0] = i.toString();
            tableContents[i][1] = billboard.getName();
            tableContents[i][2] = "TBA";
            tableContents[i][3] = billboard.getPictureLink();
            tableContents[i][4] = billboard.getMessageText();
            tableContents[i][5] = billboard.getMessageTextColour();
            tableContents[i][6] = billboard.getBackgroundColour();
            tableContents[i][7] = billboard.getInformationText();
            tableContents[i][8] = billboard.getInformationTextColour();
            i++;
        }
        String[] columns = {"ID","Billboard","Author", "IMG SRC","Message", "Msg Colour", "BG Colour", "Info Text", "Info Colour"};
        JTable table = new JTable(tableContents, columns);
        table.setRowHeight(40);
        table.setIntercellSpacing(new Dimension(10, 20));        //##
        //table.setFont(tableContentsF);
        //table.getTableHeader().setBackground(Color.GRAY);
        table.getTableHeader().setOpaque(false);
        //table.getTableHeader().setFont(tableHeader);
        table.setEnabled(false);                //uneditable
        pane.add(new JScrollPane(table));
        return pane;
    }
}
