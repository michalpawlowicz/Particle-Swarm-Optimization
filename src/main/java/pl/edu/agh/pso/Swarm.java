package pl.edu.agh.pso;

import lombok.Builder;
import org.immutables.value.Value;
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

    private ParametersContainer parameters;

    private int iteration = 0;

    public void run(BiFunction<Integer, Double, Boolean> predicate) throws ExecutionException, InterruptedException {
        while (!predicate.apply(iteration, this.globalBestKnowFitness)) {
            List<Future<Optional<Tuple2<Vector, Double>>>> futures = new LinkedList<>();
            for (Particle particle : particleList) {
                particle.setBestKnowSwarmPosition(iteration, globalBestKnowPosition);
            }
            for (Particle particle : particleList) {
                futures.add(this.executor.submit(particle));
            }
            for (Future<Optional<Tuple2<Vector, Double>>> result : futures) {
                result.get().ifPresent(this::updateSwarmsBestSolution);
            }
            iteration++;
        }
        this.executor.shutdownNow();
        System.out.println("Iteration: " + iteration + " Fitness: " + this.globalBestKnowFitness + "v: " + this.globalBestKnowPosition);
    }

    private void updateSwarmsBestSolution(Tuple2<Vector, Double> solution) {
        if (solution._2 < this.globalBestKnowFitness) {
            this.globalBestKnowFitness = solution._2;
            this.globalBestKnowPosition = solution._1;
            System.out.println("Fitness[" + this.globalBestKnowFitness +"] Iteration[" + this.iteration + "]");
        }
    }

    @Builder
    private Swarm(final Integer particlesCount,
                  final Integer threadsCount,
                  final Function<Vector, Double> ff,
                  final Integer ffDimension,
                  final Domain domain,
                  final ParametersContainer parameters) {
        this.executor = Executors.newFixedThreadPool(threadsCount);
        this.particleList = new LinkedList<>();
        this.globalBestKnowPosition = Vector.random(ffDimension, domain.getLowerBound(), domain.getHigherBound());
        this.globalBestKnowFitness = ff.apply(this.globalBestKnowPosition);
        this.parameters = parameters;
        IntStream.range(0, particlesCount).forEach(i -> {
            Particle particle = Particle.builder()
                    .ff(ff)
                    .position(Vector.random(ffDimension, domain.getLowerBound(), domain.getHigherBound()))
                    .velocity(Vector.random(ffDimension, domain.getLowerBound(), domain.getHigherBound()))
                    .searchDomain(domain)
                    .parametersContainer(this.parameters)
                    .build();
            this.particleList.add(particle);
            this.updateSwarmsBestSolution(particle.getSolution());
        });
    }
}
