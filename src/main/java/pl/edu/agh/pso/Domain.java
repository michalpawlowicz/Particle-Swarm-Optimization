package pl.edu.agh.pso;

import org.immutables.value.Value;

@Value.Immutable
public abstract class Domain {
    public abstract int getLowerBound();
    public abstract int getHigherBound();
    public double correct(double x) {
        if(x > getLowerBound() && x < getHigherBound()) {
            return x;
        } else if(x >= getHigherBound()) {
            return getHigherBound();
        } else {
            return getLowerBound();
        }
    }
}
