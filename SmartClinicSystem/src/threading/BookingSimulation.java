package threading;

import service.AppointmentService;

public class BookingSimulation {

    public static void runSimulation() {

        AppointmentService service = new AppointmentService();

        Runnable patient1 = () -> {
            boolean result = service.bookAppointment(
                    "Thread Patient 1",
                    "Dr. Smith",
                    "2026-06-01",
                    "10:00 AM");

            System.out.println("Thread Patient 1 booking result: " + result);
        };

        Runnable patient2 = () -> {
            boolean result = service.bookAppointment(
                    "Thread Patient 2",
                    "Dr. Smith",
                    "2026-06-01",
                    "10:00 AM");

            System.out.println("Thread Patient 2 booking result: " + result);
        };

        Runnable patient3 = () -> {
            boolean result = service.bookAppointment(
                    "Thread Patient 3",
                    "Dr. Smith",
                    "2026-06-01",
                    "10:00 AM");

            System.out.println("Thread Patient 3 booking result: " + result);
        };

        Thread t1 = new Thread(patient1);
        Thread t2 = new Thread(patient2);
        Thread t3 = new Thread(patient3);

        t1.start();
        t2.start();
        t3.start();
    }
}