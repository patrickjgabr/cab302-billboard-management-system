package ControlPanel;

import Shared.*;
import Shared.Event;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

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

            model.addColumn(days[i]);
            System.out.println(days[i]);
            JTable table = new JTable(model);
            pane.add(new JScrollPane(table));

            ArrayList<Event> events = ScheduleHelper.GenerateEvents(TestCase.schedule());
            int finalI1 = i;
            events.removeIf(n-> n.getDay() != finalI1 +1);
            Collections.reverse(events);
            int breaktime = 0;
            for (int x = 0; x < 1440; x++) {
                for(int y = 0; y < events.size(); y++) {
                    if(x >= events.get(y).getStartTime() && x <= events.get(y).getEndTime()) {
                        if(breaktime != 0) {
                            System.out.println("Break Time: " +breaktime);
                            breaktime=0;
                        }
                        System.out.println("Minute: " + x + " ID: "+ events.get(y).getEventID());
                        break;
                    }
                    if(y == events.size()-1) {
                        breaktime++;
                        break;
                    }
                }
            }
            System.out.println("Break Time: " + breaktime);
            table.setDefaultRenderer(Object.class, new MultiLineCellRenderer());
            table.setRowHeight(45);
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
class MultiLineCellRenderer extends JTextArea implements TableCellRenderer {

    public MultiLineCellRenderer() {
        setLineWrap(true);
        setWrapStyleWord(true);
        setOpaque(true);
        setEditable(false);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        }
        else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }
        setFont(table.getFont());
        if (hasFocus) {
            setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
            if (table.isCellEditable(row, column)) {
                setForeground(UIManager.getColor("Table.focusCellForeground"));
                setBackground(UIManager.getColor("Table.focusCellBackground"));
            }
        }
        else {
            setBorder(new EmptyBorder(1, 2, 1, 2));
        }
        setText((value == null) ? "" : value.toString());
        return this;
    }
}
