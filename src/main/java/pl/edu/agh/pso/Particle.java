package pl.edu.agh.pso;

import akka.actor.AbstractActor;
import akka.actor.Props;

import java.util.Random;
import java.util.function.Function;

public class Particle extends AbstractActor {

    static class Response {
        Vector position;
        double fitness;
        Response(Vector v, double fitness) {
            this.position = v;
            this.fitness = fitness;
        }

        @Override
        public String toString() {
            return "Response{" +
                    "position=" + position +
                    ", fitness=" + fitness +
                    '}';
        }
    }

    static class StartIteration {
        Vector gBest;
        Integer iter;
        public StartIteration(Vector gBest, Integer iter) {
            this.gBest = gBest;
            this.iter = iter;
        }
    }

    static class State {}

    private Vector position;
    private Vector velocity;

    private Vector bestKnownPosition;
    private double bestKnownFitness;

    private Function<Vector, Double> fn;

    /**
     * Apply fitness function fn to current particle position in the given domain
     * @return Double which represents current fitness of particle
     */
    private Double apply() {
        return this.fn.apply(position);
    }

    private void updateVelocity(double omega, double psi, Vector gBest) {
        Random random = new Random(); // TODO as member?
        this.velocity.map((i, vi) -> {
            var rp = random.nextDouble();
            var rg = random.nextDouble();
            return omega * vi + psi * rp * (this.bestKnownPosition.get(i) - this.position.get(i)) + omega * rg * (gBest.get(i)  - this.position.get(i));
        });
    }

    /**
     * Update particle's position according to it's actual velocity
     */
    private void updatePosition() {
        this.position.map((i, xi) -> {
            return xi + this.velocity.get(i);
        });
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(StartIteration.class, this::iterate)
                .match(State.class, msg -> {
                    context().sender().tell(new Response(bestKnownPosition, bestKnownFitness), self());
                })
                .build();
    }

    private void iterate(StartIteration msg) {
        var omega = 0.5;
        var psi = 0.1;
        updateVelocity(omega, psi, msg.gBest);
        updatePosition();
        var fitness = this.apply();
        if(fitness < this.bestKnownFitness) {
            this.bestKnownFitness = fitness;
            this.bestKnownPosition = new Vector(this.position);
        }
        context().sender().tell(new Response(bestKnownPosition, bestKnownFitness), self());
    }

    /**
     * Create particle with random position and velocity
     * @param dimension dimension in which particle exists, for instance dimension of fitness function
     * @param lbount lower bound of domain
     * @param hbound higher bound of domain
     * @return instance of particle
     */
    public Particle(int dimension, int lbount, int hbound, Function<Vector, Double> fn) {
        this.position = Vector.random(dimension, lbount, hbound);
        this.velocity = Vector.random(dimension, (-1) * Math.abs(hbound - lbount), Math.abs(hbound - lbount));
        this.bestKnownPosition = new Vector(this.position);
        this.fn = fn;
        this.bestKnownFitness = this.apply();
    }

    public static Props props(int dim, int min, int max, Function<Vector, Double> fn) {
        return Props.create(Particle.class, dim, min, max, fn);
    }
}
