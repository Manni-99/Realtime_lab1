package lift;

import java.util.Arrays;

public class Monitor {
    private int[] toEnter; // number of passengers waiting to enter the lift at each floor
    private int[] toExit; // number of passengers (in lift) waiting to exit at each floor
    private int currentFloor;
    private int nbrOfPassengers;   
    boolean isLiftMoving;
    boolean liftMovingUp;   //true lift is going up, false lift is moving down
    private LiftView view; 

    private boolean doorOpen;
    private int passengersEntering;
    private int passengersExiting;

    private final int NBR_FLOORS;
    private final int MAX_PASSENGERS;

    public Monitor(LiftView view, int nbrFloors, int maxPassengers){
        this.currentFloor = 0;
        this.doorOpen = false;
        this.isLiftMoving = false;
        this.NBR_FLOORS =nbrFloors;
        this.MAX_PASSENGERS = maxPassengers;
        this.toEnter = new int[nbrFloors];
        this.toExit = new int[nbrFloors];
        this.nbrOfPassengers = 0;
        this.view = view;
        this.passengersEntering = 0;
        this.passengersExiting = 0;
    }

    public synchronized void increaseWaitingEntry(int passengerFloor){
        toEnter[passengerFloor]++;
        notifyAll();
    }

    public synchronized void enterLift(Passenger pass){
        int passengerFloor = pass.getStartFloor();
        while(currentFloor != passengerFloor || nbrOfPassengers >= MAX_PASSENGERS 
            || isLiftMoving || passengersEntering + nbrOfPassengers >= MAX_PASSENGERS || !doorOpen){
                try {
                    wait();
                } catch(InterruptedException e){
                    throw new Error("Enter lift interupted" + e);
                }
            }
            toEnter[currentFloor]--;
            toExit[pass.getDestinationFloor()]++;
            nbrOfPassengers++;
            passengersEntering++;
         //   notifyAll();
    }

    public synchronized void enterCompleted(){
        passengersEntering--;
        notifyAll();
    }

    public synchronized void exitLift(Passenger pass){
        int passDestFloor = pass.getDestinationFloor();
        while(currentFloor != passDestFloor || !doorOpen){
            try{
                wait();
            } catch(InterruptedException e){
                throw new Error("Exit lift interupted" + e);
            }
        }
        toExit[passDestFloor]--;
        nbrOfPassengers--;
        passengersExiting++;
        notifyAll();
    }

    public synchronized void exitCompleted(Passenger pass){
        passengersExiting--;
        notifyAll();
    }

    public synchronized int[] moveLift(){
        //Wait until there are any passengers waiting to enter or exit
        while(Arrays.stream(toEnter).sum() == 0 && Arrays.stream(toExit).sum() == 0){
            try{
                wait();
            } catch(InterruptedException e){
                throw new Error("move lift interupted" + e);
            }
        }
        //If there are passengers exiting / entering on current floor -> open doors
        if(toExit[currentFloor] > 0 || (toEnter[currentFloor] > 0 && nbrOfPassengers < MAX_PASSENGERS) && !doorOpen){
            view.openDoors(currentFloor);
            doorOpen = true;
            notifyAll();
        }
        //Wait while passengers are entering or exiting
        while(passengersEntering > 0 || 
            passengersExiting > 0 ||
            (doorOpen && (toExit[currentFloor] > 0 ||
            (toEnter[currentFloor] > 0 && nbrOfPassengers < MAX_PASSENGERS)))){
                try{
                    wait();
                } catch (InterruptedException e){
                    throw new Error("Lift continue entering / exiting was interupted " + e);
                }
            }
        //Close doors if they are open
        if(doorOpen){
            doorOpen = false;
            view.closeDoors();
        }

        //If there are no passengers waiting to enter or exit on current floor, stand still and run moveLift again
        if(Arrays.stream(toEnter).sum() == 0 && Arrays.stream(toExit).sum() == 0){
            return moveLift();
        }

        //Move the lift
        isLiftMoving = true;
        calculateDirection();
        int[] movingPositions = new int[2];
        movingPositions[1] = currentFloor + (liftMovingUp ? 1: -1);
        return movingPositions;
    }

    public synchronized void arrived(){
        isLiftMoving = false;
        notifyAll();
    }
    
    public synchronized void incrementFloor(){
        currentFloor = currentFloor + (liftMovingUp ? 1: -1);
    }

    //Calculate the direction of the lift
    private void calculateDirection(){
        if(Arrays.stream(toEnter, currentFloor, NBR_FLOORS).sum() == 0 
        && Arrays.stream(toExit, currentFloor, NBR_FLOORS).sum() == 0 && currentFloor != 0){
            liftMovingUp = false;
        } else if(Arrays.stream(toEnter, 0, currentFloor +1).sum() == 0
        && Arrays.stream(toExit, 0, currentFloor +1).sum() == 0 && currentFloor != NBR_FLOORS -1){
            liftMovingUp = true;
        } else if(currentFloor == NBR_FLOORS -1){
            liftMovingUp = false;
        } else if(currentFloor == 0){
            liftMovingUp = true;
        }
    }


}
