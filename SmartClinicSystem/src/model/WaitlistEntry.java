package model;

public class WaitlistEntry {
    private int id;
    private String patientName;
    private String doctorName;
    private String date;
    private String timeSlot;
    private int priority;
    private String requestTime;

    public WaitlistEntry(int id, String patientName, String doctorName, String date,
                         String timeSlot, int priority, String requestTime) {
        this.id = id;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.date = date;
        this.timeSlot = timeSlot;
        this.priority = priority;
        this.requestTime = requestTime;
    }

    public int getId() {
        return id;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getDate() {
        return date;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public int getPriority() {
        return priority;
    }

    public String getRequestTime() {
        return requestTime;
    }
}