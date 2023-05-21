import java.util.ArrayList;
import java.util.List;

public class Elevator {
    public int id;
    public int capacity;
    public List<Request> passengers;
    public int currentFloor;
    public int targetFloor;
    public ElevatorStatus status;
    public Elevator(int id, int capacity, int currentFloor) {
        this.id = id;
        this.capacity = capacity;
        this.passengers = new ArrayList<>();
        this.currentFloor = currentFloor;
        this.status = ElevatorStatus.IDLE;
    }
    public void setStatusOnMoving() {
        this.status = this.targetFloor > this.currentFloor ? ElevatorStatus.MOVING_UP : ElevatorStatus.MOVING_DOWN;
    }
    public void stop() {
        this.status = ElevatorStatus.IDLE;
    }
}
