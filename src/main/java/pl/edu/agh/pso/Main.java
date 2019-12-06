package pl.edu.agh.pso;

import pl.edu.agh.pso.benchmark.Ackley;
import pl.edu.agh.pso.benchmark.Griewank;
import pl.edu.agh.pso.benchmark.Rosenbrock;
import pl.edu.agh.pso.benchmark.Schwefel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import pl.edu.agh.pso.ImmutableParametersContainer;
import pl.edu.agh.pso.ImmutableDomain;

public class Main {

    private static String CONF_FILENAME_PARAM = "confFileName";
    private static String DEFAULT_CONF_FILENAME = "app.conf.xml";

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final int threadsCount;
        if (args.length < 1) {
            System.out.println("Setting threads number to 4");
            threadsCount = 4;
        } else {
            threadsCount = Integer.parseInt(args[0]);
        }

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
            return;
        }

        final int particlesCount = new BigDecimal(prop.getProperty("particlesCount")).intValue();
        final int dimension = new BigDecimal(prop.getProperty("dimension")).intValue();
        final int iterMax = new BigDecimal(prop.getProperty("iterMax")).intValue();

        final double omegaMin = Double.parseDouble(prop.getProperty("omegaMin"));
        final double omegaMax = Double.parseDouble(prop.getProperty("omegaMax"));
        final double phi_1 = Double.parseDouble(prop.getProperty("phi_1"));
        final double phi_2 = Double.parseDouble(prop.getProperty("phi_2"));
        final double lambda = Double.parseDouble(prop.getProperty("lambda"));
        final int lowerBound = Integer.parseInt(prop.getProperty("lowerBound"));
        final int higherBound = Integer.parseInt(prop.getProperty("higherBound"));

        Swarm swarm = Swarm.builder()
                .ff(Schwefel.build())
                .particlesCount(particlesCount)
                .threadsCount(threadsCount)
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
                .build();
        swarm.run((i, f) -> {
            return (iterMax != 0 && i >= iterMax) || Math.abs(f) < lambda;
        });
    }
}
