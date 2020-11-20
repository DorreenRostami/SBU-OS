public class Clock extends Thread {
    private static int time = 0;
    private final Message msg;

    public Clock(Message msg){
        this.msg = msg;
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
            synchronized (msg) {
                msg.notifyAll();
            }
        }
    }

    public int getTime(){
        return time;
    }
}
