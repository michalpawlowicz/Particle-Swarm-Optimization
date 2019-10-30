package pso;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

public class Swarm {

    private List<Particle> particleList;

    private Function<Vector, Double> fn;

    private Double globalBestKnowFitness;

    private Vector globalBestKnowPosition;

    /**
     * Create swarm
     * @param fn Function to be optimized
     * @param size Size of swarm (particles count)
     * @param dimension Dimension of fn's input vector
     * @return Initialized Swarm instance
     */
    public static Swarm createSwarm(Function<Vector, Double> fn, Integer size, Integer dimension) {
        var swarm = new Swarm();
        swarm.initSwarm(size, dimension, fn);
        return swarm;
    }

    public void run(Function<Double, Boolean> predicate) {
        var omega = 0.1;
        var psi = 0.1;
        var it = 0;
        while(!predicate.apply(fn.apply(this.globalBestKnowPosition))) {
            System.out.println("Iteration " + it++ + " : " + this.globalBestKnowPosition + " : fitness -> " + fn.apply(this.globalBestKnowPosition));
            for(var particle: particleList) {
                particle.updateVelocity(omega, psi, globalBestKnowPosition);
                particle.updatePosition();
                Double currentFitness = this.fn.apply(particle.getPosition());
                if(currentFitness < this.fn.apply(particle.getBestPosition())) {
                    particle.updateBestPosition();
                    if(currentFitness < this.fn.apply(this.globalBestKnowPosition)) {
                        this.globalBestKnowPosition = particle.getPosition();
                    }
                }
            }
        }
    }

    /**
     * Swarm initialization
     * @param particlesCount Number of particles in swarm
     * @param dimension dimension of input vector to function fn
     * @param fn Function to be optimised
     */
    private void initSwarm(Integer particlesCount, Integer dimension, Function<Vector, Double> fn) {
        this.fn = fn;
        this.particleList = new LinkedList<>();
        this.globalBestKnowFitness = Double.MAX_VALUE;
        this.globalBestKnowPosition = Vector.random(dimension, -10000, 1000);
        IntStream.range(0, particlesCount).forEach(i -> {
            var particle = Particle.createRandomParticle(dimension, -1000, 1000);
            this.particleList.add(particle);
            var particleFintess = particle.apply(fn);
            if(particleFintess < globalBestKnowFitness) {
                globalBestKnowFitness = particleFintess;
                globalBestKnowPosition = particle.getPosition(); // TODO copy?
            }
        });
    }

    private Swarm() {}
}
