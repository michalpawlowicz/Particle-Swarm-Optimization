package pso;

import java.util.Random;
import java.util.function.Function;

public class Particle {
    private Vector position;
    private Vector velocity;
    private Vector bestKnownPosition;

    private Particle() {}

    public Vector getPosition() {
        try {
            return this.position.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void initRandom(int dimension, int min, int max) throws CloneNotSupportedException {
        this.position = Vector.random(dimension, min, max);
        this.velocity = Vector.random(dimension, (-1) * Math.abs(max - min), Math.abs(max - min));
        this.bestKnownPosition = this.position.clone();
    }

    public static Particle createRandomParticle(int dimension, int min, int max) {
        try {
            var p = new Particle();
            p.initRandom(dimension, min, max);
            return p;
        } catch (CloneNotSupportedException e) {
            // hehe
            e.printStackTrace();
            return null;
        }
    }

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

    public void updatePosition() {
        this.position.map((i, xi) -> {
            return xi + this.velocity.get(i);
        });
    }

    public Vector getBestPosition() {
       return bestKnownPosition;
    }

    public void updateBestPosition() {
        try {
            this.bestKnownPosition = this.position.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }
}
