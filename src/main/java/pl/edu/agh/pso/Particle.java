package pl.edu.agh.pso;

import scala.Tuple2;

import java.util.Optional;
import java.util.Random;
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


    public Optional<Tuple2<Vector, Double>> iterate() {
        // @see https://pdfs.semanticscholar.org/a4ad/7500b64d70a2ec84bf57cfc2fedfdf770433.pdf
        //final var omega = -0.2089;
        final var omega = 0.8089;
        final var phi_1 = -0.0787;
        final var phi_2 = 3.7637;
        this.updateVelocity(omega, phi_1, phi_2, this.bestKnowSwarmPosition);
        this.updatePosition();
        if(this.searchDomain.feasible(this.position)) {
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

    public void setBestKnowSwarmPosition(final Vector v) {
        this.bestKnowSwarmPosition = v;
    }

    private Double apply() {
        return this.ff.apply(this.position);
    }

    private void updateVelocity(final double omega, final double phi_1, final double phi_2, final Vector gBest) {
        if(!this.position.allMatch(searchDomain::feasible)) {
            this.velocity.map(d -> 0.004);
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

    private Particle(Vector position, Vector velocity, Function<Vector, Double> ff, Domain searchDomain) {
        this.position = position;
        this.velocity = velocity;
        this.ff = ff;
        this.bestKnownFitness = ff.apply(this.position);
        this.bestKnownPosition = new Vector(this.position);
        this.searchDomain = searchDomain;
    }

    @Override
    public Optional<Tuple2<Vector, Double>> call() throws Exception {
        return iterate();
    }

    public static class builder {
        private Vector position;
        private Vector velocity;
        private Function<Vector, Double> ff;
        private Domain domain;

        public Particle build() {
            return new Particle(position, velocity, ff, domain);
        }

        public builder setPosition(Vector v) {
            this.position = v;
            return this;
        }

        public builder setVelocity(Vector v) {
            this.velocity = v;
            return this;
        }

        public builder setFf(Function<Vector, Double> ff) {
            this.ff = ff;
            return this;
        }

        public builder setDomain(Domain domain) {
            this.domain = domain;
            return this;
        }
    }
}
