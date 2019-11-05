package pl.edu.agh.pso;

import pl.edu.agh.pso.ImmutableDomain;

import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        if(args.length < 3) {
            System.out.println("First parameter particlesCount\nSecond parameter dimension of Schwefel function\nThird parameter, number of threads");
            return;
        }
        final var particlesCount = Integer.parseInt(args[0]);
        final var dimension = Integer.parseInt(args[1]);
        final var threadsCount = Integer.parseInt(args[2]);
        Function<Vector, Double> schwefel = v -> 418.9829*dimension - v.reduce((acc, el) -> acc + el * Math.sin(Math.sqrt(Math.abs(el))));
        var swarm = new Swarm.builder()
                .numberOfParticles(particlesCount)
                .numberOfThreads(threadsCount)
                .fitnessFunction(schwefel, dimension)
                .setDomain(ImmutableDomain.builder().lowerBound(-500).higherBound(500).build())
                .build();
        swarm.run(f -> {
            return Math.abs(f) < 1e-6;
        });
    }
}