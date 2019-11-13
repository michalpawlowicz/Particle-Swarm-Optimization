package pl.edu.agh.pso.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import pl.edu.agh.pso.Domain;
import pl.edu.agh.pso.ParametersContainer;
import pl.edu.agh.pso.Vector;
import pl.edu.agh.pso.akka.messages.AcquaintanceMsg;
import pl.edu.agh.pso.akka.messages.InitMsg;
import pl.edu.agh.pso.akka.messages.SolutionContainer;

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

    private int iteration;

    private final BiFunction<Integer, Double, Boolean> endCondition;

    private final Function<Vector, Double> ff;

    private final Domain searchDomain;

    private final ParametersContainer parametersContainer;

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

    private void checkSolution(SolutionContainer solution) {
        if(solution.getFitness() < this.bestKnownFitness) {
            updateBestKnowSolution(solution);
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(InitMsg.class, this::trigger)
                .match(AcquaintanceMsg.class, this::unwrapAcquaintances)
                .match(SolutionContainer.class,this::checkSolution)
                .build();
    }

    private SolutionContainer getSolution() {
        return new SolutionContainer(new Vector(this.bestKnownPosition), this.bestKnownFitness, this.bestSolutionIteration);
    }

    private void updateBestKnowSolution(SolutionContainer solution) {
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
            final var rp = ThreadLocalRandom.current().nextDouble();
            final var rg = ThreadLocalRandom.current().nextDouble();
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
