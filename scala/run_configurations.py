import os

directory = "configurations/"
outdir = "results"
cpusmax = 8

print("Starting..")
print(list(os.listdir(directory)))
if not os.path.isdir(outdir):
    os.mkdir(outdir)


for config in os.listdir(directory):
    print("Starting..")
    if config.endswith(".properties"):
        print("-> Running configuration {}".format(config))
        for cpus in range(1, cpusmax + 1):
            print("    -> configuration run on {} cpus".format(cpus))
            configname = config.split('.')[0]
            os.system("python run.py {0} {1} {2}".format(cpus, directory + config, "{0}/{1}_{2}".format(outdir, configname, cpus)))
    else:
        continue
