import java.util.concurrent.Semaphore;

import clock.AlarmClockEmulator;
import clock.io.Choice;
import clock.io.ClockInput;
import clock.io.ClockInput.UserInput;
import clock.io.ClockOutput;
import clock.io.Monitor;
import clock.io.ThreadInput;
import clock.io.ThreadOutput;


public class ClockMain {
    public static void main(String[] args) throws InterruptedException {
        AlarmClockEmulator emulator = new AlarmClockEmulator();

        ClockInput  in  = emulator.getInput();
        ClockOutput out = emulator.getOutput();

        out.displayTime(15, 2, 37);   // arbitrary time: just an example

        Semaphore mutex = new Semaphore(1);
        Semaphore inputSemaphore = in.getSemaphore();

        Monitor monitor = new Monitor(out, in);    // Initilize our monitor object

        //ThreadInput input = new ThreadInput(monitor, in ,mutex);   // Input thread
        ThreadOutput output = new ThreadOutput(monitor, mutex); // Output thread

        //input.start();
        output.start();

        while (true) { 
                   
           inputSemaphore.acquire();
            UserInput userInput = in.getUserInput();
            System.out.println("This is before userInput contains a value : " + userInput);
            if(userInput != null){
                System.out.println("User Input is: " + userInput);
                Choice choice = userInput.choice();
                    int hours = userInput.hours();
                    int mins = userInput.minutes();
                    int secs = userInput.seconds();
                    if(choice == Choice.SET_TIME){
                        monitor.setTime(hours, mins, secs);  // Pass hours, mins, and secs to set time
                    } else if(choice == Choice.SET_ALARM){
                        monitor.setAlarm(hours, mins, secs);  // Pass hours, mins, and secs to set time
                    }
            }
        
        }
    }
}
