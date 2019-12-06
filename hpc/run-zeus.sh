CPUS=$(seq 1 24)
RUNS=$(seq 1 10)
IMPLS=(seq.sh akka.sh)

rm -rf benchmarks
mkdir benchmarks

echo "> Running"
for cpus in ${CPUS}; do
    for run in ${RUNS}; do
        for impl in ${IMPLS[*]}; do
            echo "Scheduling CPUS=$cpus IMPL=$impl RUN=$run"
            sbatch --cpus-per-task=${cpus} \
                   -t 5:0:0 \
                   -p plgrid \
                   -J "pso-$cpus-$impl-$run" \
                   -o "benchmarks/report-zeus-$cpus-$impl.out" \
                   -e "benchmarks/error-zeus-$cpus-$impl.out" \
                   ./${impl}
        done
    done
done
