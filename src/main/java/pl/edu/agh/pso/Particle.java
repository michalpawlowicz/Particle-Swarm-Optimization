package pl.edu.agh.pso;

import lombok.Builder;
import scala.Tuple2;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public class Particle extends AbstractParticle implements Callable<Optional<Tuple2<Vector, Double>>> {
    private Vector gBest;
    private int iteration;

    public void setBestKnowSwarmPosition(final int iteration, final Vector v) {
        this.iteration = iteration;
        this.gBest = v;
    }

    @Builder
    public Particle(Vector position, Vector velocity, final Function<Vector, Double> ff, final Domain searchDomain, final ParametersContainer parametersContainer) {
        super(position, velocity, ff, searchDomain, parametersContainer);
    }

    @Override
    public Optional<Tuple2<Vector, Double>> call() {
        return iterate(iteration, gBest);
    }
}
