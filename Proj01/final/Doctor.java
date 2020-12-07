public class Doctor extends Thread {
    private final int num; //starting from 0
    private int curPatient = -1; // -1 when doc is waiting for patients, 0 when doc is ready to accept a patient, any higher number shows patient number

    public Doctor(int num) {
        this.num = num;
        this.start();
    }

    public void run() {
        while(true) {
            try {
                Hospital.patients.acquire(); // acquires a patient if one is waiting. if not, waits
                curPatient = 0;
                Hospital.patientInOffice[num] = 0;
                Hospital.doctors.release();

                Hospital.patientInOfficeSem[num].acquire(); //wait til patient has set the value
                curPatient = Hospital.patientInOffice[num];
                System.out.println("doctor " + num + " is seeing patient " + curPatient + " at " + Hospital.clk.getTime());
                Hospital.accessDocs.release();

                this.seePatient();

                Hospital.patientInOfficeSem[num].acquire();
                curPatient = Hospital.patientInOffice[num];
                System.out.println("doctor " + num + " is with " + curPatient + " at " + Hospital.clk.getTime());
                Hospital.accessDocs.release();
            } catch (InterruptedException ignored) {}
        }
    }

    public void seePatient() throws InterruptedException {
        Hospital.patientInOfficeSem[num].acquire();
    }
}