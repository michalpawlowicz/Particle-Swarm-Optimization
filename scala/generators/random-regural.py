from networkx import nx
import sys


if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Number of nodes")
        print("degree")
        exit(1)
    n = int(sys.argv[1])
    d = int(sys.argv[2])
    G = nx.random_regular_graph(d, n)
    nn = [list(G.neighbors(x)) for x in G]
    for l in nn:
        for n in l:
            print("{} ".format(n), end='')
        print("")

