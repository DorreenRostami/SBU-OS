//Dorreen Rostami - 97243034
//sent the project last week but it didn't handle the "patientInOffice" part
import java.util.concurrent.*;

public class Hospital extends Thread {

    public static Semaphore patients = new Semaphore(0); //no patients first
    public static Semaphore doctors = new Semaphore(0); //doctors waiting til patient arrives
    public static Semaphore accessSeats = new Semaphore(1); //binary semaphore for when accessing seat count
    public static Semaphore accessDocs = new Semaphore(1); //binary semaphore for when accessing doctor count and patientInOffice array

    public static final int DOC_COUNT = 4; //total doctors
    public static int freeDocs = DOC_COUNT;

    //patientInOffice holds the number of the patient in each office (-1 when doc is waiting and 0 when doc is ready to accept a patient)
    public static Integer[] patientInOffice = new Integer[DOC_COUNT];
    /* patientInOfficeSem is a binary semaphore for communication between patient and a certain doctor. Used after a
    patient has selected their office and wants no notify the doctor to set their current patient and after patient
    is done visiting the doctor's office. */
    public static Semaphore[] patientInOfficeSem = new Semaphore[DOC_COUNT];

    public static final int SEAT_COUNT = 2; //total seats
    public static int freeSeats = SEAT_COUNT;

    public static Clock clk;

    public static void main(String[] args) {
        Hospital hos = new Hospital();
        hos.start();
    }

    public void run(){
        for (int i = 0 ; i < DOC_COUNT ; i++){
            new Doctor(i);
            patientInOffice[i] = -1;
            patientInOfficeSem[i] = new Semaphore(0);
        }

        clk = new Clock();
        Patient[] pats = new Patient[]{
                new Patient(1, 1),
                new Patient(2, 1),
                new Patient(3, 2),
                new Patient(4, 2),
                new Patient(5, 2),
                new Patient(6, 4),
                new Patient(7, 5),
                new Patient(8, 5),
                new Patient(9, 5),
                new Patient(10, 5),
                new Patient(11, 6),
                new Patient(12, 6),
                new Patient(13, 6),
                new Patient(14, 7)
        };
    }
}