import matplotlib.pyplot as plt
import os
import re
import sys

CORE_INDEX = 0

if __name__ == '__main__':

    dirp = sys.argv[1]
    results = dict()

    dirs = os.listdir(dirp)

    dirs = [f for f in dirs if os.path.isdir(dirp + "/" + f)]
    # files = os.listdir(dirp)

    for directory in dirs:
        files = os.listdir(dirp + "/" + directory)
        files = [f for f in files if f.startswith("time")]

        for filename in files:
            core_number = re.findall(r'\d+', filename)[CORE_INDEX]
            with open(dirp + "/" + directory + "/" + filename, "r") as fp:
                lines = fp.readlines()
                for line in lines:
                    m = re.match(".*real.*(\d+)m(\d+[,\.].\d+).*", line)
                    if m is not None:
                        real_min, real_sec = m.groups()
                        results[core_number] = float(real_sec.replace(',', '.'))

    lists = sorted(results.items())  # sorted by key, return a list of tuples

    x, y = zip(*lists)  # unpack a list of pairs into two tuples

    y = [y[0] / e for e in y]
    plt.plot(range(len(x)), [x + 1 for x in range(len(x))])
    plt.plot(x, y)
    plt.savefig(dirp + '/speedup_{}.png'.format(dirp))
    plt.show()
