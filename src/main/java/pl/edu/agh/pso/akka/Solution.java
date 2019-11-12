package pl.edu.agh.pso.akka;

import lombok.Builder;
import lombok.Data;
import pl.edu.agh.pso.Vector;

@Data
@Builder
class Solution {
    final Double fitness;
    final Integer iteration;
    final Vector position;
}
