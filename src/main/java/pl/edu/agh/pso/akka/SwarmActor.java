package pl.edu.agh.pso.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import org.immutables.value.Value;
import pl.edu.agh.pso.Domain;
import pl.edu.agh.pso.ParametersContainer;
import pl.edu.agh.pso.Vector;
import pl.edu.agh.pso.akka.messages.Acquaintances;
import pl.edu.agh.pso.akka.messages.FinalSolution;
import pl.edu.agh.pso.akka.messages.ImmutableAcquaintances;
import scala.Tuple2;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SwarmActor extends AbstractActor {
    static Props props() {
        return Props.create(SwarmActor.class);
    }

    private BiFunction<Integer, Double, Boolean> endCondition;

    private FinalSolution bestKnownSolution;

    private int particlesToBeProcessed;

    private Tuple2<Integer, Integer> tickBounds;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Init.class, m -> this.init(m.particlesCount(), m.ff(), m.ffDimension(), m.domain(), m.parameters(), m.endCondition(), m.tickBounds()))
                .match(Acquaintances.AcquaintancesResponseOK.class, ok -> {
                    sender().tell(ImmutableStart.builder().tickTimeBounds(tickBounds).build(), getSelf());
                })
                .match(FinalSolution.class, solution -> {
                    if (solution.isBetterSolutionThan(bestKnownSolution)) {
                        bestKnownSolution = solution;
                        if (endCondition.apply(0, bestKnownSolution.getFitness())) {
                            System.out.println("Final solution: " + bestKnownSolution);
                            this.cleanUp();
                            return;
                        }
                    }
                    int counter = --particlesToBeProcessed;

                    if (counter == 0) {
                        System.out.println("All particles have done their job, final best solution: " + bestKnownSolution);
                        this.cleanUp();
                    }
                })
                .build();
    }

    private void cleanUp() {
        getContext().getChildren().forEach(child -> getContext().stop(child));
        getContext().stop(getSelf());
        getContext().getSystem().terminate();
    }

    private void init(final Integer particlesCount,
                      final Function<Vector, Double> ff,
                      final Integer ffDimension,
                      final Domain domain,
                      final ParametersContainer parameters,
                      final BiFunction<Integer, Double, Boolean> endCondition,
                      final Tuple2<Integer, Integer> askTickBounds
    ) {
        System.out.println("SwarmActor initialization");
        System.out.println("Creating ParticleActors [" + particlesCount + "] ...");
        this.endCondition = endCondition;
        this.particlesToBeProcessed = particlesCount;
        this.tickBounds = askTickBounds;

        IntStream.range(0, particlesCount).forEach(i -> {
            Particle particle = Particle.builder()
                    .ff(ff)
                    .position(Vector.random(ffDimension, domain.getLowerBound(), domain.getHigherBound()))
                    .velocity(Vector.random(ffDimension, domain.getLowerBound(), domain.getHigherBound()))
                    .searchDomain(domain)
                    .parametersContainer(parameters)
                    .build();
            getContext().actorOf(ParticleActor.props(particle, endCondition));
        });

        List<ActorRef> children = new LinkedList<>();
        getContext().getChildren().forEach(children::add);
        System.out.println("Children list size: " + children.size());

        System.out.println("Setting acquaintances ...");
        final Random random = new Random();
        children.forEach(childRef -> {
            Acquaintances accs = ImmutableAcquaintances.builder()
                                    .addAllAcquaintance(children.stream()
                                    .filter(actorRef -> random.nextBoolean())
                                    .collect(Collectors.toList()))
                                    .build();
            childRef.tell(accs, getSelf());
        });

        System.out.println("Init done");
    }

    @Value.Immutable
    public static abstract class Init {
        public abstract Integer particlesCount();

        public abstract Function<Vector, Double> ff();

        public abstract Integer ffDimension();

        public abstract Domain domain();

        public abstract ParametersContainer parameters();

        public abstract BiFunction<Integer, Double, Boolean> endCondition();

        public abstract Tuple2<Integer, Integer> tickBounds();
    }
}
