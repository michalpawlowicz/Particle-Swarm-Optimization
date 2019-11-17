package pl.edu.agh.pso;

import pl.edu.agh.pso.benchmark.Ackley;
import pl.edu.agh.pso.benchmark.Griewank;
import pl.edu.agh.pso.benchmark.Rosenbrock;
import pl.edu.agh.pso.benchmark.Schwefel;

import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final int threadsCount;
        if (args.length < 1) {
            System.out.println("Setting threads number to 4");
            threadsCount = 4;
        } else {
            threadsCount = Integer.parseInt(args[0]);
        }

        final var particlesCount = 16;
        final var dimension = 100;
        final var iterMax = 5e5;

        final var omegaMin = 0.4;
        final var omegaMax = 1.4;
        final var phi_1 = 0.5;
        final var phi_2 = 2.5;
        var swarm = Swarm.builder()
                .ff(Schwefel.build())
                .particlesCount(particlesCount)
                .threadsCount(threadsCount)
                .ffDimension(dimension)
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
        swarm.run((i, f) -> {
            if (i % 1000 == 0) {
                System.out.println(i + "/" + f);
            }
            return (iterMax != 0 && i >= iterMax) || Math.abs(f) < 1e-6;
        });
    }
}