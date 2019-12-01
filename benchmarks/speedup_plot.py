"""
import re
from collections import defaultdict
import matplotlib.pyplot as plt

cpus = 16
runs = 5

file_list = [("speedup_akka.out", "akka", "b", "o"), ("speedup_synchronous.out", "synchronous", "b", "o")]
for f, label, color, marker in file_list:
    with open(f, "r") as fd:
        lines = fd.readlines()
    d = defaultdict(list)
    for l in lines:
        m = re.match(r"run: (\d+) cpus: (\d+) time: (\d+.\d*)", l)
        run, cpus, time = m.groups()
        d[int(cpus)].append(float(time))
    y = []
    X = []
    for key in d:
        y.append(sum(d[key]) / len(d[key]))
        X.append(key)
    plt.plot(X, [y[0] / yi for yi in y], linestyle='--', marker=marker, color=color, label=label)
    plt.xticks(range(len(y)))

plt.plot([x for x in range(1, len(runs * cpus) + 1)], [x for x in range(1, len(runs * cpus) + 1)], linestyle='--', marker='x', color='orange', label="ideal")

plt.legend(loc='upper left')
plt.savefig("speedup.png")
"""

import os
import re
import matplotlib.pyplot as plt

max_len = 0
for directory, label, color, marker in [("decentrailized_benchmarks/speedup", "akka", "b", "o"), ("synchronous_benchmarks/speedup", "synchronous", "g", "x")]:
    X = []
    for filename in os.listdir(directory):
        if filename.endswith(".out") or filename.startswith("report"):
            with open(os.path.join(directory, filename), "r") as fd:
                lines = fd.readlines()
            times = []
            for l in lines:
                m = re.match(r"time: (\d+.\d*)", l)
                time = m.groups()
                times.append(float(time[0]))
            average_time = sum(times) / len(times)
            cpus = int(re.match(r"report-zeus-(\d+).out", filename).groups()[0])
            X.append((cpus, average_time))
    X.sort(key=lambda x: x[0])
    X, Y = zip(*X)
    plt.plot(X, [Y[0] / y for y in Y], linestyle='--', marker=marker, color=color, label=label)
    plt.xticks(range(len(Y)))
    max_len = max(len(X), max_len)

plt.plot(range(max_len), range(max_len), label="ideal")
plt.legend(loc='upper left')
plt.show()
