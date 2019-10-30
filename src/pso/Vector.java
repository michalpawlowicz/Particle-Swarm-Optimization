package pso;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.stream.IntStream;

public class Vector {

    private int size;

    private double[] xs;

    /**
     * Vector's copy constructor
     * @param o Vector instance to be copied
     */
    public Vector(Vector o) {
        this.size = o.size;
        this.xs = new double[o.xs.length];
        System.arraycopy(o.xs, 0, this.xs, 0, o.xs.length);
    }

    /**
     * Map fn over vector's entries
     * @param fn Map function, takes Integer i, current index of vector and Double, value under i as parameters
     */
    public void map(BiFunction<Integer, Double, Double> fn) {
        IntStream.range(0, this.size).forEach(i -> {
            this.xs[i] = fn.apply(i, this.xs[i]);
        });
    }

    public double reduce(DoubleBinaryOperator fn, double start) {
        return Arrays.stream(this.xs).reduce(start, fn);
    }

    /**
     * Returns ith element
     * @param i element's index
     * @return Element under ith index
     */
    public double get(int i) {
        return this.xs[i];
    }

    /**
     * Initialize random vector from given range
     * @param size size of returned vector
     * @param min lower bound
     * @param max higher bound
     * @return Vector's instance
     */
    public static Vector random(int size, int min, int max) {
        var v = new Vector(size);
        v.initRandom(min, max);
        return v;
    }

    public int length() {
        return this.size;
    }

    private void initRandom(int min, int max) {
        IntStream.range(0, this.size).forEach(i -> { this.xs[i] = ThreadLocalRandom.current().nextDouble(min, max); });
    }

    private Vector(int size) {
        this.size = size;
        this.xs = new double[size];
    }

    @Override
    public String toString() {
        return "Vector{" + "xs=" + Arrays.toString(xs) + '}';
    }
}
