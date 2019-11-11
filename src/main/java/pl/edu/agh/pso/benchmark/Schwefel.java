package pl.edu.agh.pso.benchmark;

import pl.edu.agh.pso.Vector;

import java.util.function.Function;

public class Schwefel {
    public static Function<Vector, Double> build() {
        return v -> 418.9829 * v.size() - v.reduce((acc, el) -> acc + el * Math.sin(Math.sqrt(Math.abs(el))));
    }
}
