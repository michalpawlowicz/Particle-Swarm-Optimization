from networkx import nx
import sys

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Number of nodes")
        print("Distance threshold value")
        exit(1)
    n = int(sys.argv[1])
    p = float(sys.argv[2])
    G = nx.random_geometric_graph(n, p)
    nn = [list(G.neighbors(x)) for x in G]
    for l in nn:
        for n in l:
            print("{} ".format(n), end='')
        print("")
