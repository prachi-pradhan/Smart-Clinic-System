package ui;

import javax.swing.*;
import java.awt.*;

public class MainDashboard extends JFrame {

    public MainDashboard(String role, String username) {

        setTitle("Smart Clinic Booking System");

        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());

        JLabel welcomeLabel =
                new JLabel("Welcome, " + username + " (" + role + ")");

        welcomeLabel.setBorder(
                BorderFactory.createEmptyBorder(10, 15, 10, 10));

        welcomeLabel.setFont(
                new Font("SansSerif", Font.BOLD, 16));

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());

        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();

        if (role.equalsIgnoreCase("Admin")) {

            tabs.add("Admin Users", new AdminPanel());
            tabs.add("Receptionist", new ReceptionistPanel());
            tabs.add("Doctor Queue", new DoctorQueuePanel());
            tabs.add("Waitlist", new WaitlistPanel());
            tabs.add("Statistics", new StatsPanel());

        } else if (role.equalsIgnoreCase("Receptionist")) {

            tabs.add("Booking", new BookingPanel());
            tabs.add("Receptionist", new ReceptionistPanel());
            tabs.add("Waitlist", new WaitlistPanel());
            tabs.add("Statistics", new StatsPanel());

        } else if (role.equalsIgnoreCase("Doctor")) {

            tabs.add("Doctor Queue", new DoctorQueuePanel());
            tabs.add("Statistics", new StatsPanel());

        } else if (role.equalsIgnoreCase("Patient")) {

            tabs.add("Book Appointment", new BookingPanel());
            tabs.add("My Bookings", new PatientBookingsPanel(username));
        }

        add(topPanel, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        setVisible(true);
    }

    private void logout() {

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame();
        }
    }
}