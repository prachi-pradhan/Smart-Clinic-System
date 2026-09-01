package ui;

import service.AppointmentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DoctorQueuePanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private JButton callButton;
    private JButton completeButton;

    private AppointmentService service = new AppointmentService();

    private String doctorName;

    public DoctorQueuePanel() {
        this(null);
    }

    public DoctorQueuePanel(String doctorName) {

        this.doctorName = doctorName;

        setLayout(new BorderLayout());
        setBackground(UITheme.BG);

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

        JScrollPane scrollPane = new JScrollPane(table);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(UITheme.BG);

        callButton = new JButton("Call Next Patient");
        completeButton = new JButton("Mark Completed");

        UITheme.styleButton(callButton);
        UITheme.styleButton(completeButton);

        buttonPanel.add(callButton);
        buttonPanel.add(completeButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        callButton.addActionListener(e -> callNextPatient());
        completeButton.addActionListener(e -> markCompleted());

        loadDoctorQueue();

        Timer timer = new Timer(3000, e -> loadDoctorQueue());
        timer.start();
    }

    private void loadDoctorQueue() {

        model.setRowCount(0);

        List<String[]> appointments;

        if (doctorName == null) {
            appointments = service.getDoctorQueue();
        } else {
            appointments = service.getDoctorAppointments(doctorName);
        }

        for (String[] row : appointments) {
            model.addRow(row);
        }
    }

    private void callNextPatient() {

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No patients in queue.");
            return;
        }

        table.setRowSelectionInterval(0, 0);

        String patientName = model.getValueAt(0, 1).toString();
        String time = model.getValueAt(0, 4).toString();

        JOptionPane.showMessageDialog(
                this,
                "Calling next patient:\n" + patientName + "\nTime: " + time);
    }

    private void markCompleted() {

        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment first.");
            return;
        }

        int appointmentId =
                Integer.parseInt(model.getValueAt(selectedRow, 0).toString());

        boolean success = service.completeAppointment(appointmentId);

        if (success) {
            JOptionPane.showMessageDialog(this, "Appointment marked as completed.");
            loadDoctorQueue();
        } else {
            JOptionPane.showMessageDialog(this, "Could not complete appointment.");
        }
    }
}