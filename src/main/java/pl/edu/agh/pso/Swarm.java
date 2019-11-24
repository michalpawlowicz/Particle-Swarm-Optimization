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

    public void run(BiFunction<Integer, Double, Boolean> predicate) throws ExecutionException, InterruptedException {
        var iteration = 0;
        while (!predicate.apply(iteration, this.globalBestKnowFitness)) {
            List<Future<Optional<Tuple2<Vector, Double>>>> futures = new LinkedList<>();
            for (var particle : particleList) {
                particle.setBestKnowSwarmPosition(iteration, globalBestKnowPosition);
            }
            for (var particle : particleList) {
                futures.add(this.executor.submit(particle));
            }
            for (var result : futures) {
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
            var particle = Particle.builder()
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
