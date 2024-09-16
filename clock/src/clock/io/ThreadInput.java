package clock.io;

import java.util.NoSuchElementException;
import java.util.concurrent.Semaphore;

import clock.io.ClockInput.UserInput;

public class ThreadInput extends Thread {
    private Monitor monitor;
    private ClockInput in;
    private Semaphore mutex;

   
    public ThreadInput(Monitor monitor, ClockInput in ,Semaphore mutex) {
        this.monitor = monitor;
        this.in = in;
        this.mutex = mutex;
    }

    @Override
    public void run() {
        while (true) {
            try {
                System.out.println("Thread Input : Catch Error!");
                mutex.acquire();
                System.out.println("After mutex acquire");
                // Get user input (assuming this method does not throw InterruptedException)
                UserInput userInput = in.getUserInput(); 

                if (userInput != null) {
                    // Signal the main thread that input is available
                    Choice choice = userInput.choice();
                    int hours = userInput.hours();
                    int mins = userInput.minutes();
                    int secs = userInput.seconds();
                    System.out.println("We are are");
                    switch (choice) {
                        case SET_TIME:
                            monitor.setTime(hours, mins, secs);  // Pass hours, mins, and secs to set time
                            break;
                        case SET_ALARM:
                            monitor.setAlarm(hours, mins, secs); // Pass hours, mins, and secs to set alarm
                            break;
                        default:
                            break; // Handle default or toggle alarm here if needed
                    }
                    
                    // Signal the main thread that the input has been processed
                    mutex.release();
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();  // Reset interrupt status if the thread is interrupted
            }
        }
    }
}

/*
 * 
 */