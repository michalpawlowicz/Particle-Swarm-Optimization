package pl.edu.agh.pso.akka.messages;

import lombok.Data;
import pl.edu.agh.pso.Vector;

@Data
public class Solution {
    final private Double fitness;
    final private Integer iteration;
    final private Vector position;
}
