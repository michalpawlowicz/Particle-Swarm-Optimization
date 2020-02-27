import json
import sys
import multiprocessing as mp
import os
import shutil
from pathlib import Path
import psutil
import numpy as np

def worker(cpus, runs, return_array):
    proc = psutil.Process()
    proc.cpu_affinity(cpus)
    avgs = []
    for _ in range(runs):
        start = time.process_time()
        os.system("java -jar -DconfAppName={0} benchmarks-proj/target/scala-2.13/Multi-Agent-Particle-Swarm-Optymization-Benchmark-assembly-0.1.jar".format(conf_name))
        end = time.process_time()
        avgs.append(end - start)
    return_array=[np.mean(avgs), np.std(avgs), np.max(avgs), np.min(avgs)]

with open("benchmarks.json") as fp:
    b = json.loads("".join(fp.readlines()))

outdir = b["outdir"]
maxcpus = b["maxcpus"]
runs = b["runs"]
benchmarks = b["benchmarks"]
iterMax = b["iterMax"]
omegaMin = b["omegaMin"]
omegaMax = b["omegaMax"]
phi_1 = b["phi_1"]
phi_2 = b["phi_2"]

if os.path.isdir(outdir):
    shutil.rmtree(outdir)
os.mkdir(outdir)

if os.path.isdir("results"):
    print("WARNING! Don't override results/ folder")
    sys.exit(1)
os.mkdir("results/")

for benchmark in benchmarks:
    particles = benchmark["particles"]
    generator = benchmark["generator"]
    genargs = benchmark["generator-argv"]
    function = benchmark["function"]
    dim = benchmark["dimension"]
    with open(outdir + "/" + "bench.prop", 'w+') as prop:
        prop.seek(0)
        prop.truncate()
        prop.write("particlesCount=%d\n" % particles)
        prop.write("dimension=%d\n" % dim)
        prop.write("iterMax=%d\n" % iterMax)
        prop.write("omegaMin=%f\n" % omegaMin)
        prop.write("omegaMax=%f\n" % omegaMax)
        prop.write("phi_1=%f\n" % phi_1)
        prop.write("phi_2=%f\n" % phi_2)
        prop.write("graph=%s" % (outdir + "/" + "input.graph"))

    with open(outdir + "/" + "input.graph", 'w+') as graph:
        graph.seek(0)
        graph.truncate()
        cmd = "python3 generator.py --{0} --nodes={1} --args \"{2}\" > {3}".format(generator, particles, str(genargs)[1:-1].replace(',', ' '), outdir + "/" + "input.graph")
        print(cmd)
        os.system(cmd)

    for cpus in [range(0, i) for i in range(0, maxcpus)]:
        manager = mp.Manager()
        return_array = manager.array()
        d = dict(cpus=cpus, runs=runs, return_array=return_array)
        p = mp.Process(target=worker, kwargs=d)
        p.start()
        p.join()
        resultpath = "results/{0}-{1}-{2}-{3}.benchmark".format(generator, particles, function, dim) 
        with open(resultpath, 'w+') as runres:
            runres.write("cpus: {0} results: {1}".format(len(cpus), return_array))
