package pl.edu.agh.pso;

import scala.Tuple2;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class Particle implements Callable<Optional<Tuple2<Vector, Double>>> {

    private Vector position;
    private Vector velocity;

    private Vector bestKnownPosition;
    private double bestKnownFitness;

    private Vector bestKnowSwarmPosition;

    private Function<Vector, Double> ff;

    private Domain searchDomain;

    public Optional<Tuple2<Vector, Double>> iterate() {
        final var omega = 0.8;
        final var psi = 1.8;
        this.updateVelocity(omega, psi, this.bestKnowSwarmPosition);
        this.updatePosition();
        final var fitness = this.apply();
        if(fitness < this.bestKnownFitness) {
            this.bestKnownFitness = fitness;
            this.bestKnownPosition = new Vector(this.position);
            return Optional.of(this.getSolution());
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

    private void updateVelocity(final double omega, final double psi, final Vector gBest) {
        Random random = new Random();
        this.velocity.map((i, vi) -> {
            var rp = random.nextDouble();
            var rg = random.nextDouble();
            return omega * vi + psi * rp * (this.bestKnownPosition.get(i) - this.position.get(i)) + omega * rg * (gBest.get(i)  - this.position.get(i));
        });
    }

    private void updatePosition() {
        this.position.map((i, xi) -> this.searchDomain.correct(xi + this.velocity.get(i)));
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
