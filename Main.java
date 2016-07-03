import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Objects;

public class Main {
    public static void main (String[] args) throws IOException {
        Cache<String, String> cache = new Cache<>(5);
        try (LineNumberReader lineNumberReader = new LineNumberReader(new InputStreamReader(System.in))) {
            String s = lineNumberReader.readLine();
            while (!Objects.equals(s, "q")) {
                try {
                    String  value = s.toUpperCase();
                    cache.add(s, value);
                } catch (NumberFormatException e) {
                    System.out.println("Not a valid input");
                }
                s = lineNumberReader.readLine();
            }
        }

    }
}
