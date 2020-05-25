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
import java.util.Collections;



public class ScheduleTab {
    private JPanel pane;
    private Client client;
    private String username;
    private String token;
    public ScheduleTab(JTabbedPane mainPane, ArrayList<Integer> permissions, Client client,  String token, String username){
        this.pane = new JPanel();
        this.client = client;
        this.username = username;
        this.token = token;
        pane.setLayout(new GridBagLayout());
        scheduleView();
        mainPane.addTab("Schedule", pane);
    }

    private void scheduleView() {
        ArrayList<JScrollPane> columns = new ArrayList<>();
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        JPanel topBar = new JPanel(new GridLayout(1,3, 5, 5));
        JButton createButton = new JButton("Schedule a Billboard");

        createButton.addActionListener(e -> {
            Scheduled created = ScheduleOptions.ScheduleEditor(client, username, token);

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
            ArrayList<Event> events = ScheduleHelper.GenerateEvents(TestCase.schedule());
            int finalI1 = i;
            events.removeIf(n-> n.getDay() != finalI1 +1);
            Collections.reverse(events);
            int empty = 0;
            int count = 1;
            int current = 0;
            for (int x = 0; x < 1440; x++) {
                for(int y = 0; y < events.size(); y++) {
                    if(x >= events.get(y).getStartTime() && x < events.get(y).getEndTime()) {
                        if(empty != 0) {
                            model.addRow(new Object[]{"Break: "+ empty});
                            table.setRowHeight(model.getRowCount()-1,empty*2);
                            empty=0;
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
                        empty++;
                    }
                }
            }
            if (empty != 0) {
                model.addRow(new Object[]{"Break: "+ empty});
                table.setRowHeight(model.getRowCount()-1,empty);
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
