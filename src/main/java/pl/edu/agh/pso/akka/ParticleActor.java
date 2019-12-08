package pl.edu.agh.pso.akka;

import akka.actor.*;
import akka.event.Logging;
import akka.japi.pf.DeciderBuilder;
import org.immutables.value.Value;
import pl.edu.agh.pso.AbstractParticle;
import pl.edu.agh.pso.Vector;
import pl.edu.agh.pso.akka.messages.Acquaintances;
import pl.edu.agh.pso.akka.messages.FinalSolution;
import scala.Tuple2;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;

public class ParticleActor extends AbstractActorWithTimers {

    private Double globalBestKnowFitness;
    private Vector globalBestKnowPosition;
    private ActorRef slave;
    private ActorRef secondSlave;
    private int iteration;
    private final BiFunction<Integer, Double, Boolean> endCondition;

    public ParticleActor(AbstractParticle particle, final BiFunction<Integer, Double, Boolean> endCondition) {
        Tuple2<Vector, Double> solution = particle.getSolution();
        this.globalBestKnowPosition = solution._1;
        this.globalBestKnowFitness = solution._2;
        this.iteration = 0;
        this.endCondition = endCondition;
        slave = context().actorOf(ParticleActorWorker.props(particle));
        secondSlave = context().actorOf(SlaveWorker.props());
    }

    static Props props(AbstractParticle particle, final BiFunction<Integer, Double, Boolean> endCondition) {
        return Props.create(ParticleActor.class, particle, endCondition);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SlaveResponse.class, slaveResponse -> {
                    updateBestSolution(slaveResponse.gBest, slaveResponse.gBestFitness);
                    delegateWork();
                })
                .match(Acquaintances.class, acquaintances -> {
                    this.secondSlave.forward(acquaintances, getContext());
                })
                .match(Start.class, start -> {
                    delegateWork();
                })
                .match(AcquireBestSolutionResponse.class, response -> {
                    updateBestSolution(response.gBest, response.gBestFitness);
                })
                .match(AcquireBestSolutionRequest.class, solution -> {
                    AcquireBestSolutionResponse response = new AcquireBestSolutionResponse(new Vector(this.globalBestKnowPosition), this.globalBestKnowFitness);
                    getSender().tell(response, getSelf());
                })
                .match(TickAcquireBestSolutionRequest.class, tickAcquireBestSolutionRequest -> {
                    this.secondSlave.tell(new SlaveWorker.AskRequest(), getSelf());
                })
                .build();
    }

    private void updateBestSolution(Vector position, double fitness) {
        if (fitness < this.globalBestKnowFitness) {
            this.globalBestKnowFitness = fitness;
            this.globalBestKnowPosition = position;
            //Logging.getLogger(getContext().getSystem(), this).info("Fitness[" + this.globalBestKnowFitness + "] Iteration[" + this.iteration + "]");
        }
    }

    private void delegateWork() {
        if (!endCondition.apply(iteration, globalBestKnowFitness)) {
            this.secondSlave.tell(new SlaveWorker.AskRequest(), getSelf());
            slave.tell(new SlaveRequest(this.globalBestKnowPosition, this.iteration++), getSelf());
        } else {
            getContext().getParent().tell(new FinalSolution(this.globalBestKnowFitness, this.globalBestKnowPosition), self());
            getContext().stop(self());
        }
    }

    private static class ParticleActorWorker extends AbstractActor {

        private AbstractParticle particle;

        private static SupervisorStrategy strategy = new OneForOneStrategy(
                10,
                Duration.ofMinutes(1),
                DeciderBuilder.matchAny(o -> (SupervisorStrategy.Directive) SupervisorStrategy.restart()).build());

        public ParticleActorWorker(AbstractParticle particle) {
            this.particle = particle;
        }

        static Props props(AbstractParticle particle) {
            return Props.create(ParticleActorWorker.class, particle);
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder()
                    .match(SlaveRequest.class, slaveRequest -> {
                        particle.iterate(slaveRequest.iteration, slaveRequest.gBest);
                        Tuple2<Vector, Double> solution = particle.getSolution();
                        sender().tell(new SlaveResponse(solution._1, solution._2), getSelf());
                    })
                    .build();
        }

        @Override
        public SupervisorStrategy supervisorStrategy() {
            return strategy;
        }
    }

    private static class SlaveWorker extends AbstractActor {
        private Optional<List<ActorRef>> acquaintancesList;

        static Props props() {
            return Props.create(SlaveWorker.class);
        }

        public SlaveWorker() {
            this.acquaintancesList = Optional.empty();
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder().match(AskRequest.class, ask -> {
                this.acquaintancesList.ifPresent(list -> list.forEach(actorRef -> {
                    actorRef.tell(new AcquireBestSolutionRequest(), getSender());
                }));
            }).match(Acquaintances.class, acquaintances -> {
                this.acquaintancesList = Optional.of(acquaintances.getAcquaintance());
                getSender().tell(new Acquaintances.AcquaintancesResponseOK(), getContext().getParent());
            }).build();
        }

        public static class AskRequest {}
    }

    private static class SlaveRequest {
        private Vector gBest;
        private int iteration;

        public SlaveRequest(Vector gBest, int iteration) {
            this.gBest = gBest;
            this.iteration = iteration;
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

    private static class AcquireBestSolutionRequest {}

    private static class TickAcquireBestSolutionRequest {}

    private static class AcquireBestSolutionResponse {
        private Vector gBest;
        private double gBestFitness;

        public AcquireBestSolutionResponse(Vector gBest, double gBestFitness) {
            this.gBest = gBest;
            this.gBestFitness = gBestFitness;
        }
    }

    @Value.Immutable
    public static abstract class Start {
        public abstract Tuple2<Integer, Integer> tickTimeBounds();
    }
}
