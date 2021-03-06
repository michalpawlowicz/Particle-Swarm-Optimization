# Multi Agent Particle Swarm Optimization

This repository is an attempt to implement **fully-decentralized PSO algorithm** and compare it to a naively parallelized version it terms of it speedup on multicore machines and it's convergence. 

## Contents
1. [Introduction](#introduction)
2. [Coverage](#coverage)
3. [Speedup](#speedup)
4. [License](#license)
5. [References](#references)

## Introduction <a name="introduction"></a>

Out multi-agent system implemented in **Scala and Akka** is compered to Java implementation of a parallelized version as described in [[1]](#1). It is asynchronous master-slave implementation, where one thread is supposed to aggregate information, pass the best global solution to other workers and collect solutions from others. This solution is faster than a multi-core sequential algorithm because a particle may start next iteration with a little older best know solution which leads to lock-free implementation.

We intend to go further and **fully decentralize** the PSO algorithm.

The idea is to minimize the required communication between given particles, but still keep relatively good performance in terms of loss function convergence. This is achieved by describing the problem as the problem of **information diffusion** or **diffusion of infection** in social networks, then graphs with best properties, shortest times of diffusion or a minimal number of edges can be used as agent's adjacency matrix. 

Problem with particle **leaving the problem's domain** we tackled it with setting particle velocity to value near zero and letting the particle turn back to feasible solution domain as described in [[2]](#2).

## Coverage for Schwefel function, dimension = 4096 <a name="coverage"></a>
![alt text](https://github.com/michalpawlowicz/Particle-Swarm-Optimization/blob/experimental/scala/scala/outs/32_4096/fitness.png?raw=true)

## Speedup <a name="speedup"></a>
* Schwefel function, dimension 4096
* 32 particles

![alt text](https://github.com/michalpawlowicz/Particle-Swarm-Optimization/blob/experimental/scala/scala/outs/32_4096/speedup_final.png?raw=true)

## License <a name="license"></a>

This project is licensed under the MIT License - see the [LICENSE](./LICENSE) file for details

## References <a name="references"></a>

<a id="1">[1]</a> 
A Parallel Particle Swarm Optimization Algorithm Accelerated by Asynchronous Evaluations, https://ntrs.nasa.gov/archive/nasa/casi.ntrs.nasa.gov/20050182658.pdf

<a id="2">[2]</a> 
Dealing with Boundary Constraint Violations in Particle Swarm Optimization with Aging Leader and Challengers(ALC-PSO) https://pdfs.semanticscholar.org/cbb4/7293ca53905e865c23e9a8ed694d85f0b61e.pdf

<a id="3">[3]</a> 
A Comprehensive Review of Swarm Optimization Algorithms Mohd Nadhir Ab Wahab https://www.ncbi.nlm.nih.gov/pmc/articles/PMC4436220/pdf/pone.0122827.pdf

<a id="4">[4]</a> 
Double Flight-Modes Particle Swarm Optimization Wang Yong, 1 Li Jing-yang, 1 and Li Chun-lei 2 https://www.researchgate.net/publication/275459702_Double_Flight-Modes_Particle_Swarm_Optimization
