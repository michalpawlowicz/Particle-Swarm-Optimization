package pso;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

public class Swarm {
    private List<Particle> particleList;
    private Function<Vector, Double> fn;
    private Double gBestKnowFitness;
    private Vector gBestKnowPosition;

    private Swarm() {}

    private void initSwarm(Integer size, Integer dimension, Function<Vector, Double> fn) {
        this.fn = fn;
        this.particleList = new LinkedList<>();
        this.gBestKnowFitness = Double.MAX_VALUE;
        this.gBestKnowPosition = Vector.random(dimension, -10000, 1000);

        IntStream range = IntStream.range(0, size);
        range.forEach(i -> {
            var particle = Particle.createRandomParticle(dimension, -1000, 1000);
            this.particleList.add(particle);
            var newFitness = particle.apply(fn);
            if(newFitness < gBestKnowFitness) {
               gBestKnowFitness = newFitness;
               gBestKnowPosition = particle.getPosition(); // TODO
            }
        });
    }

    public static Swarm createSwarm(Function<Vector, Double> fn, Integer size, Integer dimension) {
        var swarm = new Swarm();
        swarm.initSwarm(size, dimension, fn);
        return swarm;
    }

    public void run(Function<Double, Boolean> predicate) {
        var omega = 0.1;
        var psi = 0.1;
        var it = 0;
        while(!predicate.apply(fn.apply(this.gBestKnowPosition))) {
            System.out.println("Iteration " + it++ + " : " + this.gBestKnowPosition + " : fitness -> " + fn.apply(this.gBestKnowPosition));
            for(var particle: particleList) {
                particle.updateVelocity(omega, psi, gBestKnowPosition);
                particle.updatePosition();
                Double currentFitness = this.fn.apply(particle.getPosition());
                if(currentFitness < this.fn.apply(particle.getBestPosition())) {
                    particle.updateBestPosition();
                    if(currentFitness < this.fn.apply(this.gBestKnowPosition)) {
                        this.gBestKnowPosition = particle.getPosition();
                    }
                }
            }
        }
    }
}
