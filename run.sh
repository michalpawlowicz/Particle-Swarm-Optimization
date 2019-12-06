if [ -z "$2" ]; then
	echo "> Running with default configuration"
	echo "> Running with $1 threads"
	java -jar -DconfFileName=app.conf.xml target/pso.jar $1
else
	echo "> Running with $2 configuration"
	echo "> Running with $1 threads"
	java -jar -DconfFileName=$2 target/pso.jar $1
fi

