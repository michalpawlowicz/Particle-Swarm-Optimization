package pl.edu.agh.pso.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import pl.edu.agh.pso.AbstractParticle;
import pl.edu.agh.pso.Vector;

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
                .match(Response.class, this::WorkerResponseCallback)
                .match(Acquaintances.class, this::unwrapAcquaintances)
                .match(Start.class, this::start)
                .build();
    }

    private void unwrapAcquaintances(Acquaintances acquaintances) {
        System.out.println("[" + getSelf().path() + "Received acquaintances: [" + acquaintances.getAcquaintance().size() + "]");
        this.acquaintancesList = acquaintances.getAcquaintance();
        getSender().tell(new Acquaintances.AcquaintancesResponseOK(), self());
    }


    private void WorkerResponseCallback(Response response) {
        System.out.println("[" + getSelf().path() + "Received response");
        updateBestSolution(response.gBest, response.gBestFitness);
        delegateWork();
    }

    private void updateBestSolution(Vector position, double fitness) {
        if (fitness < this.globalBestKnowFitness) {
            this.globalBestKnowFitness = fitness;
            this.globalBestKnowPosition = position;
        }
    }

    private void start(Start start) {
        System.out.println("[" + getSelf().path() + "] Received start");
        delegateWork();
    }

    private void delegateWork() {
        if(!endCondition.apply(iteration, globalBestKnowFitness)) {
            // TODO ask others for best and send what you have already
            slave.tell(new Request(this.globalBestKnowPosition, this.iteration), getSelf());
            this.iteration += slaveIterationInterval;
        } else {
            // TODO send to SwarmAction information you have finished
        }
    }

    private static class ParticleActorWorker extends AbstractActor {

        private AbstractParticle particle;

        private final int iterationInterval;

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
                    .match(Request.class, this::requestCallback)
                    .build();
        }

        private void requestCallback(Request gBestSolution) {
            for(int i = 0; i < this.iterationInterval; ++i) {
                particle.iterate(i + gBestSolution.startIteration, gBestSolution.gBest);
            }
            var solution = particle.getSolution();
            System.out.println("Solution: " + solution._2);
            sender().tell(new Response(solution._1, solution._2), getSelf());
        }
    }

    public static class Request {
        public Vector gBest;
        public int startIteration;
        public Request(Vector gBest, int startIteration) {
            this.gBest = gBest;
            this.startIteration = startIteration;
        }
    }

    public static class Response {
        private Vector gBest;
        private double gBestFitness;
        public Response(Vector gBest, double gBestFitness) {
            this.gBest = gBest;
            this.gBestFitness = gBestFitness;
        }
    }

    public static class Start {}
}
