package pl.edu.agh.pso.benchmark;

import pl.edu.agh.pso.Vector;

import java.util.function.Function;

public class Ackley {
    public static Function<Vector, Double> build() {
        return v -> {
            final var a = 20;
            final var b = 0.2;
            final var c = 2 * Math.PI;
            final var p_1 = -a*Math.exp(-b * Math.sqrt(1.0 / v.size() * v.reduce((acc, x) -> acc + x*x)));
            final var p_2 = Math.exp(1.0/v.size()*v.reduce((acc, x) -> acc + Math.cos(c*x)));
            return p_1 - p_2 + a + Math.exp(1.0);
        };
    }
}
