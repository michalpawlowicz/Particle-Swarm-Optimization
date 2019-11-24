package pl.edu.agh.pso;

import scala.Tuple2;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public class AbstractParticle {
    protected Vector position;
    protected Vector velocity;
    protected Vector bestKnownPosition;
    protected double bestKnownFitness;
    protected final Function<Vector, Double> ff;
    protected final Domain searchDomain;
    protected final ParametersContainer parametersContainer;

    public AbstractParticle(Vector position, Vector velocity, final Function<Vector, Double> ff, final Domain searchDomain, final ParametersContainer parametersContainer) {
        this.position = position;
        this.velocity = velocity;
        this.ff = ff;
        this.searchDomain = searchDomain;
        this.parametersContainer = parametersContainer;
        this.bestKnownPosition = new Vector(this.position);
        this.bestKnownFitness = this.apply();
    }

    protected void updateVelocity(final double omega, final double phi_1, final double phi_2, final Vector gBest) {
        if (!this.position.allMatch(searchDomain::feasible)) {
            this.velocity.map(d -> 0.002);
        }
        this.velocity.map((i, vi) -> {
            var rp = ThreadLocalRandom.current().nextDouble();
            var rg = ThreadLocalRandom.current().nextDouble();
            return omega * vi + phi_1 * rp * (this.bestKnownPosition.get(i) - this.position.get(i)) + phi_2 * rg * (gBest.get(i) - this.position.get(i));
        });
    }

    protected void updatePosition() {
        this.position.map((i, xi) -> xi + this.velocity.get(i));
    }

    protected Double apply() {
        return this.ff.apply(this.position);
    }

    public Optional<Tuple2<Vector, Double>> iterate(final int iteration, final Vector gBest) {
        this.updateVelocity(parametersContainer.getOmega(iteration), parametersContainer.getPhi_1(), parametersContainer.getPhi_2(), gBest);
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
}
