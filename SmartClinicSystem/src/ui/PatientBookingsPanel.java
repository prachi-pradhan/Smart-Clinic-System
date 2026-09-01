package ui;

import service.AppointmentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PatientBookingsPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JButton cancelButton;

    private AppointmentService service = new AppointmentService();
    private String username;

    public PatientBookingsPanel(String username) {

        this.username = username;

        setLayout(new BorderLayout());

        String[] columns = {
                "ID", "Patient", "Doctor", "Date", "Time", "Status"
        };

        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        UITheme.styleTable(table);

        cancelButton = new JButton("Cancel My Appointment");
        UITheme.styleDangerButton(cancelButton);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(cancelButton, BorderLayout.SOUTH);

        cancelButton.addActionListener(e -> cancelMyAppointment());

        loadBookings();

        Timer timer = new Timer(3000, e -> loadBookings());
        timer.start();
    }

    private void loadBookings() {

        model.setRowCount(0);

        List<String[]> appointments =
                service.getAppointmentsByPatient(username);

        for (String[] row : appointments) {
            model.addRow(row);
        }
    }

    private void cancelMyAppointment() {

        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment first.");
            return;
        }

        int appointmentId =
                Integer.parseInt(model.getValueAt(selectedRow, 0).toString());

        String status =
                model.getValueAt(selectedRow, 5).toString();

        if (status.equalsIgnoreCase("CANCELLED")) {
            JOptionPane.showMessageDialog(this, "This appointment is already cancelled.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Cancel this appointment?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {

            boolean success =
                    service.cancelAppointment(appointmentId);

            if (success) {
                JOptionPane.showMessageDialog(this, "Appointment cancelled successfully.");
                loadBookings();
            } else {
                JOptionPane.showMessageDialog(this, "Could not cancel appointment.");
            }
        }
    }
}