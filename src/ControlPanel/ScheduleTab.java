package ControlPanel;

import Shared.*;
import Shared.Event;

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


        ArrayList<Event> events = ScheduleHelper.GenerateEvents(TestCase.schedule());

        for (Event x : events) {
            System.out.println(x);
        }

        for (int i = 0; i < 7; i++) {
            DefaultTableModel model = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    //all cells false
                    return false;
                }
            };
            model.addColumn(days[i]);
            JTable table = new JTable(model);
            pane.add(new JScrollPane(table));




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
