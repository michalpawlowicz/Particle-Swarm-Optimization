import matplotlib.pyplot as plt
import os
import re
import sys

CORE_INDEX = 0
directories = ["512_128_1div6_mod1000_it3e5"]

if __name__ == '__main__':

    dirp = sys.argv[1]
    results = dict()

    files = os.listdir(dirp)
    files = [f for f in files if f.startswith("time")]

    for filename in files:
        core_number = re.findall(r'\d+', filename)[CORE_INDEX]

        with open(dirp + "/" + filename, "r") as fp:
            lines = fp.readlines()
            for line in lines:
                m = re.match(".*real.*(\d+)m(\d+[,\.].\d+).*", line)
                if m is not None:
                    real_min, real_sec = m.groups()
                    results[core_number] = int(real_min) * 60 + float(real_sec.replace(',', '.'))

    lists = sorted(results.items())  # sorted by key, return a list of tuples

    x, y = zip(*lists)  # unpack a list of pairs into two tuples

    y = [y[0] / e for e in y]
    plt.plot(range(len(x)), [x + 1 for x in range(len(x))])
    plt.plot(x, y)
    plt.savefig(dirp + '/speedup.png')
    plt.show()
