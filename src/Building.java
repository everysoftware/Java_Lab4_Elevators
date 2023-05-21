import java.util.*;
import java.util.stream.IntStream;

public class Building {
    private final Queue<Integer> stops;
    private final List<Boolean> activeFloors;
    private final List<Queue<Request>> requests;
    private final Elevator firstElevator;
    private final Elevator secondElevator;
    public Building(int maxFloor, int elevatorCapacity) {
        this.stops = new ArrayDeque<>();
        this.activeFloors = new ArrayList<>(IntStream.range(0, maxFloor + 1).mapToObj(i -> false).toList());
        this.requests = new ArrayList<>(IntStream.range(0, maxFloor + 1).mapToObj(i -> new ArrayDeque<Request>()).toList());
        this.firstElevator = new Elevator(1, elevatorCapacity, 0);
        this.secondElevator = new Elevator(2, elevatorCapacity, 0);
    }
    public synchronized void callElevator(Request request) {
        System.out.printf("Пассажир #%d вызвал лифт на %d этаже на %d этаж%n",
                request.id, request.callingFloor, request.targetFloor);
        if (!activeFloors.get(request.callingFloor)) {
            activeFloors.set(request.callingFloor, true);
            stops.offer(request.callingFloor);
        }
        this.requests.get(request.callingFloor).offer(request);
    }
    private synchronized void processPassengers(Elevator elevator) {
        activeFloors.set(elevator.currentFloor, false);
        // пассажиры выходят
        List<Request> personsForExit = new ArrayList<>();
        for (var passenger : elevator.passengers) {
            if (passenger.targetFloor == elevator.currentFloor) {
                personsForExit.add(passenger);
            }
        }
        for (var passenger : personsForExit) {
            elevator.passengers.removeIf(x -> (x.id == passenger.id));
            System.out.printf("Пассажир #%d вышел из лифта %d на этаже %d%n",
                    passenger.id, elevator.id, elevator.currentFloor);
        }
        // заходят новые
        while (elevator.passengers.size() < elevator.capacity && !requests.get(elevator.currentFloor).isEmpty()) {
            var passenger = requests.get(elevator.currentFloor).poll();
            assert passenger != null;
            elevator.passengers.add(passenger);
            System.out.printf("Пассажир #%d зашёл в лифт %d на этаже %d в лифт%n",
                    passenger.id, elevator.id, elevator.currentFloor);
        }
        if (!requests.get(elevator.currentFloor).isEmpty()) {
            activeFloors.set(elevator.currentFloor, true);
            stops.offer(elevator.currentFloor);
        }
        // начинаем везти новых пассажиров или останавливаем лифт
        if (!elevator.passengers.isEmpty()) {
            elevator.targetFloor = elevator.passengers.get(0).targetFloor;
            firstElevator.setStatusOnMoving();
        } else {
            System.out.printf("Лифт %d остановился на этаже %d%n", elevator.id, elevator.currentFloor);
            elevator.stop();
        }
    }
    private synchronized void processMoving(Elevator elevator) {
        activeFloors.set(elevator.currentFloor, false);
        // пассажиры выходят
        List<Request> personsForExit = new ArrayList<>();
        for (var passenger : elevator.passengers) {
            if (passenger.targetFloor == elevator.currentFloor) {
                personsForExit.add(passenger);
            }
        }
        for (var passenger : personsForExit) {
            elevator.passengers.removeIf(x -> (x.id == passenger.id));
            System.out.printf("Пассажир #%d вышел из лифта %d на этаже %d%n",
                    passenger.id, elevator.id, elevator.currentFloor);
        }
        var queueCopy = new ArrayDeque<>(requests.get(elevator.currentFloor).stream().filter(entry -> {
            if (elevator.status == ElevatorStatus.MOVING_UP) {
                return entry.targetFloor > elevator.currentFloor;
            } else {
                return entry.targetFloor < elevator.currentFloor;
            }
        }).toList());
        // заходят новые
        while (elevator.passengers.size() < elevator.capacity && !queueCopy.isEmpty()) {
            var passenger = queueCopy.poll();
            elevator.passengers.add(passenger);
            requests.get(elevator.currentFloor).removeIf(entry -> Objects.equals(entry.targetFloor, passenger.targetFloor));
            System.out.printf("Пассажир #%d зашёл в лифт %d на этаже %d%n",
                    passenger.id, elevator.id, elevator.currentFloor);
        }
        if (!requests.get(elevator.currentFloor).isEmpty()) {
            activeFloors.set(elevator.currentFloor, true);
            stops.offer(elevator.currentFloor);
        }
        elevator.setStatusOnMoving();
        System.out.printf("Лифт %d едет %s к этажу %d (%d)%n",
                elevator.id, elevator.status == ElevatorStatus.MOVING_UP ? "вверх" : "вниз",
                elevator.targetFloor, elevator.currentFloor);
    }
    public synchronized void processElevator(Elevator elevator) {
        if (elevator.currentFloor < elevator.targetFloor){
            elevator.currentFloor++;
            elevator.status = ElevatorStatus.MOVING_UP;
        } else if (elevator.currentFloor > elevator.targetFloor){
            elevator.currentFloor--;
            elevator.status = ElevatorStatus.MOVING_DOWN;
        }
        if (elevator.currentFloor == elevator.targetFloor){
            processPassengers(elevator);
        } else {
            processMoving(elevator);
        }
    }
    public synchronized void runElevators() {
        if (firstElevator.status == ElevatorStatus.IDLE){
            if (stops.isEmpty()){
                System.out.println("Лифт 1 ждёт заявок...");
            } else {
                firstElevator.targetFloor = stops.poll();
                if (firstElevator.targetFloor == firstElevator.currentFloor){
                    processPassengers(firstElevator);
                } else {
                    firstElevator.setStatusOnMoving();
                }
            }
        }
        if (secondElevator.status == ElevatorStatus.IDLE){
            if (stops.isEmpty()){
                System.out.println("Лифт 2 ждёт заявок...");
            } else {
                secondElevator.targetFloor = stops.poll();
                if (secondElevator.targetFloor == secondElevator.currentFloor){
                    processPassengers(secondElevator);
                } else {
                    secondElevator.setStatusOnMoving();
                }
            }
        }
        if (firstElevator.status != ElevatorStatus.IDLE){
            processElevator(firstElevator);
            processElevator(secondElevator);
        }
    }
}
