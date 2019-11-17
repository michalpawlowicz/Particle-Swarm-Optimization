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
