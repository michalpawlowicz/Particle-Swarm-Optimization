# Particle-Swarm-Optimization

## Implementacja wersji synchronicznej
* Wzorowany na [1]. Model Master-Slave, jeden wątek zajmuje się agregacją informacji od pozostałych agentów.
* Kiedy particle wyleci poza domene problemu? -> [2] Ustawiamy v na bliskie zero i pozwalamy mu wrócić

## Implementacja wersji rozproszonej

![alt text](https://github.com/michalpawlowicz/Particle-Swarm-Optimization/blob/experimental/scala/scala/outs/32_4096/fitness.png?raw=true)

![alt text](https://github.com/michalpawlowicz/Particle-Swarm-Optimization/blob/experimental/scala/scala/outs/32_4096/speedup_final.png?raw=true)


[1] A Parallel Particle Swarm Optimization Algorithm Accelerated by Asynchronous Evaluations, https://ntrs.nasa.gov/archive/nasa/casi.ntrs.nasa.gov/20050182658.pdf

[2] Dealing with Boundary Constraint Violations in Particle Swarm Optimization with Aging Leader and Challengers(ALC-PSO) https://pdfs.semanticscholar.org/cbb4/7293ca53905e865c23e9a8ed694d85f0b61e.pdf

[3] A Comprehensive Review of Swarm Optimization Algorithms Mohd Nadhir Ab Wahab https://www.ncbi.nlm.nih.gov/pmc/articles/PMC4436220/pdf/pone.0122827.pdf

[4] Double Flight-Modes Particle Swarm Optimization Wang Yong, 1 Li Jing-yang, 1 and Li Chun-lei 2 https://www.researchgate.net/publication/275459702_Double_Flight-Modes_Particle_Swarm_Optimization
