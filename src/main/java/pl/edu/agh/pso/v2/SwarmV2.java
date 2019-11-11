package pl.edu.agh.pso.v2;

import lombok.Builder;
import pl.edu.agh.pso.Domain;
import pl.edu.agh.pso.ParametersContainer;
import pl.edu.agh.pso.Vector;
import scala.Tuple3;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;

public class SwarmV2 {

    private List<ParticleV2> particleList;

    private ExecutorService executor;

    private ParametersContainer parameters;

    private Double finalGlobalBestKnowFitness;

    private Vector finalGlobalBestKnowPosition;

    private int iterationOfSolution;

    public void run() throws InterruptedException {
        for (var particle : particleList) {
            particle.setSwarmV2Supervisor(this);
        }

        this.executor.invokeAll(particleList);
        while (finalGlobalBestKnowPosition == null) {
            TimeUnit.SECONDS.sleep(10);
        }

        executor.shutdownNow();

        System.out.println("Iteration: " + this.iterationOfSolution + " Best value: " + this.finalGlobalBestKnowFitness + "v: " + this.finalGlobalBestKnowPosition);
    }

    void setFinalSolution(Tuple3<Vector, Double, Integer> solution) {
        this.finalGlobalBestKnowPosition = solution._1();
        this.finalGlobalBestKnowFitness = solution._2();
        this.iterationOfSolution = solution._3();

    }

    @Builder
    private SwarmV2(final Integer particlesCount,
                    final Integer threadsCount,
                    final Function<Vector, Double> ff,
                    final Integer ffDimension,
                    final Domain domain,
                    final ParametersContainer parameters,
                    final BiFunction<Integer, Double, Boolean> endCondition) {
        this.executor = Executors.newFixedThreadPool(threadsCount);
        this.parameters = parameters;
        this.particleList = new LinkedList<>();
        IntStream.range(0, particlesCount).forEach(i -> {
            var particle = ParticleV2.builder()
                    .ff(ff)
                    .position(Vector.random(ffDimension, domain.getLowerBound(), domain.getHigherBound()))
                    .velocity(Vector.random(ffDimension, domain.getLowerBound(), domain.getHigherBound()))
                    //.velocity(Vector.random(ffDimension, -1, 1))
                    .searchDomain(domain)
                    .parametersContainer(this.parameters)
                    .build();

            this.particleList.add(particle);
        });

        this.particleList.forEach(particle -> {
            particle.setParticlesInSwarm(this.particleList);
            particle.setEndCondition(endCondition);
        });
    }
}
