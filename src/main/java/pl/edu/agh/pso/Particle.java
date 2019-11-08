package pl.edu.agh.pso;

import lombok.Builder;
import scala.Tuple2;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public class Particle implements Callable<Optional<Tuple2<Vector, Double>>> {

    private Vector position;
    private Vector velocity;

    private Vector bestKnownPosition;
    private double bestKnownFitness;

    private Vector bestKnowSwarmPosition;

    private final Function<Vector, Double> ff;

    private final Domain searchDomain;

    private int iteration;

    private ParametersContainer parametersContainer;

    public Optional<Tuple2<Vector, Double>> iterate() {
        this.updateVelocity(parametersContainer.getOmega(this.iteration),
                            parametersContainer.getPhi_1(),
                            parametersContainer.getPhi_2(),
                            this.bestKnowSwarmPosition);
        this.updatePosition();
        if (this.searchDomain.feasible(this.position)) {
            final var fitness = this.apply();
            if (fitness < this.bestKnownFitness) {
                this.bestKnownFitness = fitness;
                this.bestKnownPosition = new Vector(this.position);
                return Optional.of(this.getSolution());
            }
        }
        return Optional.empty();
    }

    public Tuple2<Vector, Double> getSolution() {
        return Tuple2.apply(new Vector(this.bestKnownPosition), this.bestKnownFitness);
    }

    public void setBestKnowSwarmPosition(final int iteration, final Vector v) {
        this.iteration = iteration;
        this.bestKnowSwarmPosition = v;
    }

    private Double apply() {
        return this.ff.apply(this.position);
    }

    private void updateVelocity(final double omega, final double phi_1, final double phi_2, final Vector gBest) {
        if (!this.position.allMatch(searchDomain::feasible)) {
            this.velocity.map(d -> 0.002);
        }
        this.velocity.map((i, vi) -> {
            var rp = ThreadLocalRandom.current().nextDouble();
            var rg = ThreadLocalRandom.current().nextDouble();
            return omega * vi + phi_1 * rp * (this.bestKnownPosition.get(i) - this.position.get(i)) + phi_2 * rg * (gBest.get(i) - this.position.get(i));
        });
    }

    private void updatePosition() {
        this.position.map((i, xi) -> xi + this.velocity.get(i));
    }

    @Builder
    public Particle(Vector position, Vector velocity, final Function<Vector, Double> ff, final Domain searchDomain, final ParametersContainer parametersContainer) {
        this.position = position;
        this.velocity = velocity;
        this.ff = ff;
        this.bestKnownFitness = ff.apply(this.position);
        this.bestKnownPosition = new Vector(this.position);
        this.searchDomain = searchDomain;
        this.parametersContainer = parametersContainer;
    }

    @Override
    public Optional<Tuple2<Vector, Double>> call() {
        return iterate();
    }
}
