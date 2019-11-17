package pl.edu.agh.pso.akka.message;

import lombok.Data;
import pl.edu.agh.pso.Vector;

@Data
public class EndSolution {
    private final double fitness;
    private final Vector position;

    public boolean isBetterSolutionThan(EndSolution otherSolution) {
        return otherSolution == null || this.fitness < otherSolution.fitness;
    }
}
