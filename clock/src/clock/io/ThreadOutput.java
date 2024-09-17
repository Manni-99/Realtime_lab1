package clock.io;

public class ThreadOutput extends Thread {
    private Monitor monitor;
    

    public ThreadOutput(Monitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void run() {      
            monitor.displayTime();   
    }
    
    
    
}

