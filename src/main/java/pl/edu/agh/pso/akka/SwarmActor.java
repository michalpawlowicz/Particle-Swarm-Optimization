package pl.edu.agh.pso.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import org.immutables.value.Value;
import pl.edu.agh.pso.Domain;
import pl.edu.agh.pso.ParametersContainer;
import pl.edu.agh.pso.Vector;

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

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Init.class, m -> this.init(m.particlesCount(), m.ff(), m.ffDimension(), m.domain(), m.parameters(), m.endCondition(), m.iterationInterval()))
                .match(Acquaintances.AcquaintancesResponseOK.class, ok -> {
                    System.out.println("Starting [" + getSender().path() + "]");
                    sender().tell(new ParticleActor.Start(), getSelf());
                })
                .build();
    }

    private void init(final Integer particlesCount,
                      final Function<Vector, Double> ff,
                      final Integer ffDimension,
                      final Domain domain,
                      final ParametersContainer parameters,
                      final BiFunction<Integer, Double, Boolean> endCondition,
                      final Integer slaveIterationInterval) {
        System.out.println("SwarmActor initialization");
        System.out.println("Creating ParticleActors [" + particlesCount + "] ...");
        IntStream.range(0, particlesCount).forEach(i -> {
            var particle = Particle.builder()
                    .ff(ff)
                    .position(Vector.random(ffDimension, domain.getLowerBound(), domain.getHigherBound()))
                    .velocity(Vector.random(ffDimension, domain.getLowerBound(), domain.getHigherBound()))
                    .searchDomain(domain)
                    .parametersContainer(parameters)
                    .build();
            getContext().actorOf(ParticleActor.props(particle, endCondition, slaveIterationInterval));
        });

        List<ActorRef> children = new LinkedList<>();
        getContext().getChildren().forEach(children::add);
        System.out.println("Children list size: " + children.size());

        System.out.println("Setting acquaintances ...");
        final var random = new Random();
        children.forEach(childRef -> {
            var accs = ImmutableAcquaintances.builder()
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

        public abstract int iterationInterval();
    }
}
