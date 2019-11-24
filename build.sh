mvn compile
mvn package
mv target/pso-1.0-SNAPSHOT-jar-with-dependencies.jar target/pso.jar

mvn compile
mvn package -Pakka
mv target/pso-1.0-SNAPSHOT-jar-with-dependencies.jar target/pso_akka.jar
