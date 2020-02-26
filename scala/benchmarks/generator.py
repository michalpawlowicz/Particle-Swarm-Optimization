from networkx import nx
import sys
from optparse import OptionParser

def get_space_separated_args(option, opt, value, parser):
    setattr(parser.values, option.dest, value.split(' '))

if __name__ == "__main__":
    parser = OptionParser()
    parser.add_option("-n", "--nodes", type="int", dest="nodes", help="Number of nodes")
    parser.add_option("--barabasi-albert", dest="barabasi_albert", action="store_true", default=False, help="Generate graph with Barabasi-Albert model: args: M - number of edges to attach from a new node to existing nodes")
    parser.add_option("--random-regular", dest="random_regular", action="store_true", default=False, help="Generate random-regular graph: args: M - degree")
    parser.add_option("--random", dest="random", action="store_true", default=False, help="Generate random graph: args: M - number of edges")
    parser.add_option("--random-geometric", dest="random_geometric", action="store_true", default=False, help="Generate random graph: args: p[float] - Distance threshold value")
    parser.add_option("--erdos-renyi", dest="erdos_renyi", action="store_true", default=False, help="Generate random graph: args: p[float] - Probability for edge creation.")
    parser.add_option("--args", type='string', action="callback", callback=get_space_separated_args, dest="args", help="Generator specific arguments")

    (options, args) = parser.parse_args()

    if options.barabasi_albert:
        if not options.nodes or options.args is None or len(options.args) != 1:
            parser.print_help()
            sys.exit(1)
        else:
            if len(options.args) != 1:
                parser.print_help()
                sys.exit(1)
            G = nx.barabasi_albert_graph(options.nodes, int(options.args[0]))
            nn = [list(G.neighbors(x)) for x in G]
            for l in nn:
                for n in l:
                    print("{} ".format(n), end='')
                print("")
    elif options.random_regular:
        if not options.nodes or options.args is None or len(options.args) != 1:
            parser.print_help()
            sys.exit(1)
        else:
            G = nx.random_regular_graph(int(options.args[0]), options.nodes)
            nn = [list(G.neighbors(x)) for x in G]
            for l in nn:
                for n in l:
                    print("{} ".format(n), end='')
                print("")
    elif options.random:
        if not options.nodes or options.args is None or len(options.args) != 1:
            parser.print_help()
            sys.exit(1)
        else:
            G = nx.gnm_random_graph(options.nodes, int(options.args[0]))
            nn = [list(G.neighbors(x)) for x in G]
            for l in nn:
                for n in l:
                    print("{} ".format(n), end='')
                print("")
    elif options.random_geometric:
        if not options.nodes or options.args is None or len(options.args) != 1:
            parser.print_help()
            sys.exit(1)
        G = nx.random_geometric_graph(options.nodes, float(options.args[0]))
        nn = [list(G.neighbors(x)) for x in G]
        for l in nn:
            for n in l:
                print("{} ".format(n), end='')
            print("")
    elif options.erdos_renyi:
        if not options.nodes or options.args is None or len(options.args) != 1:
            parser.print_help()
            sys.exit(1)
        else:
            G = nx.erdos_renyi_graph(options.nodes, float(options.args[0]))
            nn = [list(G.neighbors(x)) for x in G]
            for l in nn:
                for n in l:
                    print("{} ".format(n), end='')
                print("")
