package pso;

import javax.swing.plaf.basic.BasicOptionPaneUI;
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
    public static Swarm createSwarm(Function<Vector, Double> fn, Integer size, Integer dimension, double omega, double phiP, double phiG, Domain domain) {
        var swarm = new Swarm();
        swarm.initSwarm(size, dimension, fn);
        return swarm;
    }

    public void run(Function<Double, Boolean> predicate) {
        var omega = 0.5;
        var phi_p = 0.9;
        var phi_g = 0.9;
        var it = 0;
        while(!predicate.apply(fn.apply(this.globalBestKnowPosition))) {
            if(it % 100 == 0) {
                System.out.println("Iteration " + it + " : " + this.globalBestKnowPosition + " : fitness -> " + fn.apply(this.globalBestKnowPosition));
            }
            ++it;
            for(var particle: particleList) {
                particle.updateVelocity(omega, phi_p, phi_g, globalBestKnowPosition);
                particle.updatePosition();
                Double currentFitness = this.fn.apply(particle.getPosition()); // TODO copy
                if(currentFitness < this.fn.apply(particle.getBestPosition())) {
                    particle.updateBestPosition();
                    if(currentFitness < this.fn.apply(this.globalBestKnowPosition)) {
                        this.globalBestKnowPosition = particle.getPosition(); // TODO and copy again
                    }
                }
            }
        }
        System.out.println("Last Iteration " + it + " : " + this.globalBestKnowPosition + " : fitness -> " + fn.apply(this.globalBestKnowPosition));
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
        this.globalBestKnowPosition = Vector.random(dimension, -500, 500);
        IntStream.range(0, particlesCount).forEach(i -> {
            var particle = Particle.createRandomParticle(dimension, -500, 500);
            this.particleList.add(particle);
            var particleFitness = particle.apply(fn);
            if(particleFitness < globalBestKnowFitness) {
                globalBestKnowFitness = particleFitness;
                globalBestKnowPosition = particle.getPosition(); // TODO copy?
            }
        });
    }

    private Swarm() {}

    public class Builder {
        private double omega = 0.1;
        private double phi_p = 0.1;
        private double phi_g = 0.1;
        private int particleCount = 1000;

        private int vDimension;
        private Function<Vector, Double> fn;

        private Domain domain;

        public Builder setOmega(double omega) {
            this.omega = omega;
            return this;
        }

        public Builder setPhiP(double phi_p) {
            this.phi_p = phi_p;
            return this;
        }

        public Builder setPhiG(double phi_g) {
            this.phi_g = phi_g;
            return this;
        }

        public Builder setParticleCount(int particleCount) {
            this.particleCount = particleCount;
            return this;
        }

        public Builder setVDimension(int vDimension) {
            this.vDimension = vDimension;
            return this;
        }

        public Builder setFn(Function<Vector, Double> fn) {
            this.fn = fn;
            return this;
        }

        public Builder setDomain(Domain domain) {
            this.domain = domain;
            return this;
        }
    }
}
