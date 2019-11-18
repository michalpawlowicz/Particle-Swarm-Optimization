package pl.edu.agh.pso.akka;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import pl.edu.agh.pso.AbstractParticle;
import pl.edu.agh.pso.Vector;
import pl.edu.agh.pso.akka.messages.Acquaintances;
import pl.edu.agh.pso.akka.messages.FinalSolution;

import java.time.Duration;
import java.util.List;
import java.util.function.BiFunction;

public class ParticleActor extends AbstractActor {

    private Double globalBestKnowFitness;
    private Vector globalBestKnowPosition;
    private List<ActorRef> acquaintancesList;
    private ActorRef slave;
    private int iteration;
    private final int slaveIterationInterval;
    private final BiFunction<Integer, Double, Boolean> endCondition;

    public ParticleActor(AbstractParticle particle, final BiFunction<Integer, Double, Boolean> endCondition, final int slaveIterationInterval) {
        var solution = particle.getSolution();
        this.globalBestKnowPosition = solution._1;
        this.globalBestKnowFitness = solution._2;
        slave = context().actorOf(ParticleActorWorker.props(particle, slaveIterationInterval));
        this.iteration = 0;
        this.slaveIterationInterval = slaveIterationInterval;
        this.endCondition = endCondition;
    }

    static Props props(AbstractParticle particle, final BiFunction<Integer, Double, Boolean> endCondition, final int slaveIterationInterval) {
        return Props.create(ParticleActor.class, particle, endCondition, slaveIterationInterval);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SlaveResponse.class, this::WorkerResponseCallback)
                .match(Acquaintances.class, this::unwrapAcquaintances)
                .match(Start.class, this::start)
                .match(AcquireBestSolutionResponse.class, this::unwrapBestSolution)
                .match(AcquireBestSolutionRequest.class, solution -> {
                    var response = new AcquireBestSolutionResponse(new Vector(this.globalBestKnowPosition), this.globalBestKnowFitness);
                    getSender().tell(response, getSelf());
                })
                .build();
    }

    private void unwrapBestSolution(AcquireBestSolutionResponse response) {
        updateBestSolution(response.gBest, response.gBestFitness);
    }

    private void unwrapAcquaintances(Acquaintances acquaintances) {
        this.acquaintancesList = acquaintances.getAcquaintance();
        getSender().tell(new Acquaintances.AcquaintancesResponseOK(), self());
    }


    private void WorkerResponseCallback(SlaveResponse slaveResponse) {
        updateBestSolution(slaveResponse.gBest, slaveResponse.gBestFitness);
        delegateWork();
    }

    private void updateBestSolution(Vector position, double fitness) {
        if (fitness < this.globalBestKnowFitness) {
            this.globalBestKnowFitness = fitness;
            this.globalBestKnowPosition = position;
        }
    }

    private void start(Start start) {
        delegateWork();
    }

    private void delegateWork() {
        if (!endCondition.apply(iteration, globalBestKnowFitness)) {
            slave.tell(new SlaveRequest(this.globalBestKnowPosition, this.iteration), getSelf());
            this.acquaintancesList.forEach(actorRef -> {
                actorRef.tell(new AcquireBestSolutionRequest(), getSelf());
            });
            this.iteration += slaveIterationInterval;
        } else {
            getContext().getParent().tell(new FinalSolution(this.globalBestKnowFitness, this.globalBestKnowPosition), self());
            getContext().stop(self());
        }
    }

    private static class ParticleActorWorker extends AbstractActor {

        private AbstractParticle particle;

        private final int iterationInterval;

        private static SupervisorStrategy strategy = new OneForOneStrategy(
                10,
                Duration.ofMinutes(1),
                DeciderBuilder.matchAny(o -> (SupervisorStrategy.Directive) SupervisorStrategy.restart())
                        .build());

        public ParticleActorWorker(AbstractParticle particle, int iterationInterval) {
            this.particle = particle;
            this.iterationInterval = iterationInterval;
        }

        static Props props(AbstractParticle particle, int iterationInterval) {
            return Props.create(ParticleActorWorker.class, particle, iterationInterval);
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder()
                    .match(SlaveRequest.class, this::requestCallback)
                    .build();
        }

        @Override
        public SupervisorStrategy supervisorStrategy() {
            return strategy;
        }

        private void requestCallback(SlaveRequest gBestSolution) {
            for (int i = 0; i < this.iterationInterval; ++i) {
                particle.iterate(i + gBestSolution.startIteration, gBestSolution.gBest);
            }
            var solution = particle.getSolution();
            System.out.println("Solution: " + solution._2);
            sender().tell(new SlaveResponse(solution._1, solution._2), getSelf());
        }
    }

    private static class SlaveRequest {
        private Vector gBest;
        private int startIteration;

        public SlaveRequest(Vector gBest, int startIteration) {
            this.gBest = gBest;
            this.startIteration = startIteration;
        }
    }

    private static class SlaveResponse {
        private Vector gBest;
        private double gBestFitness;

        public SlaveResponse(Vector gBest, double gBestFitness) {
            this.gBest = gBest;
            this.gBestFitness = gBestFitness;
        }
    }

    private static class AcquireBestSolutionRequest {
    }

    private static class AcquireBestSolutionResponse {
        private Vector gBest;
        private double gBestFitness;

        public AcquireBestSolutionResponse(Vector gBest, double gBestFitness) {
            this.gBest = gBest;
            this.gBestFitness = gBestFitness;
        }
    }

    public static class Start {
    }
}
