package ui;

import service.AppointmentService;

import javax.swing.*;
import java.awt.*;

public class StatsPanel extends JPanel {

    private JLabel totalAppointmentsLabel;
    private JLabel bookedLabel;
    private JLabel cancelledLabel;
    private JLabel waitlistLabel;
    private JLabel busiestDoctorLabel;

    private JButton refreshButton;

    private AppointmentService service =
            new AppointmentService();

    public StatsPanel() {

        setLayout(new GridLayout(6, 1, 15, 15));

        setBackground(UITheme.BG);

        setBorder(
                BorderFactory.createEmptyBorder(
                        40, 80, 40, 80));

        totalAppointmentsLabel = new JLabel();
        bookedLabel = new JLabel();
        cancelledLabel = new JLabel();
        waitlistLabel = new JLabel();
        busiestDoctorLabel = new JLabel();

        UITheme.styleLabel(totalAppointmentsLabel);
        UITheme.styleLabel(bookedLabel);
        UITheme.styleLabel(cancelledLabel);
        UITheme.styleLabel(waitlistLabel);
        UITheme.styleLabel(busiestDoctorLabel);

        refreshButton =
                new JButton("Refresh Statistics");

        UITheme.styleButton(refreshButton);

        add(totalAppointmentsLabel);
        add(bookedLabel);
        add(cancelledLabel);
        add(waitlistLabel);
        add(busiestDoctorLabel);
        add(refreshButton);

        refreshButton.addActionListener(
                e -> loadStats());

        loadStats();
    }

    private void loadStats() {

        totalAppointmentsLabel.setText(
                "Total Appointments: "
                        + service.getTotalAppointments());

        bookedLabel.setText(
                "Active Bookings: "
                        + service.getActiveBookings());

        cancelledLabel.setText(
                "Cancelled Appointments: "
                        + service.getCancelledAppointments());

        waitlistLabel.setText(
                "Patients on Waitlist: "
                        + service.getWaitlistCount());

        busiestDoctorLabel.setText(
                "Busiest Doctor: "
                        + service.getBusiestDoctor());
    }
}