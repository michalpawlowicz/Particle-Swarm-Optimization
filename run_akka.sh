if [ -z $1 ]; then
	echo "> Running with default config file"
	java -jar -DconfFileName=app.conf.xml target/pso_akka.jar
else
	echo "> Running with $1 configuration"
	java -jar -DconfFileName=$1 target/pso_akka.jar
fi
