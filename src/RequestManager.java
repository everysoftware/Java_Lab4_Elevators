import java.util.ArrayList;
import java.util.List;

public class RequestManager implements Runnable {
    private static final RequestManager manager = new RequestManager();
    public final List<Request> requests = new ArrayList<>();
    private final Elevator firstElevator = new Elevator(1);
    private final Elevator secondElevator = new Elevator(2);
    private final Thread firstElevatorThread = new Thread(firstElevator);
    private final Thread secondElevatorThread = new Thread(secondElevator);
    public boolean isRunning = true;
    public static RequestManager getManager() {
        return manager;
    }
    @Override
    public void run() {
        System.out.println("Thread: manager");
        try {
            firstElevatorThread.start();
            secondElevatorThread.start();

            firstElevatorThread.join();
            secondElevatorThread.join();
        } catch (IllegalStateException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    public synchronized void enqueueRequest(Request request) {
        requests.add(request);
    }
    public synchronized boolean isEmpty() {
        return requests.isEmpty();
    }
    public synchronized Request dequeueRequest() {
        if (isEmpty()) {
            return null;
        }
        Request request = requests.get(0);
        requests.remove(0);
        return request;
    }
    public synchronized Request getFirst() {
        return isEmpty() ? null : requests.get(0);
    }
    public synchronized int getElevator(Request request) {
        int elevatorId = Math.abs(firstElevator.currentFloor - request.callingFloor) <
                Math.abs(secondElevator.currentFloor - request.callingFloor) ? 1 : 2;
        if (elevatorId == 1) {
            firstElevator.callingFloor = request.callingFloor;
            firstElevator.targetFloor = request.targetFloor;
        } else {
            secondElevator.callingFloor = request.callingFloor;
            secondElevator.targetFloor = request.targetFloor;
        }
        return elevatorId;
    }
}
