package pl.edu.agh.pso.v2;

import lombok.Builder;
import lombok.Setter;
import pl.edu.agh.pso.Domain;
import pl.edu.agh.pso.ParametersContainer;
import pl.edu.agh.pso.Vector;
import scala.Tuple2;
import scala.Tuple3;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ParticleV2 implements Callable<Optional<Tuple2<Vector, Double>>> {

    private static final int NOTIFICATION_PERIOD = 20;
    private static int PARTICLE_COUNT_INFORMED = 10;

    private Vector position;
    private Vector velocity;

    private Vector bestKnownPosition;
    private double bestKnownFitness;
    private int bestSolutionIteration = 0;

    @Setter
    private BiFunction<Integer, Double, Boolean> endCondition;

    @Setter
    private List<ParticleV2> particlesInSwarm;

    @Setter
    private SwarmV2 swarmV2Supervisor;

    private final Function<Vector, Double> ff;

    private final Domain searchDomain;

    private int iteration;

    private ParametersContainer parametersContainer;

    public Tuple2<Vector, Double> getSolution() {
        return Tuple2.apply(new Vector(this.bestKnownPosition), this.bestKnownFitness);
    }

    public Tuple3<Vector, Double, Integer> getSolutionWithIteration() {
        return Tuple3.apply(new Vector(this.bestKnownPosition), this.bestKnownFitness, this.bestSolutionIteration);
    }

    public Optional<Tuple3<Vector, Double, Integer>> notifyBestKnowSwarmPosition(final int iteration, Tuple3<Vector, Double, Integer> offeredSolution) {
        if (offeredSolution._2() < this.bestKnownFitness) {
            updateBestKnowPosition(iteration, new Tuple2<>(offeredSolution._1(), offeredSolution._2()));
//            System.out.println(getSolutionWithIteration().toString());
            return Optional.empty();
        } else {
            return Optional.of(getSolutionWithIteration());
        }
    }

    private void updateBestKnowPosition(int iteration, Double fitness, Vector vector) {
        this.bestSolutionIteration = iteration;
        this.bestKnownFitness = fitness;
        this.bestKnownPosition = vector;
    }

    private void updateBestKnowPosition(final int iteration, Tuple2<Vector, Double> solution) {
        this.bestKnownPosition = solution._1;
        this.bestKnownFitness = solution._2;
        this.bestSolutionIteration = iteration;
    }

    private void updateBestKnowPosition(Tuple3<Vector, Double, Integer> solution) {
        this.bestKnownPosition = solution._1();
        this.bestKnownFitness = solution._2();
        this.bestSolutionIteration = solution._3();
    }

    private Double apply() {
        return this.ff.apply(this.position);
    }

    private void updateVelocity(final double omega, final double phi_1, final double phi_2, final Vector gBest) {
        if (!this.position.allMatch(searchDomain::feasible)) {
            this.velocity.map(d -> 0.002);
        }
        this.velocity.map((i, vi) -> {
            var rp = ThreadLocalRandom.current().nextDouble();
            var rg = ThreadLocalRandom.current().nextDouble();
            return omega * vi + phi_1 * rp * (this.bestKnownPosition.get(i) - this.position.get(i)) + phi_2 * rg * (gBest.get(i) - this.position.get(i));
        });
    }

    private void updatePosition() {
        this.position.map((i, xi) -> xi + this.velocity.get(i));
    }

    @Builder
    public ParticleV2(Vector position, Vector velocity, final Function<Vector, Double> ff, final Domain searchDomain, final ParametersContainer parametersContainer) {
        this.position = position;
        this.velocity = velocity;
        this.ff = ff;
        this.bestKnownFitness = ff.apply(this.position);
        this.bestKnownPosition = new Vector(this.position);
        this.searchDomain = searchDomain;
        this.parametersContainer = parametersContainer;
    }

    public Optional<Tuple2<Vector, Double>> trigger() {
        while (!endCondition.apply(iteration, this.bestKnownFitness)) {
            this.updateVelocity(parametersContainer.getOmega(this.iteration),
                    parametersContainer.getPhi_1(),
                    parametersContainer.getPhi_2(),
                    this.bestKnownPosition);
            this.updatePosition();
            if (this.searchDomain.feasible(this.position)) {
                final var fitness = this.apply();
                if (fitness < this.bestKnownFitness) {
                    updateBestKnowPosition(iteration, fitness, new Vector(this.position));
                }
            }

            iteration++;
            if (iteration % NOTIFICATION_PERIOD == 0) {
                for (int i = 0; i < PARTICLE_COUNT_INFORMED; i++) {
                    ParticleV2 particleV2 = particlesInSwarm.get(ThreadLocalRandom.current().nextInt(particlesInSwarm.size()));
                    Optional<Tuple3<Vector, Double, Integer>> betterSolution = particleV2.notifyBestKnowSwarmPosition(bestSolutionIteration, getSolutionWithIteration());
                    betterSolution.ifPresent(this::updateBestKnowPosition);
                }
            }
        }

        swarmV2Supervisor.setFinalSolution(this.getSolutionWithIteration());
        return Optional.of(this.getSolution());
    }

    @Override
    public Optional<Tuple2<Vector, Double>> call() {
        return trigger();
    }
}
