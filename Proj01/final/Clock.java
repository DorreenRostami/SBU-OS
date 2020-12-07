public class Clock extends Thread {
    private static int time = 0;

    public Clock(){
        this.start();
    }

    public void run(){
        while (true) {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            time++;
            synchronized (this) {
                this.notifyAll();
            }
        }
    }

    public int getTime(){
        return time;
    }
}
