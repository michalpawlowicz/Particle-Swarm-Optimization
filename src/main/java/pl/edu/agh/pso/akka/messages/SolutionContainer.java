package pl.edu.agh.pso.akka.messages;

import lombok.Data;
import pl.edu.agh.pso.Vector;

@Data
public class SolutionContainer {
    final private Vector position;
    final private Double fitness;
    final private Integer iteration;
}
