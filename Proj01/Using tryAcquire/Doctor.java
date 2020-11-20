public class Doctor extends Thread {
    private final Message msg;

    public Doctor(Message msg) {
        this.msg = msg;
        this.start();
    }

    public void run() {
        while(true) {
            try {
                Hospital.patients.acquire(); // acquires a patient if one is waiting. if not, waits
                Hospital.doctors.release();
                this.seePatient();
            } catch (InterruptedException ex) {}
        }
    }

    public void seePatient() throws InterruptedException {
        int t1 = Hospital.clk.getTime();
        do {
            synchronized (msg) {
                msg.wait();
            }
        } while (Hospital.clk.getTime() < t1 + 2);
    }
}