package pl.edu.agh.pso.messages;

import org.immutables.value.Value;
import pl.edu.agh.pso.Vector;

@Value.Immutable
public abstract class Complete {
    abstract Vector getPosition();
    abstract double getFitness();
}
