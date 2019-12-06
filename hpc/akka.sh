#!/bin/bash

printf "akka impl for %d cpus\n" $SLURM_JOB_CPUS_PER_NODE

TIMEFORMAT=%R
TIME=$(time (../run_akka.sh 1>/dev/null 2>&1) 2>&1)
printf "time: %f\n" $TIME
