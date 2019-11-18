package pl.edu.agh.pso.akka.messages;

import lombok.Data;
import pl.edu.agh.pso.Vector;

@Data
public class FinalSolution {
    private final double fitness;
    private final Vector position;

    public boolean isBetterSolutionThan(FinalSolution otherSolution) {
        return otherSolution == null || this.fitness < otherSolution.fitness;
    }
}
