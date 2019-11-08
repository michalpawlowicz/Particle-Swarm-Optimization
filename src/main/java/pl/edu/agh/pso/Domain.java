package pl.edu.agh.pso;

import org.immutables.value.Value;

@Value.Immutable
public abstract class Domain {
    public abstract int getLowerBound();

    public abstract int getHigherBound();

    public boolean feasible(Vector v) {
        return v.allMatch(this::feasible);
    }

    public boolean feasible(double xi) {
        return Double.compare(xi, getLowerBound()) > 0 && Double.compare(xi, getHigherBound()) < 0;
    }
}
