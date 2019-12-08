import re
import os
from collections import defaultdict
import matplotlib.pyplot as plt

benchmarks=["benchmarks/d50p256"]

#error-zeus-4-akka.sh-d512p64-8.out
#user\t3m40.341s\n', 'sys\t0m10.272s\n']

for dirp in benchmarks:
    d = dict()
    dimention = 0
    particles = 0
    for filename in os.listdir(benchmarks[0]):
        m = re.match("error-zeus-(\d+)-(.+).sh-d(\d+)p(\d+)-(\d+).out", filename)
        if m is not None:
            cpus, impl, dimention_tmp, particles_tmp, run = m.groups()
            if (dimention != 0 and particles != 0) and (dimention != dimention_tmp or particles != particles_tmp):
                raise RuntimeError("Dimention and particles error")
            with open(dirp + "/" + filename, "r") as fp:
                lines = fp.readlines()
            for l1, l2, l3 in zip(lines, lines[1:], lines[2:]):
                m_user = re.match("user.*(\d+)m(\d+\.\d+)", l2)
                m_sys = re.match("sys.*(\d+)m(\d+\.\d+)", l3)
                if m_user is not None and m_sys is not None:
                    sys_min, sys_sec = m_sys.groups()
                    user_min, user_sec = m_user.groups()
                    min = int(user_min) + int(sys_min)
                    sec = float(user_sec) + float(sys_sec)
                    if impl in d:
                        d[impl][int(cpus)].append(min * 60 + sec)
                    else:
                        d[impl] = defaultdict(list)
                        d[impl][int(cpus)].append(min * 60 + sec)
    if len(d) > 2:
        raise RuntimeError("Len is > 2")

    akka = sorted(list(d["akka"].items()), key=lambda t: t[0])
    seq = sorted(list(d["seq"].items()), key=lambda t:t[0])
    for (cpus, l) in akka:
        if len(l) != 10:
            print("WARRNING! Len of akka for {0} cpus equals {1}".format(cpus, len(l)))
    for (cpus, l) in seq:
        if len(l) != 10:
            print("WARRNING! Len of seq for {0} cpus equals {1}".format(cpus, len(l)))

    # PLOT
    for impl in [akka, seq]:
        Y = []
        for (cpus, l) in impl:
            Y.append(sum(l) / len(l))
            print("For {0} cpus avg = {1}".format(cpus, sum(l) / len(l)))
        plt.plot(range(len(Y)), [Y[0] / y for y in Y])

    plt.show()
