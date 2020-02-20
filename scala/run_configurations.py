import os

directory = "configurations/"
outdir = "results"
cpusmax = 8

if not os.path.isdir(outdir):
    os.mkdir(outdir)

for config in os.listdir(directory):
    if config.endswith(".properties"):
        for cpus in range(cpusmax):
            configname = config.split('.')[0]
            os.system("python3 run.py {0} {1} {2}".format(cpus, directory + config, "{0}/{1}_{2}".format(outdir, configname, cpus)))
    else:
        continue
