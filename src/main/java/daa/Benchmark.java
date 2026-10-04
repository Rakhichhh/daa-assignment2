package daa;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Random;

public class Benchmark {
    private static final int WARMUP = 2;
    private static final int RUNS = 5;
    private static volatile long sink;

    private record Result(long timeNs, long steps,
                          long moves, long comparisons) {
    }

    public static void main(String[] args) throws Exception {
        int[] sizes = {100, 1000, 10000, 100000};
        String[] structures = {"DynamicArray", "MyLinkedList"};

        Files.createDirectories(Path.of("results"));

        try (PrintWriter writer = new PrintWriter(
                Files.newBufferedWriter(Path.of("results/results.csv")))) {

            writer.println(
                    "workload,variant,structure,n,time_ms,steps,moves,comparisons");

            for (int n : sizes) {
                Random random = new Random(42);

                int[] values = new int[n];
                for (int i = 0; i < n; i++) {
                    values[i] = random.nextInt(1_000_000);
                }

                int[] indices = new int[10000];
                for (int i = 0; i < indices.length; i++) {
                    indices[i] = random.nextInt(n);
                }

                int[] queries = new int[1000];
                for (int i = 0; i < queries.length; i++) {
                    if (i % 2 == 0) {
                        queries[i] = values[random.nextInt(n)];
                    } else {
                        queries[i] = -1 - random.nextInt(1_000_000);
                    }
                }

                int[] insertions = new int[1000];
                for (int i = 0; i < insertions.length; i++) {
                    insertions[i] = random.nextInt(1_000_000);
                }

                for (String structure : structures) {
                    measure(writer, "W1", "-", structure, n,
                            values, indices, queries, insertions);

                    measure(writer, "W2", "-", structure, n,
                            values, indices, queries, insertions);

                    measure(writer, "W3", "head", structure, n,
                            values, indices, queries, insertions);

                    measure(writer, "W3", "middle", structure, n,
                            values, indices, queries, insertions);
                }

                measure(writer, "W4", "-", "MinHeap", n,
                        values, indices, queries, insertions);
            }
        }

        System.out.println("Saved: results/results.csv");
    }

    private static void measure(
            PrintWriter writer, String workload, String variant,
            String structure, int n, int[] values, int[] indices,
            int[] queries, int[] insertions) {

        Result[] results = new Result[RUNS];

        for (int run = 0; run < WARMUP + RUNS; run++) {
            Result result;

            if (workload.equals("W4")) {
                result = runHeap(values);
            } else {
                result = runList(workload, variant, structure,
                        values, indices, queries, insertions);
            }

            if (run >= WARMUP) {
                results[run - WARMUP] = result;
            }
        }

        for (int i = 1; i < results.length; i++) {
            Result current = results[i];
            int j = i - 1;

            while (j >= 0 && results[j].timeNs() > current.timeNs()) {
                results[j + 1] = results[j];
                j--;
            }

            results[j + 1] = current;
        }

        Result median = results[RUNS / 2];

        writer.printf(Locale.US, "%s,%s,%s,%d,%.6f,%d,%d,%d%n",
                workload, variant, structure, n,
                median.timeNs() / 1_000_000.0,
                median.steps(), median.moves(), median.comparisons());

        writer.flush();

        System.out.printf("%s %s %s n=%d finished%n",
                workload, variant, structure, n);
    }

    private static Result runList(
            String workload, String variant, String structure,
            int[] values, int[] indices, int[] queries, int[] insertions) {

        IntList list;

        if (structure.equals("DynamicArray")) {
            list = new DynamicArray();
        } else {
            list = new MyLinkedList();
        }

        for (int value : values) {
            list.add(value);
        }

        Metrics metrics = list.getMetrics();
        metrics.reset();

        long checksum = 0;
        int found = 0;
        int index = variant.equals("head") ? 0 : values.length / 2;

        long start = System.nanoTime();

        switch (workload) {
            case "W1" -> {
                for (int position : indices) {
                    checksum += list.get(position);
                }
            }

            case "W2" -> {
                for (int query : queries) {
                    if (list.contains(query)) {
                        found++;
                    }
                }
                checksum = found;
            }

            case "W3" -> {
                for (int value : insertions) {
                    list.add(index, value);
                }

                for (int i = 0; i < insertions.length; i++) {
                    checksum += list.remove(index);
                }
            }

            default -> throw new IllegalArgumentException(workload);
        }

        long elapsed = System.nanoTime() - start;
        sink = checksum;

        if (workload.equals("W2") && found != 500) {
            throw new IllegalStateException("Search result is incorrect");
        }

        if (list.size() != values.length) {
            throw new IllegalStateException("List size is incorrect");
        }

        return new Result(elapsed, metrics.getSteps(),
                metrics.getMoves(), metrics.getComparisons());
    }

    private static Result runHeap(int[] values) {
        MinHeap heap = new MinHeap();
        int[] output = new int[values.length];
        Metrics metrics = heap.getMetrics();

        long start = System.nanoTime();

        for (int value : values) {
            heap.insert(value);
        }

        for (int i = 0; i < output.length; i++) {
            output[i] = heap.extractMin();
        }

        long elapsed = System.nanoTime() - start;

        for (int i = 1; i < output.length; i++) {
            if (output[i - 1] > output[i]) {
                throw new IllegalStateException("Output is not sorted");
            }
        }

        if (heap.size() != 0) {
            throw new IllegalStateException("Heap is not empty");
        }

        sink = output[output.length - 1];

        return new Result(elapsed, metrics.getSteps(),
                metrics.getMoves(), metrics.getComparisons());
    }
}