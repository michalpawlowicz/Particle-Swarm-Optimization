if [ -z "$2" ]; then
	echo "> Running with default configuration"
	echo "> Running with $1 threads"
	java -DconfFileName=app.conf.xml -jar target/pso.jar $1
else
	echo "> Running with $2 configuration"
	echo "> Running with $1 threads"
	java -jar -DconfFileName=$2 -jar target/pso.jar $1
fi

