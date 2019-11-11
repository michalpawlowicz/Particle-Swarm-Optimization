package pl.edu.agh.pso;

import org.immutables.value.Value;

@Value.Immutable
public abstract class ParametersContainer {
    public abstract double getOmegaMax();

    public abstract double getOmegaMin();

    public abstract double getStep();

    public abstract double getPhi_1();

    public abstract double getPhi_2();

    public double getOmega(final int iteration){
        final double omega = getOmegaMax() - iteration * getStep();
        return (Double.compare(omega, getOmegaMin()) < 0) ? getOmegaMin() : omega;
    }
}
