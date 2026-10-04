package daa;

import org.junit.jupiter.api.Test;

import java.util.PriorityQueue;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class MinHeapTest {

    @Test
    void emptyHeap() {
        MinHeap heap = new MinHeap();

        assertEquals(0, heap.size());
        assertTrue(heap.isValidHeap());

        assertThrows(IllegalStateException.class,
                () -> heap.peekMin());
        assertThrows(IllegalStateException.class,
                () -> heap.extractMin());
    }

    @Test
    void oneElement() {
        MinHeap heap = new MinHeap();

        heap.insert(7);

        assertTrue(heap.isValidHeap());
        assertEquals(7, heap.peekMin());
        assertEquals(1, heap.size());
        assertEquals(7, heap.extractMin());
        assertEquals(0, heap.size());
        assertTrue(heap.isValidHeap());

        heap.insert(-3);
        assertEquals(-3, heap.extractMin());
    }

    @Test
    void duplicatesAndExtremeValues() {
        MinHeap heap = new MinHeap();
        int[] values = {5, 5, Integer.MAX_VALUE, -2, Integer.MIN_VALUE};
        int[] expected = {Integer.MIN_VALUE, -2, 5, 5, Integer.MAX_VALUE};

        for (int value : values) {
            heap.insert(value);
            assertTrue(heap.isValidHeap());
        }

        for (int value : expected) {
            assertEquals(value, heap.extractMin());
            assertTrue(heap.isValidHeap());
        }

        assertEquals(0, heap.size());
    }

    @Test
    void randomDataMatchesPriorityQueue() {
        MinHeap heap = new MinHeap();
        PriorityQueue<Integer> expected = new PriorityQueue<>();
        Random random = new Random(42);

        for (int i = 0; i < 1000; i++) {
            int value = random.nextInt();

            heap.insert(value);
            expected.add(value);

            assertTrue(heap.isValidHeap());
            assertEquals(expected.peek().intValue(), heap.peekMin());
            assertEquals(expected.size(), heap.size());
        }

        int previous = Integer.MIN_VALUE;

        while (!expected.isEmpty()) {
            int current = heap.extractMin();

            assertEquals(expected.remove().intValue(), current);
            assertTrue(current >= previous);
            assertTrue(heap.isValidHeap());
            assertEquals(expected.size(), heap.size());

            previous = current;
        }

        assertThrows(IllegalStateException.class,
                () -> heap.extractMin());
    }
}