package pl.edu.agh.pso.benchmark;

import pl.edu.agh.pso.Vector;

import java.util.function.Function;

public class Griewank {
    public static Function<Vector, Double> build() {
        return v -> {
            final var s = v.reduce((acc, d) -> acc + Math.pow(d, 2) / 4000);
            double acc = 1.0;
            for (int i = 0; i < v.size(); ++i) { acc *= Math.cos(v.get(i) / Math.sqrt(i + 1)); }
            return s - acc + 1;
        };
    }
}
