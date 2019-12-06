#!/bin/bash

printf "Seq implementation for %d cpus with $s configuration, run=%d\n" $SLURM_JOB_CPUS_PER_NODE $CONFIG $RUN

TIMEFORMAT=%R
time (cd .. && ./run.sh $SLURM_JOB_CPUS_PER_NODE $CONFIG)
