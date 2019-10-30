package pso;

public class Domain {
    private int lowb;
    private int upb;
    public Domain(int low, int up) {
        this.lowb = low;
        this.upb = up;
        if (lowb < upb) {
            throw new RuntimeException("x >= y");
        }
    }

    public int lowerBound() { return lowb; }
    public int upperBound() { return upb; }
}
