import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@SuppressWarnings("WeakerAccess")
public class Cache<K, T> implements Iterable<K> {

    private HashMap<K, Node> map;
    private long maxSize;
    private DoubleList lru;
    private final Lock readLock;
    private final Lock writeLock;

    public Cache(long maxSize) {
        map = new HashMap<K, Node>();
        this.maxSize = maxSize;
        lru = new DoubleList();
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();
    }

    public T find(K key) {
        readLock.lock();
        try {
            Node temp = map.get(key);
            if (temp == null) {
                return null;
            } else {
                lru.sendBack(temp);
                return temp.data;
            }
        } finally {
            readLock.unlock();
        }
    }

    public void add(K key, T object) {
        writeLock.lock();
        try {
            if (map.size() >= maxSize) {
                killOldest();
            }
            Node temp = new Node(key, object);
            lru.append(temp);
            map.put(key, temp);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public String toString() {
        readLock.lock();
        try {
            StringBuilder stringBuilder = new StringBuilder();
            for (K key : this) {
                stringBuilder.append(key).append(", ");
                if (stringBuilder.length() > 10000) {
                    stringBuilder.append("...");
                    break;
                }
            }
            if (stringBuilder.length() > 0) {
                stringBuilder.setLength(stringBuilder.length() - 2);
            }
            return stringBuilder.toString();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Iterator<K> iterator() {
        return lru.iterator();
    }

    private void killOldest() {
        if (map.size() == 0)  return;
        Node temp = lru.front;
        lru.remove(temp);
        map.remove(temp.key);
    }

    private class DoubleList {
        Node front;
        Node back;
        DoubleList() {
            front = null;
            back = null;
        }

        Iterator<K> iterator() {
            return new Iterator<K>() {
                Node node = lru.front;
                @Override
                public boolean hasNext() {
                    return (node != null);
                }

                @Override
                public K next() {
                    if (!hasNext()) throw new NoSuchElementException();
                    K key = node.key;
                    node = node.next;
                    return key;
                }
            };
        }

        void append(Node node) {
            node.prev = null;
            node.next = null;
            if (back == null) {
                back = node;
                front = back;
            } else {
                back.next = node;
                back = back.next;
            }
        }

        void sendBack(Node node) {
            remove(node);
            append(node);
        }

        private void remove(Node node) {
            if (node == lru.front) {
                lru.front = node.next;
            }
            if (node == lru.back) {
                lru.back = node.prev;
            }
            if (node.next != null) {
                node.next.prev = node.prev;
            }
            if (node.prev != null) {
                node.prev.next = node.next;
            }
        }
    }

    private class Node {
        Node next;
        Node prev;
        T data;
        K key;

        private Node(K key, T data) {
            this.data = data;
            this.key = key;
        }

        @Override
        public String toString() {
            return "[" + key + "|" + data + "]";
        }
    }
}