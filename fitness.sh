#!/bin/bash

rm -f akka_fitness.out
rm -f synchronous_fitness.out

./run_akka.sh > akka_fitness.out 2>&1
./run.sh $(($(grep -c ^processor /proc/cpuinfo))) > synchronous_fitness.out 2>&1
