import multiprocessing as mp
import os
import psutil
import sys
from pathlib import Path
import datetime

import time

def run_child(affinity, conf_name, dir_result):
    print(affinity, conf_name, dir_result)


    proc = psutil.Process()  # get self pid
    proc.cpu_affinity(affinity)
    RUNS=2
    fullTime =datetime.timedelta(0)
    for i in range(RUNS):
        time1 = datetime.datetime.now()
        os.system("java -jar -DconfAppName={} target/scala-2.13/Particle-Swarm-Optymization-Scala-assembly-0.1.jar".format(conf_name))
        time2 = datetime.datetime.now() # waited a few minutes before pressing enter
        elapsedTime = time2 - time1

        fullTime = fullTime + elapsedTime

    print ("FT ", fullTime)

    elapsedTime = fullTime / 2

    print("AV ", elapsedTime)
    seconds = elapsedTime.seconds
    microseconds = elapsedTime.microseconds
    minutes = (seconds % 3600) // 60

    string_time = str(minutes)  + "m" + str(seconds) + "," + str(microseconds)
    Path(dir_result).mkdir(parents=True, exist_ok=True)
    with open(dir_result + "/time_" + str(affinity[-1] + 1), "w") as file: # Use file to refer to the file object
        file.write("real " + string_time)


if __name__ == '__main__':

    affinity = list(range(int(sys.argv[1])))
    conf_name = sys.argv[2]
    dir_result = sys.argv[3]
    d = dict(affinity=affinity, conf_name=conf_name, dir_result=dir_result)
    p = mp.Process(target=run_child, kwargs=d)
    p.start()
    p.join()
