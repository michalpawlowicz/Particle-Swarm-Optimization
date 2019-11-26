package pl.edu.agh.pso.benchmark;

import pl.edu.agh.pso.Vector;

import java.util.function.Function;

public class Ackley {
    public static Function<Vector, Double> build() {
        return v -> {
            final double a = 20.0;
            final double b = 0.2;
            final double c = 2 * Math.PI;
            final double p_1 = -a*Math.exp(-b * Math.sqrt(1.0 / v.size() * v.reduce((acc, x) -> acc + x*x)));
            final double p_2 = Math.exp(1.0/v.size()*v.reduce((acc, x) -> acc + Math.cos(c*x)));
            return p_1 - p_2 + a + Math.exp(1.0);
        };
    }
}
