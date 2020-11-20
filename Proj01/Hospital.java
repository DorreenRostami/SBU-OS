//Dorreen Rostami - 97243034
import java.util.concurrent.*;

public class Hospital extends Thread {

    public static Semaphore patients = new Semaphore(0); //no patients first
    public static Semaphore doctors = new Semaphore(0); //doctors waiting til patient arrives
    public static Semaphore accessSeats = new Semaphore(1); //binary semaphore for when accessing seat count
    public static Semaphore accessDocs = new Semaphore(1); //binary semaphore for when accessing doctor count
    public static final int DOC_COUNT = 4; //total doctors
    public static int freeDocs = DOC_COUNT;
    public static final int SEAT_COUNT = 2; //total seats
    public static int freeSeats = SEAT_COUNT;
    public static Clock clk;

    public static void main(String[] args) {
        Hospital hos = new Hospital();
        hos.start();
    }

    public void run(){
        Message msg = new Message();
        Doctor[] docs = new Doctor[]{
                new Doctor(msg),
                new Doctor(msg),
                new Doctor(msg),
                new Doctor(msg)
        };
        clk = new Clock(msg);
        Patient[] pats = new Patient[]{
                new Patient(1, 1, msg),
                new Patient(2, 1, msg),
                new Patient(3, 2, msg),
                new Patient(4, 2, msg),
                new Patient(5, 2, msg),
                new Patient(6, 4, msg),
                new Patient(7, 5, msg),
                new Patient(8, 5, msg),
                new Patient(9, 5, msg),
                new Patient(10, 5, msg),
                new Patient(11, 6, msg),
                new Patient(12, 6, msg),
                new Patient(13, 6, msg),
                new Patient(14, 7, msg)
        };
    }
}