import java.text.MessageFormat;

public class Elevator implements Runnable {
    public final int id;
    public int currentFloor = 1;
    public int callingFloor;
    public int targetFloor;
    public Direction direction = Direction.IDLE;
    public final static Object obj = new Object();

    public Elevator(int id) {
        this.id = id;
    }
    public Elevator(int id, int currentFloor) {
        this(id);
        this.currentFloor = currentFloor;
    }
    public synchronized void move(int startFloor, int endFloor) {
        direction = startFloor < endFloor ? Direction.UP : Direction.DOWN;
        while (currentFloor != endFloor) {
            System.out.println(MessageFormat.format("Elevator #{0} is moving {1} to {2} floor (now on: {3})",
                    id, direction, endFloor, currentFloor));
            this.currentFloor += direction == Direction.UP ? 1 : -1;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(MessageFormat.format("Elevator #{0} has arrived to {1} floor", id, currentFloor));
    }
    @Override
    public void run() {
        System.out.println(MessageFormat.format("Thread: elevator #{0}", id));
        var manager = RequestManager.getManager();
        while (manager.isRunning || !manager.isEmpty()) {
            synchronized (RequestGenerator.obj) {
                while (manager.isRunning && manager.isEmpty()) {
                    System.out.println("Waiting for requests...");
                    try {
                        RequestGenerator.obj.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (manager.isEmpty() || manager.getElevator(manager.getFirst()) != id) {
                continue;
            }
            var request = manager.dequeueRequest();
            move(currentFloor, request.callingFloor);
            move(currentFloor, request.targetFloor);
            synchronized (obj) {
                if (!manager.isEmpty() && manager.getElevator(manager.getFirst()) == id) {
                    var nextRequest = manager.dequeueRequest();
                    move(currentFloor, nextRequest.callingFloor);
                    move(currentFloor, nextRequest.targetFloor);
                }
            }
        }
    }
}
