#!/bin/bash

rm -f speedup_synchronous.out
for i in `seq 1 $(($(grep -c ^processor /proc/cpuinfo)))`
do
    for run in `seq 0 4`
    do
        TIMEFORMAT=%R
        echo $run"th for "$i" cpus"
        printf "run: %d cpus: %d time: %f\n" $run $i $( { time ./run.sh $i > /dev/null 2>&1; } 2>&1 ) >> speedup_synchronous.out
    done
done

rm -f speedup_akka.out
for i in `seq 1 $(($(grep -c ^processor /proc/cpuinfo)))`
do
    for run in `seq 0 4`
    do
        TIMEFORMAT=%R
        echo "akka "$run"th for "$i" cpus"
        printf "run: %d cpus: %d time: %f\n" $run $i $( { time python run_akka.py $i > /dev/null 2>&1; } 2>&1 ) >> speedup_akka.out
    done
done
