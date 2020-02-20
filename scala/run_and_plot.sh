sbt clean assembly ; dir=directory; mkdir $dir; for i in {1..8}; do  python3 run.py $i ./application.properties $dir ;   done && python3 plot_speedup.py $dir
