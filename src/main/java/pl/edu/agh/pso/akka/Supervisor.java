package pl.edu.agh.pso.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import pl.edu.agh.pso.akka.messages.ImmutableAcquaintanceMsg;
import pl.edu.agh.pso.akka.messages.InitMsg;
import pl.edu.agh.pso.akka.messages.SolutionContainer;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Supervisor extends AbstractActor {
    private InitData initData;

    static Props props(InitData initData) {
        return Props.create(Supervisor.class, initData);
    }

    private Supervisor(InitData initData) {
        this.initData = initData;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(InitMsg.class, this::init)
                .match(SolutionContainer.class, message -> {
                    System.out.println("Received solution: " + message + " ");
                }).build();
    }

    private void init(InitMsg msg) {
        IntStream.range(0, initData.particlesCount).forEach(i -> {
            getContext().actorOf(ParticleAkka.props(initData));
        });

        List<ActorRef> children = new LinkedList<>();
        getContext().getChildren().forEach(children::add);

        final var random = new Random();
        children.forEach(childRef -> {
            childRef.tell(ImmutableAcquaintanceMsg.builder()
                            .addAllAcquaintance(
                                    children.stream()
                                            .filter(actorRef -> random.nextBoolean())
                                            .collect(Collectors.toList()))
                            .build(),
                    getSelf());
        });

        children.forEach(childRef -> childRef.tell(new InitMsg(), getSelf()));
    }
}
