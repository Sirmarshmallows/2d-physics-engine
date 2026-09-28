
//updates a physics environment through time
abstract public class PhysicsLoop extends Thread{
    private boolean running = true;
    private int targetFPS;

    public PhysicsLoop(int targetFPS) {
        this.targetFPS = targetFPS;
    }
    //main body of the physics loop
    public void run() {
        double dt = 0;
        while (running) {
            long startTime = System.nanoTime();
            //update the simulation
            update(dt);
            //have the thread sleep if the simulation is running too fast
            long endTime = System.nanoTime();
            long sleepTimeMilli = 1000L / targetFPS - (endTime - startTime) / 1000000L;
            if (sleepTimeMilli > 0) {
                try {
                    sleep(sleepTimeMilli);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            endTime = System.nanoTime();
            dt = (endTime - startTime) / 1000000000.0;
        }
    }

    //toggles the simulation on and off
    public void pause() {
        running = false;
    }

    abstract public void update(double dt);
}
