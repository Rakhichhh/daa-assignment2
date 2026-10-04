# DAA Assignment 2 — DAA
**Student:** Myrzabekov Rakhat  
**Group:** SE-2518
Java implementations of DynamicArray, MyLinkedList and MinHeap
with operation counters, JUnit 5 tests and reproducible benchmarks.

## Requirements

- JDK 17
- Maven
- Python 3 and matplotlib for plots

## Build and run tests

```bash
mvn clean verify
```

## Run benchmark

Run from the project root:

```bash
mvn compile
java -cp target/classes daa.Benchmark
```

The benchmark uses Random(42), two warm-up runs and five measured
runs per case. It saves the median time and operation counters to
results/results.csv.

Workloads:

- W1: 10000 random index accesses.
- W2: 1000 searches, half present and half absent.
- W3: 1000 insertions followed by 1000 removals at head or n/2.
- W4: n heap insertions followed by n minimum extractions.

Sizes: 100, 1000, 10000 and 100000.

## Generate plots

```bash
python3 -m pip install matplotlib
python3 plots.py
```

Charts are saved to results/plots/.

## Project layout

- src/main/java/daa/ — structures, metrics and benchmark
- src/test/java/daa/ — JUnit 5 tests
- results/results.csv — benchmark measurements
- results/plots/ — four workload charts
- plots.py — chart generation script
- REPORT.md — complexity analysis, proofs and discussion

## Git workflow

Feature branches: feature/array, feature/list, feature/heap
and feature/metrics.

The final working version is on main with release tag v1.0.
**GitHub:** https://github.com/Rakhichhh/daa-assignment2