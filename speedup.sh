#!/bin/bash

for i in `seq 1 $(($(grep -c ^processor /proc/cpuinfo)))`
do
        TIMEFORMAT=%R
        echo $run"th for "$i" cpus"
        printf "run: %d cpus: %d time: %f\n" $run $i $( { time ./run.sh $i > /dev/null 2>&1; } 2>&1 )
done

for i in `seq 1 $(($(grep -c ^processor /proc/cpuinfo)))`
do
        TIMEFORMAT=%R
        echo "akka "$run"th for "$i" cpus"
        printf "run: %d cpus: %d time: %f\n" $run $i $( { time python run_akka.py $i > /dev/null 2>&1; } 2>&1 )
done
