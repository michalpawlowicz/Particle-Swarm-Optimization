package pl.edu.agh.pso;

import lombok.ToString;
import pl.edu.agh.pso.v2.SwarmV2;

import java.util.function.BiFunction;
import java.util.function.Function;

@ToString
class PSOBuilder {

    static PSOBuilder builder() {
        return new PSOBuilder();
    }

    private Integer particlesCount;
    private Integer threadsCount;
    private Function<Vector, Double> ff;
    private Integer ffDimension;
    private Domain domain;
    private ParametersContainer parameters;
    private BiFunction<Integer, Double, Boolean> endCondition;
    private AlgorithmVersion version;

    private PSOBuilder() {
    }

    PSOBuilder particlesCount(Integer particlesCount) {
        this.particlesCount = particlesCount;
        return this;
    }

    PSOBuilder threadsCount(Integer threadsCount) {
        this.threadsCount = threadsCount;
        return this;
    }

    PSOBuilder ff(Function<Vector, Double> ff) {
        this.ff = ff;
        return this;
    }

    PSOBuilder ffDimension(Integer ffDimension) {
        this.ffDimension = ffDimension;
        return this;
    }

    PSOBuilder domain(Domain domain) {
        this.domain = domain;
        return this;
    }

    PSOBuilder parameters(ParametersContainer parameters) {
        this.parameters = parameters;
        return this;
    }

    PSOBuilder endCondition(BiFunction<Integer, Double, Boolean> endCondition) {
        this.endCondition = endCondition;
        return this;
    }

    PSOBuilder version(AlgorithmVersion version) {
        this.version = version;
        return this;
    }

    PSOAlgorithm build() {
        if (this.version == AlgorithmVersion.V1) {
            return new Swarm(this.particlesCount, this.threadsCount, this.ff, this.ffDimension, this.domain, this.parameters, this.endCondition);
        } else if (this.version == AlgorithmVersion.DISTRIBUTED_PSO) {
            return new SwarmV2(this.particlesCount, this.threadsCount, this.ff, this.ffDimension, this.domain, this.parameters, this.endCondition);
        }
        throw new IllegalArgumentException("Not defined version of algorithm");
    }
}
