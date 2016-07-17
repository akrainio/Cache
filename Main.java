import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Objects;

public class Main {
    public static void main (String[] args) throws IOException {
        final int cacheSize = 3;
        Cache<String, String> cache = new Cache<>(cacheSize);
        try (LineNumberReader lineNumberReader = new LineNumberReader(new InputStreamReader(System.in))) {
            System.out.println("Max size of cache is " + cacheSize);
            System.out.println("Enter value to perform operation on:");
            String s = lineNumberReader.readLine();
            while (!Objects.equals(s, "q")) {
                String got = cache.find(s);
                if (got == null) {
                    System.out.println("Not in cache");
                    got = operation(s);
                    cache.add(s, got);
                } else {
                    System.out.println("Found in cache");
                }
                cache.printLRU();
                System.out.println("Next value:");
                s = lineNumberReader.readLine();
            }
        }
    }
    private static String operation(String s) {
        return s.toUpperCase();
    }
}