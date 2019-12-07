CPUS=$(seq 1 24)
RUNS=$(seq 1 10)
IMPLS=(seq.sh akka.sh)
#CONFS=(d1024p128 d1024p192 d1024p256 d1024p64 d512p128 d512p192 d512p256 d512p64)
CONFS=(d512p64)

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
