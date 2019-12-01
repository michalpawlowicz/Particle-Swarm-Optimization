#!/bin/bash
echo "AKKA"
for i in `seq 1 $(($(grep -c ^processor /proc/cpuinfo)))`
do
        TIMEFORMAT=%R
        TIME=$(time (python run_akka.py $i >/dev/null 2>&1) 2>&1)
        printf "cpus: %d time: %f\n" $i $TIME
done
