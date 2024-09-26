package lift;

public class PassengerThread extends Thread {
    private Monitor lift;
    private LiftView view;

    public PassengerThread(Monitor lift, LiftView view) {
        this.lift = lift;
        this.view = view;
    }

    @Override
    public void run() {
        while (true) {
            Passenger pass = view.createPassenger(); // We create a new passenger for each thread
            pass.begin(); // Passenger starts walking

            lift.increaseWaitingEntry(pass.getStartFloor());

            lift.enterLift(pass);
            pass.enterLift();
            lift.enterCompleted();

            lift.exitLift(pass);
            pass.exitLift();
            lift.exitCompleted(pass);

            pass.end();
        }
    }
}
