public class Patient extends Thread {
    private final int num; // patient number
    private final long in; // patient's arrival time
    private int office = -1; // number of the doctor's office the patient is in (-1 if not in any)

    public Patient(int num, long in) {
        this.num = num;
        this.in = in;
        this.start();
    }

    public void run() {
        if (Hospital.clk.getTime() < in) {
            do {
                synchronized (Hospital.clk) {
                    try {
                        Hospital.clk.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } while (Hospital.clk.getTime() < in);
        }

        try {
            Hospital.accessSeats.acquire();
            if(Hospital.freeSeats > 0) {
                Hospital.patients.release(); //notify the doctor that there is a patient

                Hospital.accessDocs.acquire();
                if(Hospital.freeSeats == Hospital.SEAT_COUNT && Hospital.freeDocs > 0) //check if a doctor is available so they don't sit down
                {
                    Hospital.doctors.acquire();
                    Hospital.accessSeats.release();
                    for (int i = 0; i < Hospital.DOC_COUNT ; i++)
                    {
                        if(Hospital.patientInOffice[i] == 0)
                        {
                            Hospital.patientInOffice[i] = num;
                            office = i;
                            break;
                        }
                    }
                    System.out.println("patient " + num + " went right into office " + office + " at " + Hospital.clk.getTime());
                    Hospital.freeDocs--;
                    //doctor with number "office" will perform Hospital.accessDocs.release();
                    Hospital.patientInOfficeSem[office].release();
                }
                else //they sit down
                {
                    System.out.println("patient " + num + " sat down at " + Hospital.clk.getTime());
                    Hospital.accessDocs.release(); //only time the patient releases, bc they don't have a doc to do so
                    Hospital.freeSeats--;
                    Hospital.accessSeats.release();

                    Hospital.doctors.acquire();  // wait til doctor isn't busy
                    Hospital.accessSeats.acquire();
                    Hospital.accessDocs.acquire();
                    // note that seats still arent empty so if another patient just arrives they'll sit down
                    for (int i = 0; i < Hospital.DOC_COUNT ; i++)
                    {
                        if(Hospital.patientInOffice[i] == 0)
                        {
                            Hospital.patientInOffice[i] = num;
                            office = i;
                            break;
                        }
                    }
                    System.out.println("patient " + num + " went into office " + office + " at " + Hospital.clk.getTime());
                    Hospital.freeDocs--;
                    //Hospital.accessDocs.release(); done by the doc
                    Hospital.patientInOfficeSem[office].release();
                    Hospital.freeSeats++;
                    Hospital.accessSeats.release();
                }
                this.seeDoctor();
                Hospital.accessDocs.acquire();
                Hospital.patientInOffice[office] = -1;
                Hospital.freeDocs++;
                //Hospital.accessDocs.release(); done by the doc
                Hospital.patientInOfficeSem[office].release();
                office = -1;
            }
            else // there are no free seats
            {
                Hospital.accessSeats.release();
                System.out.println("There are no free seats. Patient " + this.num + " has left at " + Hospital.clk.getTime());
            }
        }
        catch (InterruptedException ignored) {}
    }


    public void seeDoctor() throws InterruptedException {
        int t1 = Hospital.clk.getTime();
        do {
            synchronized (Hospital.clk) {
                Hospital.clk.wait();
            }
        } while (Hospital.clk.getTime() < t1 + 2);
        Hospital.patientInOfficeSem[office].release();
        System.out.println("Patient " + this.num + " is leaving office " + office + " at " + Hospital.clk.getTime());
    }
}
