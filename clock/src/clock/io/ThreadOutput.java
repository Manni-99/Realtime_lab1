package clock.io;

import java.util.concurrent.Semaphore;

public class ThreadOutput extends Thread {
    private Monitor monitor;
    private Semaphore mutex;
    

    public ThreadOutput(Monitor monitor, Semaphore mutex) {
        this.monitor = monitor;
        this.mutex = mutex;
    }

    @Override
    public void run() {
        try{
            mutex.acquire();
            monitor.displayTime(monitor.getHours(), monitor.getMinutes(), monitor.getSeconds());
            mutex.release();
        } catch(InterruptedException e){
            Thread.currentThread().interrupt();
        }
        
    }
    
    
    
}

