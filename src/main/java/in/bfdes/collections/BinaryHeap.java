package in.bfdes.collections;

import java.util.function.Predicate;

public class BinaryHeap<V, K extends Comparable<K>> implements PriorityQueue<V, K> {
    private K[] keys;
    private V[] values;
    private final Map<V, Integer> index = new HashMap<>();
    private int size = 0;

    public BinaryHeap() {
        this(10);
    }

    public BinaryHeap(int initialCapacity) {
        if (initialCapacity < 1)
            throw new IllegalArgumentException();
        keys = (K[]) new Object[initialCapacity + 1];
        values = (V[]) new Object[initialCapacity + 1];
    }

    @Override
    public boolean contains(V value) {
        return index.contains(value);
    }

    @Override
    public void push(V value, K key) {
        if (contains(value)) {
            var i = index.get(value);
            var oldKey = keys[i];
            keys[i] = key;
            var isGreater = key.compareTo(oldKey) > 0;
            if (isGreater)
                sink(i);
            else
                swim(i);
        } else {
            if (isFull())
                resize(2 * keys.length);
            keys[++size] = key;
            values[size] = value;
            index.put(value, size);
            swim(size);
        }
    }

    @Override
    public V peek() {
        if (isEmpty())
            throw new IllegalStateException();
        return values[1];
    }

    @Override
    public V pop() {
        if (isEmpty())
            throw new IllegalStateException();
        var smallestItem = values[1];

        swap(1, size);
        // Prevent loitering
        keys[size] = null;
        values[size] = null;
        size--;
        sink(1);
        // Clear index
        index.delete(smallestItem);

        // Lazily reduces the size of the backing array, if necessary
        if (size == keys.length / 4)
            resize(keys.length / 2);

        return smallestItem;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    private boolean isFull() {
        return size == keys.length - 1;
    }

    /**
     * Returns `true` if this binary tree is heap ordered, `false` otherwise.
     * Note: A `BinaryHeap` should always be in heap order -- this method is for debugging.
     */
    public boolean isHeapOrdered() {
        var isHeapOrdered = new Predicate<Integer>() {
            @Override
            public boolean test(Integer k) {
                if (k > size)
                    return true;
                var l = 2 * k;
                var r = 2 * k + 1;
                if (l <= size && isGreater(k, l))
                    return false;
                if (r <= size && isGreater(k, r))
                    return false;
                return test(l) && test(r);
            }
        };
        return isHeapOrdered.test(1);
    }

    private void resize(int newCapacity) {
        var newKeys = (K[]) new Object[newCapacity];
        var newValues = (V[]) new Object[newCapacity];
        for (var i = 0; i < size; i++) {
            newKeys[i] = keys[i];
            newValues[i] = values[i];
        }
        keys = newKeys;
        values = newValues;
    }

    private void sink(int k) {
        while (2 * k <= size) {
            var smallest = 2 * k < size && isGreater(2 * k, 2 * k + 1) ? 2 * k + 1 : 2 * k;
            if (!isGreater(k, smallest)) break;
            swap(k, smallest);
            k = smallest;
        }
    }

    private void swim(int k) {
        while (k > 1 && isGreater(k / 2, k)) {
            swap(k / 2, k);
            k = k / 2;
        }
    }

    private boolean isGreater(int i, int j) {
        return keys[i].compareTo(keys[j]) > 0;
    }

    private void swap(int i, int j) {
        // Swap keys
        var tmpKey = keys[i];
        keys[i] = keys[j];
        keys[j] = tmpKey;

        // Maintain index
        index.put(values[i], j);
        index.put(values[j], i);

        // Swap values
        var tmpValue = values[i];
        values[i] = values[j];
        values[j] = tmpValue;
    }
}