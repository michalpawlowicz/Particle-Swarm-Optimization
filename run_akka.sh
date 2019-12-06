if [ -z $1 ]; then
	echo "> Running with default config file"
	java -DconfFileName=app.conf.xml -jar target/pso_akka.jar
else
	echo "> Running with $1 configuration"
	java -DconfFileName=$1 -jar target/pso_akka.jar
fi
