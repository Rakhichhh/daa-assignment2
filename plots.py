import csv
from pathlib import Path

import matplotlib
matplotlib.use("Agg")

import matplotlib.pyplot as plt

root = Path(__file__).resolve().parent
output = root / "results" / "plots"
output.mkdir(parents=True, exist_ok=True)

with open(root / "results" / "results.csv", newline="") as file:
    rows = list(csv.DictReader(file))

for row in rows:
    row["n"] = int(row["n"])
    row["time_ms"] = float(row["time_ms"])

    for metric in ("steps", "moves", "comparisons"):
        row[metric] = int(row[metric])

titles = {
    "W1": "Random Access",
    "W2": "Search",
    "W3": "Insert and Remove",
    "W4": "Priority Processing",
}

metrics = [
    ("time_ms", "Time (ms)"),
    ("steps", "Steps"),
    ("moves", "Moves"),
    ("comparisons", "Comparisons"),
]

colors = {
    "DynamicArray": "tab:blue",
    "MyLinkedList": "tab:orange",
    "MinHeap": "tab:green",
}

for workload, title in titles.items():
    selected = [row for row in rows if row["workload"] == workload]

    groups = sorted({
        (row["structure"], row["variant"])
        for row in selected
    })

    fig, axes = plt.subplots(2, 2, figsize=(12, 8))

    for ax, (metric, label) in zip(axes.flat, metrics):
        for structure, variant in groups:
            data = [
                row for row in selected
                if row["structure"] == structure
                and row["variant"] == variant
            ]
            data.sort(key=lambda row: row["n"])

            name = structure
            if variant != "-":
                name += " " + variant

            ax.plot(
                [row["n"] for row in data],
                [row[metric] for row in data],
                label=name,
                color=colors[structure],
                linestyle="--" if variant == "middle" else "-",
                marker="s" if variant == "middle" else "o",
            )

        ax.set_xscale("log")

        if metric == "time_ms":
            ax.set_yscale("log")
        else:
            ax.set_yscale("symlog", linthresh=1)

        ax.set_title(label + " vs n")
        ax.set_xlabel("n (elements)")
        ax.set_ylabel(label)
        ax.grid(True, which="both", alpha=0.3)
        ax.legend(fontsize=8)

    fig.suptitle(workload + " — " + title)
    fig.tight_layout()
    fig.savefig(output / (workload + ".png"), dpi=180)
    plt.close(fig)

print("Created W1.png, W2.png, W3.png, W4.png")