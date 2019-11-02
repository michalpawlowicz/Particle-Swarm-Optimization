package pl.edu.agh.pso;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;

import java.time.Duration;
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

    private void run(RunMessage m) {
        final Timeout timeout = Timeout.create(Duration.ofSeconds(10));
        context().children().iterator().map(cRef -> Patterns.ask(cRef,
                new Particle.State(),
                Timeout.apply(timeout.duration()))).foreach(f -> {
            try {
                var result = (Particle.Response) Await.result(f, timeout.duration());
                System.out.println("Response: " + result);
            } catch (Exception e) {
                // TODO restart particle without losing the state?
                e.printStackTrace();
            }
            return true;
        });
        System.out.println("-----------------------------------------------");
        int iter = 0;
        while (!predicate.apply(iter, this.globalBestKnowFitness)) {
            final int finalIter = iter;
            context().children().iterator().map(cRef -> {
                return Patterns.ask(cRef, new Particle.StartIteration(globalBestKnowPosition, finalIter), Timeout.apply(timeout.duration()));
            }).foreach(f -> {
                try {
                    var result = (Particle.Response) Await.result(f, timeout.duration());
                    if (result.fitness < globalBestKnowFitness) {
                        System.out.println("New fitness: " + result);
                        this.globalBestKnowPosition = result.position;
                        this.globalBestKnowFitness = result.fitness;
                    }
                } catch (Exception e) {
                    // TODO restart particle without losing the state?
                    e.printStackTrace();
                }
                return true;
            });
            iter++;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(RunMessage.class, this::run)
                .build();
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
        this.globalBestKnowPosition = Vector.random(dimension, domainLowerBound, domainHigherBound);
        this.predicate = predicate;
        IntStream.range(0, particlesCount).forEach(i -> {
            context().actorOf(Particle.props(dimension, domainLowerBound, domainHigherBound, fn));
        });
    }
}
