package pl.edu.agh.pso.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import pl.edu.agh.pso.PSOAlgorithm;

public class SwarmAkka implements PSOAlgorithm {

    @Override
    public void launch() {
        throw new IllegalArgumentException("Not implemented");
    }


    public static void run(InitData initData) {
        Props props = Supervisor.props(initData);
        ActorSystem system = ActorSystem.create("dupa");
        ActorRef supervisor = system.actorOf(props, Constants.SUPERVISOR);
        supervisor.tell(Constants.START_ALGORITHM, null);

    }
}
