package wash.control;

import javax.swing.SwingUtilities;

import actor.ActorThread;
import wash.io.WashingIO;
import wash.simulation.WashingSimulator;

public class Wash {

    private static ActorThread<WashingMessage> currentProgram;

    public static void main(String[] args) throws InterruptedException {
        
        WashingSimulator sim = new WashingSimulator(Settings.SPEEDUP);

        WashingIO io = sim.startSimulation();

        ActorThread<WashingMessage> temp = new TemperatureController(io);
        ActorThread<WashingMessage> water = new WaterController(io);
        ActorThread<WashingMessage> spin = new SpinController(io);
        temp.start();
        water.start();
        spin.start();

        while (true) {

            int n = io.awaitButton();
            System.out.println("user selected program " + n);

            switch (n) {
                case 1:
                        if(currentProgram != null){return;}
                        System.out.println("Starting Washing Program 1");
                        currentProgram = new WashingProgram1(io, temp, water, spin); // Assume WashingProgram2 is
                        currentProgram.start();
                    break;
                case 2:
                        if(currentProgram != null){return;}
                        System.out.println("Starting Washing Program 2");
                        currentProgram = new WashingProgram2(io, temp, water, spin); // Assume WashingProgram2 is
                        currentProgram.start();
                    break;
                case 3:
                        if(currentProgram != null){return;}
                        System.out.println("Starting Washing Program 3");
                        currentProgram = new WashingProgram3(io, temp, water, spin);
                        currentProgram.start();
                    break;
                case 0:
                    if (currentProgram != null) {
                        System.out.println("Stopping current program");
                        currentProgram.interrupt(); // or implement a stop method
                        currentProgram = null; // Reset the current program tracker
                    }
                    break;
                default:
                    break;
            }
        }
    }
};
