package pl.edu.agh.pso.v2;

import pl.edu.agh.pso.ImmutableDomain;
import pl.edu.agh.pso.ImmutableParametersContainer;
import pl.edu.agh.pso.benchmark.Schwefel;

import java.time.Duration;
import java.time.Instant;
import java.util.function.BiFunction;

public class MainV2 {
    public static void main(String[] args) throws InterruptedException {
        final int threadsCount;
        if (args.length < 1) {
            threadsCount = 4;
            System.out.println("Setting threads number to: " + threadsCount);
        } else {
            threadsCount = Integer.parseInt(args[2]);
        }

        final var particlesCount = 20;
        final var dimension = 100;
        final var iterMax = 5e5;

        final var omegaMin = 0.4;
        final var omegaMax = 1.4;
        final var phi_1 = 0.5;
        final var phi_2 = 2.5;

        BiFunction<Integer, Double, Boolean> endCondition = (i, f) -> {
            if (i % 1000 == 0) {
                System.out.println(i + "/" + f);
            }
            return (i >= iterMax) || Math.abs(f) < 1e-4;
        };
        var swarm = SwarmV2.builder()
                .ff(Schwefel.build())
                .particlesCount(particlesCount)
                .threadsCount(threadsCount)
                .ffDimension(dimension)
                .endCondition(endCondition)
                .domain(ImmutableDomain.builder()
                        .lowerBound(-500)
                        .higherBound(500)
                        .build())
                .parameters(ImmutableParametersContainer.builder()
                        .phi_1(phi_1)
                        .phi_2(phi_2)
                        .omegaMin(omegaMin)
                        .omegaMax(omegaMax)
                        .step((omegaMax - omegaMin) / iterMax)
                        .build())
                .build();

        Instant start = Instant.now();
        swarm.run();
        long elapsedTime = Duration.between(start, Instant.now()).toMillis();
        System.out.println("Elapsed time: " + elapsedTime);
    }
}