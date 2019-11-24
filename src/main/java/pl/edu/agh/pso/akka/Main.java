package pl.edu.agh.pso.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import pl.edu.agh.pso.ImmutableDomain;
import pl.edu.agh.pso.ImmutableParametersContainer;
import pl.edu.agh.pso.benchmark.Schwefel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Properties;

public class Main {
    private static String CONF_FILENAME_PARAM = "confFilename";
    private static String DEFAULT_CONF_FILENAME = "app.conf.xml";

    public static void main(String[] args) {

        Properties prop = new Properties();
        String propFileName = System.getProperty(CONF_FILENAME_PARAM, DEFAULT_CONF_FILENAME);
        System.out.println(propFileName);
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(propFileName);
            prop.loadFromXML(inputStream);
        } catch (FileNotFoundException e) {
            System.out.println(propFileName + " file not found");
            return;
        } catch (IOException e) {
            System.out.println("Could not read property file");
        }

        var x = prop.getProperty("nodes");
        final var particlesCount = new BigDecimal(prop.getProperty("particlesCount")).intValue();
        final var dimension = new BigDecimal(prop.getProperty("dimension")).intValue();
        final var iterMax = new BigDecimal(prop.getProperty("iterMax")).intValue();
        final var omegaMin = Double.parseDouble(prop.getProperty("omegaMin"));
        final var omegaMax = Double.parseDouble(prop.getProperty("omegaMax"));
        final var phi_1 = Double.parseDouble(prop.getProperty("phi_1"));
        final var phi_2 = Double.parseDouble(prop.getProperty("phi_2"));
        final var iterationInterval = Integer.parseInt(prop.getProperty("iterationInterval"));
        final var lambda = Double.parseDouble(prop.getProperty("lambda"));
        final var lowerBound = Integer.parseInt(prop.getProperty("lowerBound"));
        final var higherBound = Integer.parseInt(prop.getProperty("higherBound"));
        var startMsg = ImmutableInit.builder()
                .ff(Schwefel.build())
                .particlesCount(particlesCount)
                .ffDimension(dimension)
                .domain(ImmutableDomain.builder()
                        .lowerBound(lowerBound)
                        .higherBound(higherBound)
                        .build())
                .parameters(ImmutableParametersContainer.builder()
                        .phi_1(phi_1)
                        .phi_2(phi_2)
                        .omegaMin(omegaMin)
                        .omegaMax(omegaMax)
                        .step((omegaMax - omegaMin) / iterMax)
                        .build())
                .endCondition((i, f) -> {
                    return (iterMax != 0 && i >= iterMax) || Math.abs(f) < lambda;
                })
                .iterationInterval(iterationInterval)
                .build();

        ActorSystem system = ActorSystem.create("pso");
        ActorRef swarm = system.actorOf(SwarmActor.props());
        swarm.tell(startMsg, null);
    }
}
