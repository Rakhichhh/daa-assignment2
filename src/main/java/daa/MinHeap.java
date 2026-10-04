package daa;

public class MinHeap {
    private int[] data = new int[10];
    private int size;
    private final Metrics metrics = new Metrics();

    public void insert(int value) {
        ensureCapacity();

        int index = size;
        data[index] = value;
        size++;

        while (index > 0) {
            int parent = (index - 1) / 2;

            if (!less(index, parent)) {
                break;
            }

            swap(index, parent);
            index = parent;
        }
    }

    public int peekMin() {
        checkNotEmpty();
        return read(0);
    }

    public int extractMin() {
        checkNotEmpty();

        int minimum = read(0);
        size--;

        if (size > 0) {
            data[0] = read(size);
            metrics.move();
            bubbleDown(0);
        }

        return minimum;
    }

    public int size() {
        return size;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    private void bubbleDown(int index) {
        while (index < size / 2) {
            int left = 2 * index + 1;
            int right = left + 1;
            int smallest = left;

            if (right < size && less(right, left)) {
                smallest = right;
            }

            if (!less(smallest, index)) {
                break;
            }

            swap(index, smallest);
            index = smallest;
        }
    }

    private int read(int index) {
        metrics.step();
        return data[index];
    }

    private boolean less(int first, int second) {
        int a = read(first);
        int b = read(second);

        metrics.compare();
        return a < b;
    }

    private void swap(int first, int second) {
        int a = read(first);
        int b = read(second);

        data[first] = b;
        metrics.move();

        data[second] = a;
        metrics.move();
    }

    private void ensureCapacity() {
        if (size < data.length) {
            return;
        }

        int[] bigger = new int[data.length * 2];

        for (int i = 0; i < size; i++) {
            bigger[i] = read(i);
            metrics.move();
        }

        data = bigger;
    }

    private void checkNotEmpty() {
        if (size == 0) {
            throw new IllegalStateException("Heap is empty");
        }
    }

    boolean isValidHeap() {
        for (int child = 1; child < size; child++) {
            int parent = (child - 1) / 2;

            if (data[parent] > data[child]) {
                return false;
            }
        }

        return true;
    }
}