#!/bin/bash
printf "> Akka implementation for %d cpus with %s configuration, run=%d\n" $SLURM_JOB_CPUS_PER_NODE $CONFIG $RUN
time (cd .. && ./run_akka.sh $CONFIG)
