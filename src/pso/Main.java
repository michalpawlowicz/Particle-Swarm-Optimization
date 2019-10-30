package pso;

import java.util.function.Function;

public class Main {
    public static void main(String[] args) {
        Function<Vector, Double> fn2 = v -> {
            return (v.get(0) - 71) * (v.get(0) - 71) + (v.get(1) + 21) * (v.get(1) + 21);
        };
        var swarm = Swarm.createSwarm(fn2, 1000000, 2);
        swarm.run(f -> {
            return f < 1e-6;
        });
    }
}