public class RequestGenerator implements Runnable {
    private final RequestManager manager = RequestManager.getManager();
    private final int count;
    private final int interval;
    public final static Object obj = new Object();
    public RequestGenerator(int count, int interval) {
        this.count = count;
        this.interval = interval;
    }
    @Override
    public void run() {
        System.out.println("Thread: generator");
        for (int i = 0; i < count; ++i) {
            int callingFloor = (int)(Math.random() * 10) + 1;
            int targetFloor = (int)(Math.random() * 10) + 1;
            while (callingFloor == targetFloor) {
                targetFloor = (int)(Math.random() * 10) + 1;
            }
            var request = new Request(callingFloor, targetFloor);
            manager.enqueueRequest(request);
            System.out.println(request);
            synchronized (obj) {
                obj.notifyAll();
            }
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        manager.isRunning = false;
    }
}
