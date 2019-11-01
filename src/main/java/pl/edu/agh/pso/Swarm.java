package pl.edu.agh.pso;

import akka.actor.AbstractActor;
import akka.actor.Props;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;

public class Swarm extends AbstractActor {

    static class RunMessage {
    }

    private BiFunction<Integer, Double, Boolean> predicate;

    private Double globalBestKnowFitness;
    private Vector globalBestKnowPosition;

    private Integer domainLowerBound;
    private Integer domainHigherBound;

    private int iter;

    private int childrenToWait;

    private void run(RunMessage m) {
        this.childrenToWait = context().children().size();
        context().children().foreach(cRef -> {
            cRef.tell(new Particle.StartIteration(globalBestKnowPosition, iter), self());
            return cRef;
        });
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(RunMessage.class, this::run)
                .match(Particle.BestPositionMessage.class, this::bestPositionCallback)
                .build();
    }

    private void bestPositionCallback(Particle.BestPositionMessage msg) {
        if(--childrenToWait < 0) {
            throw new RuntimeException("oops");
        }
        if (msg.fitness < this.globalBestKnowFitness) {
            System.out.println("New best fitness: " + this.globalBestKnowFitness);
            this.globalBestKnowFitness = msg.fitness;
            this.globalBestKnowPosition = msg.position;
        }
        if(childrenToWait == 0) {
            if(!predicate.apply(iter, globalBestKnowFitness)) {
                this.run(new RunMessage());
            }
        }
    }

    public static Props props(Function<Vector, Double> fn, Integer particlesCount, Integer dimension, BiFunction<Integer, Double, Boolean> predicate) {
        return Props.create(Swarm.class, fn, particlesCount, dimension, predicate);
    }

    /**
     * Create swarm
     *
     * @param fn             Function to be optimized
     * @param particlesCount Size of swarm
     * @param dimension      Dimension of fn's input vector
     */
    public Swarm(Function<Vector, Double> fn, Integer particlesCount, Integer dimension, BiFunction<Integer, Double, Boolean> predicate) {
        this.iter = 0;
        this.domainLowerBound = -100;
        this.domainHigherBound = 100;
        this.globalBestKnowFitness = Double.MAX_VALUE;
        this.globalBestKnowPosition = Vector.random(dimension, -10000, 1000);
        this.predicate = predicate;
        IntStream.range(0, particlesCount).forEach(i -> {
            context().actorOf(Particle.props(dimension, domainLowerBound, domainHigherBound, fn));
        });
    }
}
