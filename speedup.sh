#!/bin/bash

for i in `seq 1 $(($(grep -c ^processor /proc/cpuinfo)))`
do
        TIMEFORMAT=%R
        printf "cpus: %d time: %f\n" $i $( { time ./run.sh $i > /dev/null 2>&1; } 2>&1 )
done

echo "AKKA START"

for i in `seq 1 $(($(grep -c ^processor /proc/cpuinfo)))`
do
        TIMEFORMAT=%R
        printf "cpus: %d time: %f\n" $i $( { time python run_akka.py $i > /dev/null 2>&1; } 2>&1 )
done
