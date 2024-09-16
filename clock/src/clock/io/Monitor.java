package clock.io;

import java.util.concurrent.Semaphore;

public class Monitor {
    private int hours, mins, secs; 
    private int alarmHours, alarmMins, alarmSecs;
    private boolean alarmSet = false;
    private boolean alarmTriggered = false;

    private Semaphore mutex = new Semaphore(1);
    private Semaphore available = new Semaphore(1);
    private Semaphore busy = new Semaphore(0);

    private ClockOutput out;
    private ClockInput in;

    public Monitor(ClockOutput out, ClockInput in){
        // Start ticking thread 
        this.out = out;
        this.in = in;
        }
    

    public void setTime(int hours, int mins, int secs){
        try
        {
            available.acquire();
            this.hours = hours;
            this.mins = mins;
            this.secs = secs;
           // out.displayTime(hours, mins, secs);
            System.out.println("Time set to: " + this.hours + ":" + this.mins + ":" + this.secs);
            busy.release();
        } 
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }

    // Set the alarm time
    public void setAlarm(int alarmHours, int alarmMins, int alarmSecs) {
        try 
        {
            available.acquire(); // Acquire lock before setting alarm
            this.alarmHours = alarmHours;
            this.alarmMins = alarmMins;
            this.alarmSecs = alarmSecs;
            this.alarmSet = true;
            System.out.println("Alarm set for: " + alarmHours + ":" + alarmMins + ":" + alarmSecs);
            busy.release();  // Notify that alarm is set
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    public void displayTime(int hours, int mins, int secs) {
        this.hours = hours;
        this.mins = mins;
        this.secs = secs;
        try 
        {
            while (true) {
                
                long startTime = System.currentTimeMillis();
                System.out.println("Time is ticking");
        
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

            long elapsedTime = System.currentTimeMillis() - startTime;
            long sleepTime = 1000 - elapsedTime;

            if(sleepTime < 0){
                sleepTime = 0;
            }
            Thread.sleep(sleepTime);
                // Print current time for debugging
               // available.acquire();
                System.out.println("Time is " + hours + ":" + mins + ":" + secs);
                out.displayTime(hours, mins, secs);
               // available.release();
                System.out.println("We are here after available.release()");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private void tickTime(){
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
    }

    // Check if the current time matches the alarm
    private void checkAlarm() {
        if (alarmSet && hours == alarmHours && mins == alarmMins && secs == alarmSecs) {
            alarmTriggered = true;
            out.setAlarmIndicator(alarmTriggered);
            out.alarm();  // Trigger the alarm
        }
    }

    public int getHours(){
        return this.hours;
    }

    public int getMinutes(){
        return this.mins;
    }

    public int getSeconds(){
        return this.secs;
    }

}
