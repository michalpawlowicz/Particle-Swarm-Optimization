package pl.edu.agh.pso.benchmark;

import pl.edu.agh.pso.Vector;

import java.util.function.Function;

public class Rosenbrock {
    public static Function<Vector, Double> build() {
        return v -> {
            double acc = 0.0;
            for (int i = 0; i < v.size() - 1; ++i) {
                acc += 100 * Math.pow((v.get(i + 1) - Math.pow(v.get(i), 2)), 2) + Math.pow((v.get(i) - 1), 2);
            }
            return acc;
        };
    }
}
