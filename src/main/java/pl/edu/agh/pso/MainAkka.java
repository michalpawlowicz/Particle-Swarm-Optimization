package pl.edu.agh.pso;

import pl.edu.agh.pso.akka.InitData;
import pl.edu.agh.pso.akka.SwarmAkka;
import pl.edu.agh.pso.benchmark.Schwefel;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;

public class MainAkka {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final int threadsCount;
        AlgorithmVersion algorithmVersion = AlgorithmVersion.V1;
        if (args.length < 1) {
            System.out.println("Setting threads number to 4");
            threadsCount = 4;
        } else {
            threadsCount = Integer.parseInt(args[2]);

        }
        if (args.length >= 2) {
            algorithmVersion = AlgorithmVersion.DISTRIBUTED_PSO;
        }

        System.out.println("Version of algorithm: " + algorithmVersion);

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
            return (i >= iterMax) || Math.abs(f) < 1;
        };

        InitData initData = InitData.builder().ff(Schwefel.build())
                .particlesCount(particlesCount)
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
        SwarmAkka.run(initData);
        long elapsedTime = Duration.between(start, Instant.now()).toMillis();
        System.out.println("Elapsed time: " + elapsedTime);
    }
}