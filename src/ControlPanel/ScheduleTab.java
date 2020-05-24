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
        pane.setLayout(new GridBagLayout());
        scheduleView();
        mainPane.addTab("Schedule", pane);
    }

    private void scheduleView() {
        ArrayList<JScrollPane> columns = new ArrayList<>();
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel topBar = new JPanel(new GridLayout(1,3, 5, 5));
        JButton createButton = new JButton("Schedule a Billboard");
        JButton editButton = new JButton("");
        editButton.setVisible(false);
        topBar.add(createButton);
        topBar.add(editButton);
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.gridwidth=7;
        gbc.weightx = 1;
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor= GridBagConstraints.NORTHWEST;
        pane.add(topBar,gbc);
        JLabel selected = new JLabel("Select Event");
        gbc.gridx= 0;
        gbc.gridy= 2;
        gbc.gridwidth =7;
        pane.add(selected, gbc);

        for (int i = 0; i < 7; i++) {
            DefaultTableModel model = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {

                    return false;
                }
            };
            model.addColumn(days[i]);
            JTable table = new JTable(model);
            JScrollPane scrollpane = new JScrollPane(table);
            scrollpane.setVerticalScrollBarPolicy((JScrollPane.VERTICAL_SCROLLBAR_NEVER));
            columns.add(scrollpane);
            ArrayList<Event> events = ScheduleHelper.GenerateEvents(TestCase.schedule());
            int finalI1 = i;
            events.removeIf(n-> n.getDay() != finalI1 +1);
            Collections.reverse(events);
            int breaktime = 0;
            int count = 1;
            int current = 0;
            for (int x = 0; x < 1440; x++) {
                for(int y = 0; y < events.size(); y++) {
                    if(x >= events.get(y).getStartTime() && x < events.get(y).getEndTime()) {
                        if(breaktime != 0) {
                            model.addRow(new Object[]{"Break: "+ breaktime});
                            table.setRowHeight(model.getRowCount()-1,breaktime*2);
                            breaktime=0;
                            current=0;
                            count =1;
                        }
                        else if (current==0){
                            model.addRow(new Object[]{});
                            current = events.get(y).getEventID();
                        }
                        else if(current != events.get(y).getEventID()) {
                            model.addRow(new Object[]{});
                            current = events.get(y).getEventID();
                            count = 1;
                        }
                        else {
                            table.setRowHeight(model.getRowCount()-1,count*2);
                        }
                        if (current != 0) {
                            table.setValueAt("Event ID: "+ current + "\n" + count + " minutes.",model.getRowCount()-1,0);
                        }
                        count++;
                        break;
                    }
                    if(y == events.size()-1) {
                        breaktime++;
                    }
                }
            }

            if (breaktime != 0) {
                model.addRow(new Object[]{"Break: "+ breaktime});
                table.setRowHeight(model.getRowCount()-1,breaktime);
            }
            table.setDefaultRenderer(Object.class, new MultiLineCellRenderer());
            table.setRowSelectionAllowed(false);
            table.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                        JTable target = (JTable)e.getSource();
                        int row = target.getSelectedRow();
                        int column = target.getSelectedColumn();
                        String text = (String) target.getValueAt(row,column);
                        if (!text.contains("Break")) {
                            editButton.setText("Edit " + text.split("\n")[0]);
                            selected.setText(text.replace("\n", " Duration: "));
                            editButton.setVisible(true);
                        }
                        else {
                            editButton.setVisible(false);
                            selected.setText("Select Event");
                        }

                }
            });
        }

        columns.get(6).setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        System.out.println(columns.size());
        gbc = new GridBagConstraints();
        for(int x = 0; x < 7; x++) {
            columns.get(x).getVerticalScrollBar().setModel(columns.get(6).getVerticalScrollBar().getModel());
            gbc.gridy =1;
            gbc.gridx =x;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1;
            gbc.weighty = 1;
            gbc.gridwidth = 1;
            pane.add(columns.get(x),gbc);
        }









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
