package pl.edu.agh.pso;

import akka.actor.AbstractActor;
import akka.actor.Props;

import java.util.Random;
import java.util.function.Function;

public class Particle extends AbstractActor {

    static class BestPositionMessage {
        Vector position;
        double fitness;
        BestPositionMessage(Vector v, double fitness) {
            this.position = v;
            this.fitness = fitness;
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

    private Vector position;
    private Vector velocity;

    private Vector bestKnownPosition;
    private double bestKnownFitness;

    private Function<Vector, Double> fn;

    @Override
    public void preStart() throws Exception {
        super.preStart();
        getContext().parent().tell(new Particle.BestPositionMessage(this.position, this.bestKnownFitness), self());
    }

    @Override
    public void postRestart(Throwable reason) throws Exception {
        // BIG FAT WARNING
        // super.postRestart() calls preStart after every restart by default
        // preStart must be called only once at agent creation
        // do not call super.postRestart in this method
        // leaving empty on purpose
    }

    /**
     * Apply fitness function fn to current particle position in the given domain
     * @return Double which represents current fitness of particle
     */
    private Double apply() {
        return this.fn.apply(position);
    }

    private void updateVelocity(double omega, double psi, Vector gBest) {
        Random random = new Random();
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
                .build();
    }

    private void iterate(StartIteration msg) {
        var omega = 0.1;
        var psi = 0.1;
        updateVelocity(omega, psi, msg.gBest);
        updatePosition();
        var fitness = this.apply();
        if(fitness < this.bestKnownFitness) {
            this.bestKnownFitness = fitness;
            this.bestKnownPosition = new Vector(this.position); // TODO copy?
        }
        context().parent().tell(new BestPositionMessage(bestKnownPosition, bestKnownFitness), self());
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
