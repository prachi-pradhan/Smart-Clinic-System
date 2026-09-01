package ui;

import service.AppointmentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class WaitlistPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private AppointmentService service =
            new AppointmentService();

    public WaitlistPanel() {

        setLayout(new BorderLayout());
        setBackground(UITheme.BG);

        String[] columns = {
                "ID",
                "Patient",
                "Doctor",
                "Date",
                "Time",
                "Priority",
                "Request Time"
        };

        model = new DefaultTableModel(columns, 0);

        table = new JTable(model);

        UITheme.styleTable(table);

        JScrollPane scrollPane =
                new JScrollPane(table);

        scrollPane.getViewport()
                .setBackground(UITheme.BG);

        add(scrollPane, BorderLayout.CENTER);

        loadWaitlist();

        Timer timer =
                new Timer(3000,
                        e -> loadWaitlist());

        timer.start();
    }

    private void loadWaitlist() {

        model.setRowCount(0);

        List<String[]> waitlist =
                service.getWaitlist();

        for (String[] row : waitlist) {

            model.addRow(row);
        }
    }
}