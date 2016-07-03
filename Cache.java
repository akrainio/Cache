import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class Cache<K, T> {

    private HashMap<K, Pair<T>> map;
    private long maxSize;

    public Cache (long maxSize) {
        map = new HashMap<K, Pair<T>>();
        this.maxSize = maxSize;
    }

    public void add(K key, T object) {
        Collection<Pair<T>> values = map.values();
        for (Pair<T> p : values) {
            p.update(object);
        }
        if (map.size() >= maxSize) {
            killOldest();
        }
        map.put(key, new Pair<T>(object));
        printCache();
    }

    private void killOldest() {
        if (map.size() == 0)  return;
        Map.Entry<K, Pair<T>> oldest = null;
        Collection<Map.Entry<K, Pair<T>>> values = map.entrySet();
        for (Map.Entry<K, Pair<T>> entry : values) {
            if (oldest == null) {
                oldest = entry;
            } else {
                if (entry.getValue().age > oldest.getValue().age) oldest = entry;
            }
        }
        assert oldest != null;
        System.out.println("REMOVED [Key: " + oldest.getKey() + " | Value: " +
                oldest.getValue().object + " | Age: " + oldest.getValue().age + "]");
        map.remove(oldest.getKey());
    }

    private void printCache() {
        Collection<Map.Entry<K, Pair<T>>> values = map.entrySet();
        for (Map.Entry<K, Pair<T>> entry : values) {
            String s = ("[Key: " + entry.getKey()) +
                    " | Value: " + entry.getValue().object +
                    " | Age: " + entry.getValue().age +
                    "]";
            System.out.println(s);
        }
    }

    private class Pair<E> {

        final E object;
        long age;

        private Pair (E object) {
            this.object = object;
            age = 0;
        }

        private void update (E object) {
            if (this.object == object) {
                age = 0;
            } else {
                age += 1;
            }
        }
    }
}
