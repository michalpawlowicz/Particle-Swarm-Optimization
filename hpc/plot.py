import re
import os
from collections import defaultdict

benchmarks=["benchmarks/d512p64"]

#error-zeus-4-akka.sh-d512p64-8.out
#user\t3m40.341s\n', 'sys\t0m10.272s\n']

for dirp in benchmarks:
    d = dict()
    for filename in os.listdir(benchmarks[0]):
        m = re.match("error-zeus-(\d+)-(.+).sh-d(\d+)p(\d+)-(\d+).out", filename)
        if m is not None:
            cpus, impl, dim, particles, run = m.groups()
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
                    if (dim, particles) in d:
                        d[(dim, particles)][cpus].append(min * 60 + sec)
                    else:
                        d[(dim, particles)] = defaultdict(list)
                        d[(dim, particles)][cpus].append(min * 60 + sec)

    print(d)
