package pl.edu.agh.pso.messages;

import org.immutables.value.Value;
import pl.edu.agh.pso.Vector;

@Value.Immutable
public abstract class Activate {
    public abstract Vector getBestPosition();
    public abstract int getIteration();
}
