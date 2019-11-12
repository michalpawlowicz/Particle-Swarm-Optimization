package pl.edu.agh.pso.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import pl.edu.agh.pso.PSOAlgorithm;
import pl.edu.agh.pso.akka.messages.InitMsg;

public class SwarmAkka implements PSOAlgorithm {

    @Override
    public void launch() {
        throw new IllegalArgumentException("Not implemented");
    }


    public static void run(InitData initData) {
        Props props = Supervisor.props(initData);
        ActorSystem system = ActorSystem.create("dupa");
        ActorRef supervisor = system.actorOf(Supervisor.props(initData));
        supervisor.tell(new InitMsg(), null);

    }
}
