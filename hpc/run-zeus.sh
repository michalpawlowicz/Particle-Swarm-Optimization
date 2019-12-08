CPUS=$(seq 1 24)
RUNS=$(seq 1 10)
IMPLS=(seq.sh akka.sh)
CONFS=(d100p512 d100p256 d50p192 d100p128)

rm -rf benchmarks
mkdir benchmarks

echo "> Running"

for config in ${CONFS[*]}; do
	for cpus in ${CPUS}; do
	    for run in ${RUNS}; do
		for impl in ${IMPLS[*]}; do
		    echo "Scheduling CPUS=$cpus IMPL=$impl RUN=$run with CONF=configs/$config.xml"
		    sbatch --cpus-per-task=${cpus} \
			   -t 5:0:0 \
			   -p plgrid \
			   -J "pso-$cpus-$impl-$config-$run" \
			   -o "benchmarks/report-zeus-$cpus-$impl-$config-$run.out" \
			   -e "benchmarks/error-zeus-$cpus-$impl-$config-$run.out" \
			   --export=ALL,CONFIG=hpc/configs/${config}.xml,RUN=${run} \
			   ./${impl}
		done
	    done
	done
done
