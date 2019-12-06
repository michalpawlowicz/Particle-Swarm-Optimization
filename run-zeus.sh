CPUS=$(seq 1 24)
RUNS=$(seq 1 10)
IMPLS=(run_akka.sh)

echo "> Running"
for cpus in ${CPUS}; do
    for run in ${RUNS}; do
        for impl in ${IMPLS[*]}; do
            echo "Scheduling CPUS=$cpus IMPL=$impl"
            sbatch --cpus-per-task=${cpus} \
                   -t 5:0:0 \
                   -p plgrid \
                   -o "report-zeus-$cpus-$impl.out" \
                   -e "error-zeus-$cpus-$impl.out" \
                   ./${impl}
        done
    done
done
