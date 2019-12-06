#!/bin/bash

printf "Akka implementation for %d cpus with %s configuration, run=$d\n" $SLURM_JOB_CPUS_PER_NODE $CONFIG $RUN

TIMEFORMAT=%R
TIME=$(time (cd .. && ./run_akka.sh $CONFIG 1>/dev/null 2>&1) 2>&1)
printf "time: %f\n" $TIME
