import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Thread: main");

        var scanner = new Scanner(System.in);
        int count = 0;
        int interval = 0;
        try {
            System.out.print("Request count: ");
            count = scanner.nextInt();
            System.out.print("Request interval: ");
            interval = scanner.nextInt();
        } catch (NoSuchElementException | IllegalStateException e) {
            e.printStackTrace();
        }

        var managerThread = new Thread(new RequestManager());
        var generationThread = new Thread(new RequestGenerator(count, interval));

        try {
            managerThread.start();
            generationThread.start();

            managerThread.join();
            generationThread.join();
        } catch (IllegalStateException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
