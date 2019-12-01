#!/bin/bash
TIMEFORMAT=%R
TIME=$(time (./run_akka.sh 1>/dev/null 2>&1) 2>&1)
printf "time: %f\n" $i $TIME
