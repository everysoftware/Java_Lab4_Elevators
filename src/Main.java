import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        int maxFloor;
        int capacity;
        int passengerInterval;
        int velocity;
        try {
            System.out.println("Минимальный этаж: 0.");
            System.out.print("Максимальный этаж: ");
            maxFloor = scanner.nextInt();
            System.out.print("Вместимость лифтов: ");
            capacity = scanner.nextInt();
            System.out.print("Интервал появления пассажиров (мс): ");
            passengerInterval = scanner.nextInt();
            System.out.print("Скорость движения лифта (мс на пролёт 1 этажа): ");
            velocity = scanner.nextInt();
        } catch (NoSuchElementException | IllegalStateException e) {
            e.printStackTrace();
            return;
        }

        var building = new Building(maxFloor, capacity);
        var passengerIds = new HashSet<Integer>();
        var random = new Random(Instant.now().getEpochSecond());
        var timer = new Timer();
        var executor = Executors.newCachedThreadPool();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                executor.execute(building::runElevators);
            }
        }, 0, velocity);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                var from = random.nextInt(0, maxFloor + 1);
                var to = random.nextInt(0, maxFloor + 1);
                while (from == to) {
                    to = random.nextInt(0, maxFloor + 1);
                }
                var personId = random.nextInt(Integer.MAX_VALUE);
                while (passengerIds.contains(personId)) {
                    personId = random.nextInt(Integer.MAX_VALUE);
                }
                passengerIds.add(personId);
                var request = new Request(personId, from, to);
                executor.execute(() -> building.callElevator(request));

            }
        }, 0, passengerInterval);
    }
}
