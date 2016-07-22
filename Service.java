import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Objects;

public class Service {

    private final Cache<String, String> cache;
    public static String timeWaste = "";

    public Service(int size) {
        cache = new Cache<>(size);
    }

    private static String operation(String s) {
        return s.toUpperCase();
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
        synchronized (cache) {
            got = cache.find(s);
        }
        if (got == null) {
            got = compute(s);
            synchronized (cache) {
                cache.add(s, got);
            }
        }
        return got;
    }
}