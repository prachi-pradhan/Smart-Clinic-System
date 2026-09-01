package service;

import database.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    public String login(String username, String password) {
        String query = "SELECT role FROM users WHERE username=? AND password=?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("role");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean createUser(String username, String password, String role) {
        String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            return false;
        }
    }

    public boolean deleteUser(int userId) {
        String query = "DELETE FROM users WHERE id=? AND username!='admin'";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<String[]> getAllUsers() {
        List<String[]> users = new ArrayList<>();

        String query = "SELECT id, username, role FROM users ORDER BY role, username";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(new String[] {
                        String.valueOf(rs.getInt("id")),
                        rs.getString("username"),
                        rs.getString("role")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public java.util.List<String> getDoctors() {

        java.util.List<String> doctors =
                new java.util.ArrayList<>();

        String query =
                "SELECT username FROM users WHERE role='Doctor'";

        try (
                java.sql.Connection conn =
                        database.DatabaseManager.connect();

                java.sql.PreparedStatement stmt =
                        conn.prepareStatement(query);

                java.sql.ResultSet rs =
                        stmt.executeQuery()
        ) {

            while (rs.next()) {

                String doctor =
                        rs.getString("username");

                if (!doctor.startsWith("Dr.")) {
                    doctor = "Dr. " + doctor;
                }

                doctors.add(doctor);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return doctors;
    }
}