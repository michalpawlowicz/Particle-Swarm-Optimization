package pl.edu.agh.pso.akka;

import akka.actor.AbstractActor;
import akka.actor.Props;
import pl.edu.agh.pso.akka.messages.ImmutableAcquaintanceMsg;
import pl.edu.agh.pso.akka.messages.InitMsg;

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
                .match(InitMsg.class, m -> {
                    initChildren();
                })
                .match(Solution.class, message -> {
                    System.out.println("Received solution: " + message + " ");
                })
                .build();
    }

    private void initChildren() {
        IntStream.range(0, initData.particlesCount).forEach(i -> {
            var actorRef = getContext().actorOf(ParticleAkka.props(initData));
            var childrenIterator = context().children().iterator();
            childrenIterator.forall(childRef -> {
                childRef.tell(ImmutableAcquaintanceMsg.builder().build(), getSelf());
                return childRef;
            });
            actorRef.tell(Constants.START_ALGORITHM, getSelf());
        });
    }
}
