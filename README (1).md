# Smart Clinic System

A desktop clinic management app built in Java (Swing) for the Intro to Java (CS6103) final project. It supports role-based logins for Admin, Receptionist, Doctor, and Patient, and models real clinic workflows: appointment booking with conflict detection, a priority waitlist that automatically promotes patients when a slot frees up, and a live doctor queue for check-in and calling patients.

## Features

- **Role-based login** — Admin, Receptionist, Doctor, and Patient roles, each routed to a different dashboard view after login.
- **Appointment booking with conflict detection** — booking a taken slot automatically places the patient on a priority waitlist instead of failing outright.
- **Automatic waitlist promotion** — when an appointment is cancelled, the highest-priority (then earliest-requested) waitlisted patient is automatically booked into the freed slot.
- **Doctor queue management** — check patients in, call the next patient, and mark appointments complete, with per-doctor filtered views.
- **Admin panel** — manage users and view clinic-wide stats (total appointments, active bookings, cancellations, waitlist size, busiest doctor).
- **Concurrency handling** — appointment booking is synchronized to prevent race conditions when multiple patients try to book the same slot simultaneously; includes a threading demo (`BookingSimulation`) that fires concurrent booking requests to demonstrate this.

## Tech Stack

- **Language:** Java (Swing for UI)
- **Database:** SQLite (via JDBC), initialized automatically on first run
- **Concurrency:** Java threads, synchronized methods for booking safety

## Project Structure

```
SmartClinicSystem/
└── src/
    ├── Main.java                    # Entry point — initializes DB, launches login screen
    ├── database/
    │   └── DatabaseManager.java     # SQLite connection + schema setup, seeds demo accounts
    ├── model/
    │   ├── Appointment.java
    │   └── WaitlistEntry.java
    ├── service/
    │   ├── AppointmentService.java  # Booking, cancellation, waitlist promotion, stats queries
    │   └── UserService.java         # Login, user management, doctor lookup
    ├── threading/
    │   └── BookingSimulation.java   # Demonstrates concurrent booking safety
    └── ui/
        ├── LoginFrame.java
        ├── MainDashboard.java
        ├── AdminPanel.java
        ├── ReceptionistPanel.java
        ├── BookingPanel.java
        ├── DoctorQueuePanel.java
        ├── PatientBookingsPanel.java
        ├── WaitlistPanel.java
        ├── StatsPanel.java
        └── UITheme.java
```

## Setup

1. Requires Java 17+ (uses text blocks) and the SQLite JDBC driver on the classpath.
2. Run `Main.java` — it creates `clinic.db` and seeds four demo accounts on first launch:
   - `admin` / `admin123` (Admin)
   - `receptionist` / `rec123` (Receptionist)
   - `doctor` / `doc123` (Doctor)
   - `patient` / `pat123` (Patient)
3. Log in with any of the above to explore the corresponding dashboard.

> These are seeded demo credentials for local testing only — not meant for a real deployment.

## Status

Built as a course final project to practice Java, JDBC/SQL, multithreading, and Swing UI design around a realistic scheduling domain.
