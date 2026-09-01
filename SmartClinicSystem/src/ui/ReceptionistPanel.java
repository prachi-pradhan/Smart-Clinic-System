package ui;

import service.AppointmentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReceptionistPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private JTextField searchField;

    private JButton searchButton;
    private JButton checkInButton;
    private JButton cancelButton;
    private JButton deleteButton;
    private JButton clearButton;

    private AppointmentService service = new AppointmentService();

    public ReceptionistPanel() {

        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.BG);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] columns = {
                "ID",
                "Patient",
                "Doctor",
                "Date",
                "Time",
                "Status"
        };

        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        UITheme.styleTable(table);

        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(180);

        JScrollPane scrollPane = new JScrollPane(table);

        JPanel topPanel = UITheme.card();
        topPanel.setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Receptionist Dashboard");
        UITheme.styleTitle(titleLabel);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(UITheme.CARD);

        JLabel searchLabel = new JLabel("Search Patient/Doctor:");
        UITheme.styleLabel(searchLabel);

        searchField = new JTextField(20);
        UITheme.styleTextField(searchField);

        searchButton = new JButton("Search");
        UITheme.styleButton(searchButton);

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);

        JPanel buttonPanel = UITheme.card();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 12, 8));

        checkInButton = new JButton("Check In Patient");
        cancelButton = new JButton("Cancel Appointment");
        deleteButton = new JButton("Delete Appointment");
        clearButton = new JButton("Clear Test Data");

        UITheme.styleButton(checkInButton);
        UITheme.styleButton(cancelButton);
        UITheme.styleDangerButton(deleteButton);
        UITheme.styleDangerButton(clearButton);

        buttonPanel.add(checkInButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> searchAppointments());
        checkInButton.addActionListener(e -> checkInSelectedPatient());
        cancelButton.addActionListener(e -> cancelSelectedAppointment());
        deleteButton.addActionListener(e -> deleteSelectedAppointment());
        clearButton.addActionListener(e -> clearTestData());

        loadAppointments();

        Timer timer = new Timer(3000, e -> {
            if (searchField.getText().trim().isEmpty()) {
                loadAppointments();
            }
        });

        timer.start();
    }

    public void loadAppointments() {

        model.setRowCount(0);

        List<String[]> appointments = service.getAllAppointments();

        for (String[] row : appointments) {
            model.addRow(row);
        }
    }

    private void searchAppointments() {

        String keyword = searchField.getText().trim().toLowerCase();

        model.setRowCount(0);

        List<String[]> appointments = service.getAllAppointments();

        for (String[] row : appointments) {

            String patient = row[1].toLowerCase();
            String doctor = row[2].toLowerCase();

            if (patient.contains(keyword) || doctor.contains(keyword)) {
                model.addRow(row);
            }
        }
    }

    private void checkInSelectedPatient() {

        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment first.");
            return;
        }

        int appointmentId =
                Integer.parseInt(model.getValueAt(selectedRow, 0).toString());

        boolean success =
                service.checkInPatient(appointmentId);

        if (success) {
            JOptionPane.showMessageDialog(this, "Patient checked in.");
            loadAppointments();
        } else {
            JOptionPane.showMessageDialog(this, "Could not check in patient.");
        }
    }

    private void cancelSelectedAppointment() {

        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment first.");
            return;
        }

        int appointmentId =
                Integer.parseInt(model.getValueAt(selectedRow, 0).toString());

        boolean success =
                service.cancelAppointment(appointmentId);

        if (success) {
            JOptionPane.showMessageDialog(this, "Appointment cancelled successfully.");
            loadAppointments();
        } else {
            JOptionPane.showMessageDialog(this, "Could not cancel appointment.");
        }
    }

    private void deleteSelectedAppointment() {

        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Permanently delete this appointment?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        int appointmentId =
                Integer.parseInt(model.getValueAt(selectedRow, 0).toString());

        boolean success =
                service.deleteAppointment(appointmentId);

        if (success) {
            JOptionPane.showMessageDialog(this, "Appointment deleted.");
            loadAppointments();
        } else {
            JOptionPane.showMessageDialog(this, "Could not delete appointment.");
        }
    }

    private void clearTestData() {

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete ALL appointments and waitlist data?",
                "Confirm",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            service.clearAllData();
            loadAppointments();
            JOptionPane.showMessageDialog(this, "All test data cleared.");
        }
    }
}