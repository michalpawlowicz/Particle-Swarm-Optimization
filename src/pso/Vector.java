package pso;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Vector implements Cloneable {
    private int size;
    private double[] xs;

    @Override
    public String toString() {
        return "Vector{" + "xs=" + Arrays.toString(xs) + '}';
    }

    private Vector(int size) {
        this.size = size;
        this.xs = new double[size];
    }

    public double get(int i) {
        return this.xs[i];
    }

    private void initRandom(int min, int max) {
        IntStream.range(0, this.size).forEach(i -> { this.xs[i] = ThreadLocalRandom.current().nextDouble(min, max); });
    }

    public static Vector random(int size, int min, int max) {
        var v = new Vector(size);
        v.initRandom(min, max);
        return v;
    }

    public double[] doubles() {
        return this.xs;
    }

    @Override
    protected Vector clone() throws CloneNotSupportedException {
        Vector v = (Vector)super.clone();
        v.size = this.size;
        v.xs = this.xs.clone();
        return v;
    }

    public void map(BiFunction<Integer, Double, Double> fn) {
        IntStream.range(0, this.size).forEach(i -> {
            this.xs[i] = fn.apply(i, this.xs[i]);
        });
    }

    public List<Double> asList() {
        return Arrays.stream(xs)
                .boxed()
                .collect(Collectors.toList());
    }
}
