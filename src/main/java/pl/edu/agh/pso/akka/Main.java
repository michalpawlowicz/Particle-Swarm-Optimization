package pl.edu.agh.pso.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import pl.edu.agh.pso.ImmutableDomain;
import pl.edu.agh.pso.ImmutableParametersContainer;
import pl.edu.agh.pso.Swarm;
import pl.edu.agh.pso.benchmark.Schwefel;

public class Main {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("pso");
        ActorRef swarm = system.actorOf(SwarmActor.props());
        final var particlesCount = 16;
        final var dimension = 100;
        final var iterMax = 5e5;
        final var omegaMin = 0.4;
        final var omegaMax = 1.4;
        final var phi_1 = 0.5;
        final var phi_2 = 1.5;
        var startMsg = ImmutableInit.builder()
                .ff(Schwefel.build())
                .particlesCount(particlesCount)
                .ffDimension(dimension)
                .domain(ImmutableDomain.builder()
                        .lowerBound(-500)
                        .higherBound(500)
                        .build())
                .parameters(ImmutableParametersContainer.builder()
                        .phi_1(phi_1)
                        .phi_2(phi_2)
                        .omegaMin(omegaMin)
                        .omegaMax(omegaMax)
                        .step((omegaMax - omegaMin) / iterMax)
                        .build())
                .endCondition((i, f) -> {
                    return (iterMax != 0 && i >= iterMax) || Math.abs(f) < 1e-9;
                })
                .iterationInterval(200)
                .build();
        swarm.tell(startMsg, null);
    }
}
