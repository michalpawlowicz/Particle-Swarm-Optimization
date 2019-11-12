package pl.edu.agh.pso.akka.messages;

import akka.actor.ActorRef;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public abstract class AcquaintanceMsg {
    public abstract List<ActorRef> getAcquaintance();
}
