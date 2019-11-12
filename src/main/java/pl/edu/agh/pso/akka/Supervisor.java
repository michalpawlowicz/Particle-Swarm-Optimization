package pl.edu.agh.pso.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import java.util.stream.IntStream;

public class Supervisor extends AbstractActor {

    private InitData initData;

    static Props props(InitData initData) {
        return Props.create(Supervisor.class, initData);
    }

    private Supervisor(InitData initData) {
        this.initData = initData;
        System.out.println("Supervisor initialized");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, message -> {
                    if (message.equals(Constants.START_ALGORITHM)) {
                        initChildren();
                    } else {
                        throw new IllegalArgumentException("Received unexpected message");
                    }
                })
                .match(Solution.class, message -> {
                    System.out.println("Received solution: " + message + " ");
//                    getContext().stop(getSelf());

                }).build();
    }

    private void initChildren() {
        IntStream.rangeClosed(0, initData.particlesCount).forEach(i -> {
            ActorRef actorRef = getContext().actorOf(ParticleAkka.props(initData), Constants.WORKER + i);
            actorRef.tell(Constants.START_ALGORITHM, getSelf());
        });
    }
}
