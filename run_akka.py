import multiprocessing as mp
import psutil
import time
import sys
import os


def run_child(affinity):
    proc = psutil.Process()  # get self pid
    proc.cpu_affinity(affinity)
    os.system("./run_akka.sh")

if __name__ == '__main__':
    affinity = list(range(int(sys.argv[1])))
    d = dict(affinity=affinity)
    p = mp.Process(target=run_child, kwargs=d)
    p.start()
    p.join()
