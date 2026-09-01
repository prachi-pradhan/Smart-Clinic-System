package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String URL = "jdbc:sqlite:clinic.db";

    public static Connection connect() {
        try {
            Class.forName("org.sqlite.JDBC"); // loads SQLite driver

            Connection conn = DriverManager.getConnection(URL);
            System.out.println("Connected to SQLite database.");
            return conn;

        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC driver not found.");
            e.printStackTrace();
            return null;

        } catch (SQLException e) {
            System.out.println("Connection failed.");
            e.printStackTrace();
            return null;
        }
    }
    
    public static void createTables() {
    	
    	String usersTable = """
    		    CREATE TABLE IF NOT EXISTS users (
    		        id INTEGER PRIMARY KEY AUTOINCREMENT,
    		        username TEXT UNIQUE NOT NULL,
    		        password TEXT NOT NULL,
    		        role TEXT NOT NULL
    		    );
    		""";

        String appointmentsTable = """
            CREATE TABLE IF NOT EXISTS appointments (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                patient_name TEXT NOT NULL,
                doctor_name TEXT NOT NULL,
                date TEXT NOT NULL,
                time_slot TEXT NOT NULL,
                status TEXT NOT NULL
            );
        """;

        String waitlistTable = """
            CREATE TABLE IF NOT EXISTS waitlist (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                patient_name TEXT NOT NULL,
                doctor_name TEXT NOT NULL,
                date TEXT NOT NULL,
                time_slot TEXT NOT NULL,
                priority INTEGER,
                request_time TEXT
            );
        """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(appointmentsTable);
            stmt.execute(waitlistTable);
            stmt.execute(usersTable);
            
            stmt.executeUpdate("INSERT OR IGNORE INTO users (username, password, role) VALUES ('receptionist', 'rec123', 'Receptionist')");
            stmt.executeUpdate("INSERT OR IGNORE INTO users (username, password, role) VALUES ('doctor', 'doc123', 'Doctor')");
            stmt.executeUpdate("INSERT OR IGNORE INTO users (username, password, role) VALUES ('patient', 'pat123', 'Patient')");
            stmt.executeUpdate("INSERT OR IGNORE INTO users " + "(username, password, role) " + "VALUES ('admin', 'admin123', 'Admin')");

            System.out.println("Tables created successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}