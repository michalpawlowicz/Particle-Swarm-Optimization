package pl.edu.agh.pso.akka;

import lombok.Builder;
import pl.edu.agh.pso.Domain;
import pl.edu.agh.pso.ParametersContainer;
import pl.edu.agh.pso.Vector;

import java.util.function.BiFunction;
import java.util.function.Function;

@Builder
public class InitData {
    final Integer particlesCount;
    //    final Integer threadsCount;
    final Function<Vector, Double> ff;
    final Integer ffDimension;
    final Domain domain;
    final ParametersContainer parameters;
    final BiFunction<Integer, Double, Boolean> endCondition;
}
