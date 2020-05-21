package ControlPanel;

import Shared.Billboard;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import static ControlPanel.CustomFont.*;
import static ControlPanel.CustomFont.tableHeader;

public class ScheduleTab {
    private JPanel pane;
    public ScheduleTab(JTabbedPane mainPane, ArrayList<Integer> permissions, Client client,  String Token){
        this.pane = new JPanel();
        scheduleView();
        mainPane.addTab("Schedule", pane);
    }

    private void scheduleView() {

        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        for (int i = 0; i < 7; i++) {
            DefaultTableModel model = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    //all cells false
                    return false;
                }
            };
            String[] data = {"test1","test2"};
            model.addColumn(days[i], data);
            JTable table = new JTable(model);
            pane.add(new JScrollPane(table));
            if (days[i].equals("Sunday")) {
                table.setRowHeight(1,100);
            }
            table.setRowSelectionAllowed(false);
            int finalI = i;
            table.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                        JTable target = (JTable)e.getSource();
                        int row = target.getSelectedRow();
                        int column = target.getSelectedColumn();
                        Object text = target.getValueAt(row,column);
                        System.out.println(" " + row + " " + column+ " "+ days[finalI] + " " + text);
                        // do some action if appropriate column

                }
            });
        }
        pane.setLayout(new GridLayout(1,7));








    }
}
