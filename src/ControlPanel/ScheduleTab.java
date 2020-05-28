package ControlPanel;
import Shared.*;
import Shared.Event;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;



public class ScheduleTab {
    private JPanel pane;
    private Client client;
    private String username;
    private String token;
    private ArrayList<Scheduled> schedule;
    public ScheduleTab(JTabbedPane mainPane, ArrayList<Integer> permissions, Client client,  String token, String username){
        this.pane = new JPanel();
        this.client = client;
        this.username = username;
        this.token = token;;
        //this.schedule = TestCase.schedule();
        pane.setLayout(new GridBagLayout());
        refresh();
        mainPane.addTab("Schedule", pane);
    }

    private String toTime(int min) {
        int hours = min / 60;
        if (hours == 0) {
            hours = 12;
        }
        int minutes = min % 60;
        String formatted = String.format("%02d", minutes);
        String period;
        if(hours > 12) {
            period = "PM";
        }
        else {
            period = "AM";
        }
        return (hours + ":" + formatted + period);


    }

    private  void refresh() {
        this.schedule = (ArrayList<Scheduled>) client.sendMessage(new Message(token).requestSchedule()).getData();
        pane.removeAll();
        pane.revalidate();
        pane.repaint();
        scheduleView();
    }

    private void scheduleView() {
        ArrayList<JScrollPane> columns = new ArrayList<>();
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        JPanel topBar = new JPanel(new GridLayout(1,3, 5, 5));
        JButton createButton = new JButton("Schedule a Billboard");

        createButton.addActionListener(e -> {
            Scheduled created = new ScheduleOptions(client, username, token).newSchedule();
            if(created != null) {
                client.sendMessage(new Message(token).scheduleBillboard(created));
                refresh();
            }
        });
        JButton editButton = new JButton("");
        editButton.setVisible(false);
        topBar.add(createButton);
        topBar.add(editButton);
        pane.add(topBar,GUI.generateGBC(0,0,7,1,1,0,0,5,GridBagConstraints.NORTHWEST));
        JLabel selected = new JLabel("Select Event");
        pane.add(selected, GUI.generateGBC(0,2,7,1,1,0,0,5,GridBagConstraints.NORTHWEST));

        for (int i = 0; i < 7; i++) {
            DefaultTableModel model = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            model.addColumn(days[i]);
            JTable table = new JTable(model);
            JScrollPane scrollPanel = new JScrollPane(table);
            scrollPanel.setVerticalScrollBarPolicy((JScrollPane.VERTICAL_SCROLLBAR_NEVER));
            columns.add(scrollPanel);
            ArrayList<Scheduled> todaySchedule = schedule;
            ArrayList<Event> events = ScheduleHelper.GenerateEvents(todaySchedule);
            Collections.reverse(events);
            int finalI = i;
            events.removeIf(n-> n.getDay() != finalI);
            int empty = 0;
            int current = 0;
            int count = 1;
            for (int x = 0; x < 1440; x++) {
                for(int y = 0; y < events.size(); y++) {
                    if(x >= events.get(y).getStartTime() && x < events.get(y).getEndTime()) {
                        if(empty != 0) {
                            model.addRow(new Object[]{"Empty"});
                            table.setRowHeight(model.getRowCount()-1,empty*2);
                            empty=0;
                            current=0;
                            count =0;
                        }
                        if (current != events.get(y).getEventID() || current==0) {
                            current = events.get(y).getEventID();
                            model.addRow(new Object[]{toTime(x) + "\nEvent ID: " + (current) +"\n"});

                            count = 1;
                        }
                        table.setRowHeight(model.getRowCount()-1,count*2);
                        if (current != 0) {
                            String[] strings = table.getValueAt(model.getRowCount()-1,0).toString().split("\n");
                            table.setValueAt( strings[0]+ "\n" + strings[1] + "\n" + count + " minutes.",model.getRowCount()-1,0);
                        }
                        count++;

                        break;
                    }
                    if(y == events.size()-1) {
                        empty++;
                    }
                }
            }
            if (empty != 0) {
                model.addRow(new Object[]{"Empty"});
                table.setRowHeight(model.getRowCount()-1,empty*2);
            }
            if (events.size() == 0) {
                model.addRow(new Object[]{"Empty"});
                table.setRowHeight(0,2280);
            }
            table.setDefaultRenderer(Object.class, new MultiLineCellRenderer());
            table.setRowSelectionAllowed(false);
            table.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                        JTable target = (JTable)e.getSource();
                        int row = target.getSelectedRow();
                        int column = target.getSelectedColumn();
                        String text = (String) target.getValueAt(row,column);
                        if (!text.contains("Empty")) {
                            editButton.setText("Edit " + text.split("\n")[1]);
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
        for(int x = 0; x < 7; x++) {
            columns.get(x).getVerticalScrollBar().setModel(columns.get(6).getVerticalScrollBar().getModel());
            pane.add(columns.get(x), GUI.generateGBC(x,1,1,1,1,1,GridBagConstraints.BOTH,0,GridBagConstraints.NORTHWEST));
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
