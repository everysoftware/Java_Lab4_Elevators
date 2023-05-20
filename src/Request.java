import java.text.MessageFormat;

public class Request {
    public final int callingFloor;
    public final int targetFloor;
    public final Direction direction;

    public Request(int currentFloor, int targetFloor) {
        this.callingFloor = currentFloor;
        this.targetFloor = targetFloor;
        direction = targetFloor > currentFloor ?
                Direction.UP : Direction.DOWN;
    }

    @Override
    public String toString() {
        return MessageFormat.format("Request {0} to {1} (direction={2})",
                callingFloor, targetFloor, direction);
    }
}
