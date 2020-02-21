import os
import matplotlib.pyplot as plt

directory = "outs/32_4096/fitness/"

for fitness in os.listdir(directory):
    X = []
    Y = []
    if fitness.endswith(".fitness"):
        with open(directory+fitness) as fp:
            lines = [[s.rstrip() for s in l.split(' ')] for l in fp.readlines()]
            lines = [(int(l[0]), float(l[1])) for l in lines]
        for (x, y) in lines:
            X.append(x)
            Y.append(y)
    plt.plot(X, Y, label=fitness.split('.')[0])
plt.savefig("fintess_32_4096.png")
plt.legend()
plt.show()
