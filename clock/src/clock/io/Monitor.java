package clock.io;

import java.util.concurrent.Semaphore;

public class Monitor {
    private int hours, mins, secs; 
    private int alarmHours, alarmMins, alarmSecs;
    private boolean alarmSet = false;
    private boolean alarmTriggered = false;

    private Semaphore mutex = new Semaphore(1);
    //private Semaphore available = new Semaphore(1);
    //private Semaphore busy = new Semaphore(1);

    private ClockOutput out;

    public Monitor(ClockOutput out){
        // Start ticking thread 
        this.out = out;
        }
    

    public void setTime(int hours, int mins, int secs){
        try
        {
            mutex.acquire();
            this.hours = hours;
            this.mins = mins;
            this.secs = secs;
            System.out.println("Time set to: " + this.hours + ":" + this.mins + ":" + this.secs);
        } 
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }  finally{mutex.release();}
    }

    // Set the alarm time
    public void setAlarm(int alarmHours, int alarmMins, int alarmSecs) {
        try 
        {
            mutex.acquire(); // Acquire lock before setting alarm
            this.alarmHours = alarmHours;
            this.alarmMins = alarmMins;
            this.alarmSecs = alarmSecs;
            this.alarmSet = true;
            System.out.println("Alarm set for: " + alarmHours + ":" + alarmMins + ":" + alarmSecs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally{mutex.release();}
    }


    public void displayTime() {
        try 
        {
            long startTime = System.currentTimeMillis();
            while (true) {
                mutex.acquire();
                secs++;
            if (secs == 60) {
                secs = 0;
                mins++;
                if (mins == 60) {
                    mins = 0;
                    hours++;
                    if (hours == 24) {
                        hours = 0;
                    }
                }
            }

            if (alarmSet && hours == alarmHours && mins == alarmMins && secs == alarmSecs) {
                alarmTriggered = true;
                out.setAlarmIndicator(alarmTriggered);
                out.alarm();  // Trigger the alarm
            }

            startTime = 1000 + startTime;
            long sleepTime = startTime - System.currentTimeMillis();

            if(sleepTime < 0){
                sleepTime = 0;
            }
            mutex.release();
            Thread.sleep(sleepTime);

            out.displayTime(hours, mins, secs);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
}
