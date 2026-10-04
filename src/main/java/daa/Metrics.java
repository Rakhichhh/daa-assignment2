package daa;

public class Metrics {
    private long steps;
    private long moves;
    private long comparisons;

    public void step() {
        steps++;
    }

    public void move() {
        moves++;
    }

    public void compare() {
        comparisons++;
    }

    public long getSteps() {
        return steps;
    }

    public long getMoves() {
        return moves;
    }

    public long getComparisons() {
        return comparisons;
    }

    public void reset() {
        steps = 0;
        moves = 0;
        comparisons = 0;
    }
}