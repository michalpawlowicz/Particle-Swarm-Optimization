#!/bin/bash

printf "seq impl for %d cpus" $SLURM_JOB_CPUS_PER_NODE

TIMEFORMAT=%R
printf "time: %f\n" $( { time ../run.sh $SLURM_JOB_CPUS_PER_NODE > /dev/null 2>&1; } 2>&1 )
