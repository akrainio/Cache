import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@SuppressWarnings("WeakerAccess")
public class Service {
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Cache<String, String> cache;
    public static String timeWaste = "";

    public Service(int size) {
        cache = new Cache<>(size);
    }

    public static String compute(String s) throws InterruptedException {
        s = "prefix-" + s;
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 10) {
            String hi = "hello";
            for (int i = 0; i < 100; ++i) {
                hi += "hi";
            }
            timeWaste = hi;
        }
        return s;
    }

    public String cacheCompute(String s) throws InterruptedException {
        String got;
        lock.readLock().lock();
        try {
            got = cache.find(s);
        } finally {
            lock.readLock().unlock();
        }
        if (got == null) {
            got = compute(s);
            lock.writeLock().lock();
            try {
                cache.add(s, got);
            } finally {
                lock.writeLock().unlock();
            }
        }
        return got;
    }
}