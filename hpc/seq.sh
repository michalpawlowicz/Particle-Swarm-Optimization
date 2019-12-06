#!/bin/bash

printf "Seq implementation for %d cpus with $s configuration, run=%d\n" $SLURM_JOB_CPUS_PER_NODE $CONFIG $RUN

TIMEFORMAT=%R
printf "time: %f\n" $( { time (cd .. && ./run.sh $SLURM_JOB_CPUS_PER_NODE $CONFIG > /dev/null 2>&1); } 2>&1 )
