# Multi Agent Particle Swarm Optimization

This repository is an attempt to implement the PSO algorithm and compare it to a naively parallelized version it terms of it speedup on multicore machines and it's convergence. 

## Introduction

Out multi-agent system implemented in Scala+Akka is compered to Java implementation of a parallelized version as described in [1]. It is asynchronous master-slave implementation, where one thread is supposed to aggregate information, pass the best global solution to other workers and collect solutions from others. This solution is faster than a multi-core sequential algorithm because a particle may start next iteration with a little older best know solution which leads to lock-free implementation.

We intend to go further and fully decentralize the PSO algorithm.

The idea is to minimize the required communication between given particles, but still keep relatively good performance in terms of loss function convergence. This is achieved by describing the problem as the problem of information diffusion or diffusion of infection in social networks, then graphs with best properties, shortest times of diffusion or a minimal number of edges can be used as agent's adjacency matrix. 

Problem with particle leaving the problem's domain we tackled it with setting particle velocity to value near zero and letting the particle turn back to feasible solution domain [2].

## Implementacja wersji rozproszonej
![alt text](https://github.com/michalpawlowicz/Particle-Swarm-Optimization/blob/experimental/scala/scala/PSO_Flow.png?raw=true)

## Rodzaje połączeń pomiędzy aktorami
* Particle komunikują się na podstawie predefiniowanego grafu, wybraliśmy grafy które wydawały nam się dobre oraz które reprezentują sieci społecznościowe

* Barabási–Albert model
![alt text](https://upload.wikimedia.org/wikipedia/commons/thumb/4/40/Barabasi_albert_graph.svg/1920px-Barabasi_albert_graph.svg.png)

* Erdős–Rényi model
![alt text](https://www.researchgate.net/profile/Mikayel_Poghosyan/publication/330369123/figure/fig4/AS:715020707045386@1547485629921/Tree-graph-of-Erdos-Renyi-model-for-large-number-of-nodes.ppm)

* Random geometric graph
![alt text](https://networkx.github.io/documentation/networkx-1.9/_images/random_geometric_graph.png)

* Implementacja w Scala + Akka

## Zbieżność fitnessu dla funkcji Schwefel'a (dimension = 4096)
* Porównanie dla różnych reprezentacji oraz dla wersji sekwencyjnej
![alt text](https://github.com/michalpawlowicz/Particle-Swarm-Optimization/blob/experimental/scala/scala/outs/32_4096/fitness.png?raw=true)

## Speedup dla różnych reprezentacji

![alt text](https://github.com/michalpawlowicz/Particle-Swarm-Optimization/blob/experimental/scala/scala/outs/32_4096/speedup_final.png?raw=true)


[1] A Parallel Particle Swarm Optimization Algorithm Accelerated by Asynchronous Evaluations, https://ntrs.nasa.gov/archive/nasa/casi.ntrs.nasa.gov/20050182658.pdf

[2] Dealing with Boundary Constraint Violations in Particle Swarm Optimization with Aging Leader and Challengers(ALC-PSO) https://pdfs.semanticscholar.org/cbb4/7293ca53905e865c23e9a8ed694d85f0b61e.pdf

[3] A Comprehensive Review of Swarm Optimization Algorithms Mohd Nadhir Ab Wahab https://www.ncbi.nlm.nih.gov/pmc/articles/PMC4436220/pdf/pone.0122827.pdf

[4] Double Flight-Modes Particle Swarm Optimization Wang Yong, 1 Li Jing-yang, 1 and Li Chun-lei 2 https://www.researchgate.net/publication/275459702_Double_Flight-Modes_Particle_Swarm_Optimization
