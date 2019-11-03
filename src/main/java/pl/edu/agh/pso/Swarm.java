package pl.edu.agh.pso;

import scala.Tuple2;
import scala.Tuple3;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;

public class Swarm {

    private List<Particle> particleList;

    private Double globalBestKnowFitness;

    private Vector globalBestKnowPosition;

    private ExecutorService executor;

    private int itNum = 0;

    public void run(Function<Double, Boolean> predicate) throws ExecutionException, InterruptedException {
        this.itNum = 0;
        while (!predicate.apply(this.globalBestKnowFitness)) {
            List<Future<Optional<Tuple2<Vector, Double>>>> futures = new LinkedList<>();
            for (var particle : particleList) {
                particle.setBestKnowSwarmPosition(globalBestKnowPosition);
            }
            for (var particle : particleList) {
                futures.add(this.executor.submit(particle));
            }
            for (var result : futures) {
                result.get().ifPresent(this::updateSwarmsBestSolution);
            }
            itNum++;
        }
        this.executor.shutdownNow();
    }

    private void updateSwarmsBestSolution(Tuple2<Vector, Double> solution) {
        if (solution._2 < this.globalBestKnowFitness) {
            this.globalBestKnowFitness = solution._2;
            this.globalBestKnowPosition = solution._1;
            System.out.println("Iteration: " + this.itNum + " Fitness: " + this.globalBestKnowFitness + " position: " + this.globalBestKnowPosition);
        }
    }

    private Swarm(final Integer particlesCount,
                  final Integer threadsCount,
                  Function<Vector, Double> ff,
                  Integer ffDimension,
                  BiFunction<Integer, Double, Tuple3<Double, Double, Double>> pf,
                  Domain domain)
    {
        this.executor = Executors.newFixedThreadPool(threadsCount);
        this.particleList = new LinkedList<>();
        this.globalBestKnowPosition = Vector.random(ffDimension, domain.getLowerBound(), domain.getHigherBound());
        this.globalBestKnowFitness = ff.apply(this.globalBestKnowPosition);
        IntStream.range(0, particlesCount).forEach(i -> {
            var particle = new Particle.builder()
                    .setFf(ff)
                    .setPosition(Vector.random(ffDimension, domain.getLowerBound(), domain.getHigherBound()))
                    .setVelocity(Vector.random(ffDimension, domain.getLowerBound(), domain.getHigherBound()))
                    .setDomain(domain)
                    .build();
            this.particleList.add(particle);
            this.updateSwarmsBestSolution(particle.getSolution());
        });
    }

    public static class builder {
        private Domain domain;
        private int count = 100;
        private int threadsCount = 1;
        private Function<Vector, Double> ff = null;
        private Integer dim;
        private BiFunction<Integer, Double, Tuple3<Double, Double, Double>> pf = null;

        public builder numberOfParticles(Integer count) {
            this.count = count;
            return this;
        }

        public builder numberOfThreads(Integer count) {
            this.threadsCount = count;
            return this;
        }

        public builder fitnessFunction(Function<Vector, Double> ff, Integer vectorDimension) {
            this.ff = ff;
            this.dim = vectorDimension;
            return this;
        }

        public builder parametersCallbackFunction(BiFunction<Integer, Double, Tuple3<Double, Double, Double>> pf) {
            this.pf = pf;
            return this;
        }

        public builder setDomain(Domain domain) {
            this.domain = domain;
            return this;
        }

        Swarm build() {
            return new Swarm(this.count, this.threadsCount, this.ff, this.dim, this.pf, this.domain);
        }
    }
}
