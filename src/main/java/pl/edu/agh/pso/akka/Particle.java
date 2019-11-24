package pl.edu.agh.pso.akka;

import lombok.Builder;
import pl.edu.agh.pso.AbstractParticle;
import pl.edu.agh.pso.Domain;
import pl.edu.agh.pso.ParametersContainer;
import pl.edu.agh.pso.Vector;

import java.util.function.Function;

public class Particle extends AbstractParticle {
    @Builder
    public Particle(Vector position, Vector velocity, Function<Vector, Double> ff, Domain searchDomain, ParametersContainer parametersContainer) {
        super(position, velocity, ff, searchDomain, parametersContainer);
    }
}
