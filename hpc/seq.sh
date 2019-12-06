#!/bin/bash

printf "seq impl for %d cpus\n" $SLURM_JOB_CPUS_PER_NODE

TIMEFORMAT=%R
printf "time: %f\n" $( { time cd .. && ./run.sh $SLURM_JOB_CPUS_PER_NODE > /dev/null 2>&1; } 2>&1 )
