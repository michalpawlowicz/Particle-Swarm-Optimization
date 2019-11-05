package pl.edu.agh.pso;

import pl.edu.agh.pso.ImmutableDomain;

import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class Main {
    public static void main(String[] args) {
        final var dimention = 100;
        Function<Vector, Double> fn2 = v -> (v.get(0) - 71) * (v.get(0) - 71) + (v.get(1) + 21) * (v.get(1) + 21);
        Function<Vector, Double> schwefel = v -> {
            return 418.9829*dimention - v.reduce((acc, el) -> acc + el * Math.sin(Math.sqrt(Math.abs(el))));
        };
        var swarm = new Swarm.builder()
                .numberOfParticles(100)
                .numberOfThreads(16)
                .fitnessFunction(schwefel, dimention)
                .parametersCallbackFunction(null)
                .setDomain(ImmutableDomain.builder().lowerBound(-500).higherBound(500).build())
                .build();
        try {
            swarm.run(f -> {
                return Math.abs(f) < 1e-6;
            });
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}