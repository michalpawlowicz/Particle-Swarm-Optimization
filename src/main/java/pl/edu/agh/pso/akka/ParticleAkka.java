package pl.edu.agh.pso.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import pl.edu.agh.pso.Domain;
import pl.edu.agh.pso.ParametersContainer;
import pl.edu.agh.pso.Vector;
import pl.edu.agh.pso.akka.messages.AcquaintanceMsg;
import pl.edu.agh.pso.akka.messages.InitMsg;
import pl.edu.agh.pso.akka.messages.Solution;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.Function;

class ParticleAkka extends AbstractActor {

    private static final int NOTIFICATION_PERIOD = 20;

    private Vector position;
    private Vector velocity;

    private Vector bestKnownPosition;
    private double bestKnownFitness;
    private int bestSolutionIteration = 0;

    private BiFunction<Integer, Double, Boolean> endCondition;

    private final Function<Vector, Double> ff;

    private final Domain searchDomain;

    private int iteration;

    private ParametersContainer parametersContainer;

    private List<ActorRef> acquaintances;

    static Props props(InitData initData) {
        return Props.create(ParticleAkka.class, initData);
    }

    private ParticleAkka(InitData initData) {
        this.ff = initData.ff;
        this.position = Vector.random(initData.ffDimension, initData.domain.getLowerBound(), initData.domain.getHigherBound());
        this.velocity = Vector.random(initData.ffDimension, initData.domain.getLowerBound(), initData.domain.getHigherBound());
        this.searchDomain = initData.domain;
        this.parametersContainer = initData.parameters;

        this.bestKnownFitness = ff.apply(this.position);
        this.bestKnownPosition = new Vector(this.position);
        this.endCondition = initData.endCondition;
    }


    private void unwrapAcquaintances(AcquaintanceMsg msg) {
        this.acquaintances = msg.getAcquaintance();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(InitMsg.class, this::trigger)
                .match(AcquaintanceMsg.class, this::unwrapAcquaintances)
                .match(Solution.class, solution -> {
                    if (isBetterSolution(solution)) {
                        updateBestKnowSolution(solution);
                    } else {
                        //send to others;
                        getSender().tell(getSolution(), getSelf());
                    }
                }).build();
    }

    private boolean isBetterSolution(Solution solution) {
        return solution.getFitness() < this.bestKnownFitness;
    }

    private Solution getSolution() {
        return new Solution(this.bestKnownFitness, this.bestSolutionIteration, new Vector(this.bestKnownPosition));
    }

    private void updateBestKnowSolution(Solution solution) {
        this.bestSolutionIteration = solution.getIteration();
        this.bestKnownFitness = solution.getFitness();
        this.bestKnownPosition = solution.getPosition();
    }

    private void updateBestKnowPosition(int iteration, Double fitness, Vector vector) {
        this.bestSolutionIteration = iteration;
        this.bestKnownFitness = fitness;
        this.bestKnownPosition = vector;
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

    void trigger(InitMsg msg) {
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
                acquaintances.forEach(particle -> particle.tell(getSolution(), getSelf()));
            }
        }
        getContext().getParent().tell(getSolution(), getSelf());
        getContext().stop(self());
    }
}
