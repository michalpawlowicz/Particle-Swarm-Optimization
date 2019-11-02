package pl.edu.agh.pso;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import java.util.function.Function;

public class Main {
    public static void main(String[] args) {
        Function<Vector, Double> fn2 = v -> {
            return (v.get(0) - 71) * (v.get(0) - 71) + (v.get(1) + 21) * (v.get(1) + 21);
        };
        var system = ActorSystem.create("pso");
        var swarmRef = system.actorOf(Swarm.props(fn2, 20, 2, (i, f) -> {
            return f < 1e-6;
        }), "swarm");
        swarmRef.tell(new Swarm.RunMessage(), ActorRef.noSender());
    }
}