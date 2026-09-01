package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseManager;

public class AppointmentService {

    public synchronized boolean bookAppointment(
            String patientName,
            String doctorName,
            String date,
            String timeSlot) {

        return bookAppointment(patientName, doctorName, date, timeSlot, 1);
    }

    public synchronized boolean bookAppointment(
            String patientName,
            String doctorName,
            String date,
            String timeSlot,
            int priority) {

        if (patientName == null || patientName.trim().isEmpty()) {
            System.out.println("Patient name cannot be empty.");
            return false;
        }

        if (doctorName == null || doctorName.trim().isEmpty()) {
            System.out.println("Doctor name cannot be empty.");
            return false;
        }

        if (date == null || date.trim().isEmpty()) {
            System.out.println("Date cannot be empty.");
            return false;
        }

        if (timeSlot == null || timeSlot.trim().isEmpty()) {
            System.out.println("Time slot cannot be empty.");
            return false;
        }

        try (Connection conn = DatabaseManager.connect()) {

            String checkQuery =
                    "SELECT * FROM appointments " +
                    "WHERE doctor_name=? AND date=? AND time_slot=? AND status='BOOKED'";

            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, doctorName);
                checkStmt.setString(2, date);
                checkStmt.setString(3, timeSlot);

                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Slot already booked.");
                        addToWaitlist(conn, patientName, doctorName, date, timeSlot, priority);
                        return false;
                    }
                }
            }

            insertAppointment(conn, patientName, doctorName, date, timeSlot, "BOOKED");

            System.out.println("Appointment booked successfully.");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void insertAppointment(
            Connection conn,
            String patientName,
            String doctorName,
            String date,
            String timeSlot,
            String status) throws SQLException {

        String insertQuery =
                "INSERT INTO appointments (patient_name, doctor_name, date, time_slot, status) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.setString(1, patientName);
            stmt.setString(2, doctorName);
            stmt.setString(3, date);
            stmt.setString(4, timeSlot);
            stmt.setString(5, status);
            stmt.executeUpdate();
        }
    }

    private void addToWaitlist(
            Connection conn,
            String patientName,
            String doctorName,
            String date,
            String timeSlot,
            int priority) throws SQLException {

        String query =
                "INSERT INTO waitlist (patient_name, doctor_name, date, time_slot, priority, request_time) " +
                "VALUES (?, ?, ?, ?, ?, datetime('now'))";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, patientName);
            stmt.setString(2, doctorName);
            stmt.setString(3, date);
            stmt.setString(4, timeSlot);
            stmt.setInt(5, priority);
            stmt.executeUpdate();
        }

        System.out.println("Patient added to waitlist with priority " + priority + ".");
    }

    public List<String[]> getAllAppointments() {

        List<String[]> appointments = new ArrayList<>();

        String query =
                "SELECT id, patient_name, doctor_name, date, time_slot, status " +
                "FROM appointments";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                appointments.add(new String[] {
                        String.valueOf(rs.getInt("id")),
                        rs.getString("patient_name"),
                        rs.getString("doctor_name"),
                        rs.getString("date"),
                        rs.getString("time_slot"),
                        rs.getString("status")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointments;
    }

    public List<String[]> getDoctorQueue() {

        List<String[]> appointments = new ArrayList<>();

        String query =
                "SELECT id, patient_name, doctor_name, date, time_slot, status " +
                "FROM appointments " +
                "WHERE status='BOOKED' OR status='BOOKED_FROM_WAITLIST' " +
                "ORDER BY date, time_slot";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                appointments.add(new String[] {
                        String.valueOf(rs.getInt("id")),
                        rs.getString("patient_name"),
                        rs.getString("doctor_name"),
                        rs.getString("date"),
                        rs.getString("time_slot"),
                        rs.getString("status")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointments;
    }

    public synchronized boolean cancelAppointment(int appointmentId) {

        try (Connection conn = DatabaseManager.connect()) {

            String getQuery =
                    "SELECT doctor_name, date, time_slot FROM appointments WHERE id=?";

            String doctorName = null;
            String date = null;
            String timeSlot = null;

            try (PreparedStatement getStmt = conn.prepareStatement(getQuery)) {
                getStmt.setInt(1, appointmentId);

                try (ResultSet rs = getStmt.executeQuery()) {
                    if (rs.next()) {
                        doctorName = rs.getString("doctor_name");
                        date = rs.getString("date");
                        timeSlot = rs.getString("time_slot");
                    } else {
                        return false;
                    }
                }
            }

            String cancelQuery =
                    "UPDATE appointments SET status='CANCELLED' WHERE id=?";

            try (PreparedStatement cancelStmt = conn.prepareStatement(cancelQuery)) {
                cancelStmt.setInt(1, appointmentId);
                cancelStmt.executeUpdate();
            }

            promoteFromWaitlist(conn, doctorName, date, timeSlot);

            System.out.println("Appointment cancelled.");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void promoteFromWaitlist(
            Connection conn,
            String doctorName,
            String date,
            String timeSlot) throws SQLException {

        String selectQuery =
                "SELECT id, patient_name FROM waitlist " +
                "WHERE doctor_name=? AND date=? AND time_slot=? " +
                "ORDER BY priority DESC, request_time ASC LIMIT 1";

        int waitlistId = -1;
        String patientName = null;

        try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
            selectStmt.setString(1, doctorName);
            selectStmt.setString(2, date);
            selectStmt.setString(3, timeSlot);

            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    waitlistId = rs.getInt("id");
                    patientName = rs.getString("patient_name");
                } else {
                    return;
                }
            }
        }

        insertAppointment(
                conn,
                patientName,
                doctorName,
                date,
                timeSlot,
                "BOOKED_FROM_WAITLIST");

        String deleteQuery =
                "DELETE FROM waitlist WHERE id=?";

        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
            deleteStmt.setInt(1, waitlistId);
            deleteStmt.executeUpdate();
        }

        System.out.println("Waitlisted patient promoted.");
    }
    
    public List<String[]> getWaitlist() {

        List<String[]> waitlist = new ArrayList<>();

        String query =
                "SELECT id, patient_name, doctor_name, date, time_slot, priority, request_time " +
                "FROM waitlist " +
                "ORDER BY priority DESC, request_time ASC";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                String priorityText;

                int priority = rs.getInt("priority");

                if (priority == 3) {
                    priorityText = "Emergency";
                } else if (priority == 2) {
                    priorityText = "Urgent";
                } else {
                    priorityText = "Regular";
                }

                waitlist.add(new String[] {
                        String.valueOf(rs.getInt("id")),
                        rs.getString("patient_name"),
                        rs.getString("doctor_name"),
                        rs.getString("date"),
                        rs.getString("time_slot"),
                        priorityText,
                        rs.getString("request_time")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return waitlist;
    }
    
    public void clearAllData() {

        String deleteAppointments =
                "DELETE FROM appointments";

        String deleteWaitlist =
                "DELETE FROM waitlist";

        try (Connection conn = DatabaseManager.connect()) {

            PreparedStatement stmt1 =
                    conn.prepareStatement(deleteAppointments);

            stmt1.executeUpdate();

            PreparedStatement stmt2 =
                    conn.prepareStatement(deleteWaitlist);

            stmt2.executeUpdate();

            System.out.println("All data cleared.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public boolean deleteAppointment(int appointmentId) {

        String query =
                "DELETE FROM appointments WHERE id=?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, appointmentId);

            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    
    public int getTotalAppointments() {
        return getCount("SELECT COUNT(*) FROM appointments");
    }

    public int getActiveBookings() {
        return getCount(
                "SELECT COUNT(*) FROM appointments " +
                "WHERE status='BOOKED' OR status='BOOKED_FROM_WAITLIST'");
    }

    public int getCancelledAppointments() {
        return getCount(
                "SELECT COUNT(*) FROM appointments " +
                "WHERE status='CANCELLED'");
    }

    public int getWaitlistCount() {
        return getCount("SELECT COUNT(*) FROM waitlist");
    }

    private int getCount(String query) {

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
    public String getBusiestDoctor() {

        String query =
                "SELECT doctor_name, COUNT(*) AS total " +
                "FROM appointments " +
                "WHERE status='BOOKED' OR status='BOOKED_FROM_WAITLIST' " +
                "GROUP BY doctor_name " +
                "ORDER BY total DESC " +
                "LIMIT 1";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getString("doctor_name") + " (" + rs.getInt("total") + " appointments)";
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "No active appointments";
    }
    public List<String[]> getAppointmentsByPatient(String patientName) {

        List<String[]> appointments = new ArrayList<>();

        String query =
                "SELECT id, patient_name, doctor_name, date, time_slot, status " +
                "FROM appointments " +
                "WHERE LOWER(patient_name)=LOWER(?) " +
                "ORDER BY date, time_slot";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, patientName);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                appointments.add(new String[] {
                        String.valueOf(rs.getInt("id")),
                        rs.getString("patient_name"),
                        rs.getString("doctor_name"),
                        rs.getString("date"),
                        rs.getString("time_slot"),
                        rs.getString("status")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointments;
    }
    public boolean checkInPatient(int appointmentId) {

        String query =
                "UPDATE appointments " +
                "SET status='WAITING' " +
                "WHERE id=?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, appointmentId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean callNextPatient(int appointmentId) {

        String query =
                "UPDATE appointments " +
                "SET status='NOW_SERVING' " +
                "WHERE id=?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, appointmentId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean completeAppointment(int appointmentId) {

        String query =
                "UPDATE appointments SET status='COMPLETED' WHERE id=?";

        try (java.sql.Connection conn =
                     database.DatabaseManager.connect();
             java.sql.PreparedStatement stmt =
                     conn.prepareStatement(query)) {

            stmt.setInt(1, appointmentId);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    public java.util.List<String[]> getDoctorAppointments(
            String doctorName) {

        java.util.List<String[]> appointments =
                new java.util.ArrayList<>();

        String query =
                "SELECT * FROM appointments " +
                "WHERE doctor_name=? " +
                "AND (status='BOOKED' " +
                "OR status='BOOKED_FROM_WAITLIST') " +
                "ORDER BY date, time_slot";

        try (
                java.sql.Connection conn =
                        database.DatabaseManager.connect();

                java.sql.PreparedStatement stmt =
                        conn.prepareStatement(query)
        ) {

            stmt.setString(1, doctorName);

            java.sql.ResultSet rs =
                    stmt.executeQuery();

            while (rs.next()) {

                appointments.add(new String[] {
                        String.valueOf(rs.getInt("id")),
                        rs.getString("patient_name"),
                        rs.getString("doctor_name"),
                        rs.getString("date"),
                        rs.getString("time_slot"),
                        rs.getString("status")
                });
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return appointments;
    }
}