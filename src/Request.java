public class Request {
    public int id;
    public int callingFloor;
    public int targetFloor;
    public ElevatorStatus direction;
    public Request(int id, int currentFloor, int targetFloor) {
        this.id = id;
        this.callingFloor = currentFloor;
        this.targetFloor = targetFloor;
        this.direction = targetFloor > currentFloor ?
                ElevatorStatus.MOVING_UP : ElevatorStatus.MOVING_DOWN;
    }
}
