import re
from collections import defaultdict

import matplotlib.pyplot as plt

file_list = [("fitness_akka.out", "akka", "b", "o"), ("fitness_synchronous.out", "synchronous", "b", "o")]
for f, label, color, marker in file_list:
    with open(f, "r") as fd:
        lines = fd.readlines()
    d = defaultdict(list)

    for l in lines:
        m = re.match(r".*Fitness\[\s*(?P<fit>\d+.?\d*)\s*\]\s*Iteration\[\s*(?P<iteration>\d+)\s*\]", l)
        fitness = m.group("fit")
        iteration = m.group("iteration")

        d[int(iteration)].append(float(fitness))

    x = []
    y = []
    for iteration, fitnesses in d.items():
        x.append(iteration)
        y.append(min(fitnesses))

    plt.plot(x, y, linestyle='--', marker=marker, color=color, label=label)

plt.legend(loc='upper left')
plt.savefig("fitness.png")
