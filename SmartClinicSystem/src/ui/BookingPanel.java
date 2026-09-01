package ui;

import service.AppointmentService;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import service.UserService;

public class BookingPanel extends JPanel {

    private JTextField patientField;

    private JComboBox<String> doctorBox;
    private JComboBox<String> timeBox;
    private JComboBox<String> priorityBox;

    private JDateChooser dateChooser;

    private AppointmentService service;

    public BookingPanel() {

        service = new AppointmentService();

        setLayout(new GridBagLayout());

        setBackground(UITheme.BG);

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(10, 10, 10, 10);

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        JLabel title =
                new JLabel("Book Appointment");

        UITheme.styleTitle(title);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        add(title, gbc);

        gbc.gridwidth = 1;

        patientField = new JTextField();
        UITheme.styleTextField(patientField);

        UserService userService =
                new UserService();

        java.util.List<String> doctors =
                userService.getDoctors();

        doctorBox =
                new JComboBox<>(
                        doctors.toArray(new String[0]));

        timeBox = new JComboBox<>(new String[] {
                "9:00 AM",
                "10:00 AM",
                "11:00 AM",
                "12:00 PM",
                "1:00 PM",
                "2:00 PM",
                "3:00 PM"
        });

        priorityBox = new JComboBox<>(new String[] {
                "Regular",
                "Urgent",
                "Emergency"
        });

        dateChooser = new JDateChooser();

        dateChooser.setDateFormatString("yyyy-MM-dd");

        dateChooser.setMinSelectableDate(new Date());

        JButton bookButton =
                new JButton("Book Appointment");

        UITheme.styleButton(bookButton);

        addRow(gbc, 1, "Patient Name:", patientField);
        addRow(gbc, 2, "Doctor:", doctorBox);
        addRow(gbc, 3, "Date:", dateChooser);
        addRow(gbc, 4, "Time Slot:", timeBox);
        addRow(gbc, 5, "Priority:", priorityBox);

        gbc.gridx = 1;
        gbc.gridy = 6;

        add(bookButton, gbc);

        bookButton.addActionListener(
                e -> bookAppointment());
    }

    private void addRow(
            GridBagConstraints gbc,
            int row,
            String labelText,
            Component field) {

        gbc.gridx = 0;
        gbc.gridy = row;

        JLabel label =
                new JLabel(labelText);

        UITheme.styleLabel(label);

        add(label, gbc);

        gbc.gridx = 1;

        add(field, gbc);
    }

    private void bookAppointment() {

        String patient =
                patientField.getText().trim();

        String doctor =
                (String) doctorBox.getSelectedItem();

        String time =
                (String) timeBox.getSelectedItem();

        if (patient.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter patient name.");

            return;
        }

        Date selectedDate =
                dateChooser.getDate();

        if (selectedDate == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a valid date.");

            return;
        }

        java.time.LocalDate today =
                java.time.LocalDate.now();

        java.time.LocalDate chosenDate =
                selectedDate.toInstant()
                        .atZone(
                                java.time.ZoneId.systemDefault())
                        .toLocalDate();

        int currentYear =
                today.getYear();

        if (chosenDate.getYear() < currentYear ||
                chosenDate.getYear() > currentYear + 2) {

            JOptionPane.showMessageDialog(
                    this,
                    "Year must be between "
                            + currentYear
                            + " and "
                            + (currentYear + 2));

            return;
        }

        if (chosenDate.isBefore(today)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Cannot book past dates.");

            return;
        }

        SimpleDateFormat sdf =
                new SimpleDateFormat("yyyy-MM-dd");

        String date =
                sdf.format(selectedDate);

        String priorityText =
                (String) priorityBox.getSelectedItem();

        int priority = 1;

        if ("Urgent".equals(priorityText)) {
            priority = 2;
        } else if ("Emergency".equals(priorityText)) {
            priority = 3;
        }

        boolean success =
                service.bookAppointment(
                        patient,
                        doctor,
                        date,
                        time,
                        priority);

        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Appointment booked successfully.");

            patientField.setText("");

            dateChooser.setDate(null);

            priorityBox.setSelectedIndex(0);

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Slot unavailable.\nPatient added to waitlist.");
        }
    }
}