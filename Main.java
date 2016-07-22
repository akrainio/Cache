public class Main {
    private final static int size = 100;
    private static long startTime = System.currentTimeMillis();
    private static long requestCount = 0;
    private final static Object lock = new Object();
    public static void main(String args[]) {
        Service service = new Service(size);
        Runnable worker = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        load(service, size * 2);
                    } catch (InterruptedException e) {
                        throw new AssertionError(e);
                    }
                }
            }
        };
        for (int i = 0; i < 20; ++i) {
            Thread thread = new Thread(worker);
            thread.start();
        }
    }

    private static void load(Service service, int count) throws InterruptedException {
        for (int i = 0; i < count; ++i) {
            service.cacheCompute("k" + i);
        }
        addRequests(count);
    }

    private static void addRequests(int count) {
        synchronized (lock) {
            requestCount += count;
            long elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime > 1000) {
                System.out.println((requestCount*1000) / elapsedTime + " reqs/s");
                requestCount = 0;
                startTime = System.currentTimeMillis();
            }
        }
    }
}
