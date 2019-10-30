package pso;

import java.util.function.Function;

public class Main {
    public static void main(String[] args) {
        var dimension = 200;
        Function<Vector, Double> schwefel_function = v -> {
            return v.length() * 418.9829 - v.reduce((acc, xi) -> { return acc + xi*Math.sin(Math.sqrt(Math.abs(xi))); }, 0.0);
        };
        var swarm = Swarm.createSwarm(schwefel_function, 1000, dimension);
        swarm.run(f -> {
            return Math.abs(f) < 1e-6;
        });
    }
}