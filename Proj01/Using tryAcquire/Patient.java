public class Patient extends Thread {
    private final int num; // patient number
    private final long in; // patient's arrival time
    private boolean wentIn = false;
    private final Message msg;

    public Patient(int num, long in, Message msg) {
        this.num = num;
        this.in = in;
        this.msg = msg;
        this.start();
    }

    public void run() {
        if (Hospital.clk.getTime() < in) {
            do {
                synchronized (msg) {
                    try {
                        msg.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } while (Hospital.clk.getTime() < in);
        }

        try {
            Hospital.accessSeats.acquire();
            if(Hospital.freeSeats > 0) {
                Hospital.patients.release();  //notify the doctor that there is a patient
                sleep(1);
                if(Hospital.freeSeats == Hospital.SEATS) //check if a doctor is available so they don't sit down
                    wentIn = Hospital.doctors.tryAcquire();
                //System.out.println(num + " went in " + wentIn + " at " + Hospital.clk.getTime());

                if(wentIn) // patient immediately went in
                {
                    Hospital.accessSeats.release();
                    this.seeDoctor();
                }
                else {
                    Hospital.freeSeats--;
                    Hospital.accessSeats.release();
                    try {
                        Hospital.doctors.acquire();  // wait til doctor isn't busy
                        Hospital.accessSeats.acquire(); // a patient walks into the office from the waiting room
                        Hospital.freeSeats++;
                        Hospital.accessSeats.release();
                        wentIn = true;
                        this.seeDoctor();
                    } catch (InterruptedException ex) {}
                }
            }
            else {  // there are no free seats
                Hospital.accessSeats.release();
                System.out.println("There are no free seats. Patient " + this.num + " has left at " + Hospital.clk.getTime());
            }
        }
        catch (InterruptedException ex) {}
    }


    public void seeDoctor() throws InterruptedException {
        int t1 = Hospital.clk.getTime();
        do {
            synchronized (msg) {
                msg.wait();
            }
        } while (Hospital.clk.getTime() < t1 + 2);
        System.out.println("Patient " + this.num + " is leaving at " + Hospital.clk.getTime());
    }
}
