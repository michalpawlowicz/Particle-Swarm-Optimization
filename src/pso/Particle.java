package pso;

import java.util.Random;
import java.util.function.Function;

public class Particle {

    private Vector position;

    private Vector velocity;

    private Vector bestKnownPosition;

    /**
     * Current particle's position
     * @return Returns copy of vector representing particles's position
     */
    public Vector getPosition() {
        return new Vector(this.position);
    }

    /**
     * Create particle with random position and velocity
     * @param dimension dimension in which particle exists, for instance dimension of fitness function
     * @param min lower bound of domain
     * @param max higher bound of domain
     * @return instance of particle
     */
    public static Particle createRandomParticle(int dimension, int min, int max) {
        var p = new Particle();
        p.initRandom(dimension, min, max);
        return p;
    }

    /**
     * Apply fintess function fn to current particle position in the given domain
     * @param fn Fitness function
     * @return Double which represents current fitness of particle
     */
    public Double apply(Function<Vector, Double> fn) {
        return fn.apply(position);
    }

    public void updateVelocity(double omega, double psi, Vector gBest) {
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
    public void updatePosition() {
        this.position.map((i, xi) -> {
            return xi + this.velocity.get(i);
        });
    }

    public Vector getBestPosition() {
       return bestKnownPosition;
    }

    public void updateBestPosition() {
        this.bestKnownPosition = new Vector(this.position);
    }

    private Particle() {}

    private void initRandom(int dimension, int min, int max) {
        this.position = Vector.random(dimension, min, max);
        this.velocity = Vector.random(dimension, (-1) * Math.abs(max - min), Math.abs(max - min));
        this.bestKnownPosition = new Vector(this.position);
    }
}
