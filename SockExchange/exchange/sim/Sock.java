package exchange.sim;

public class Sock {
    public int R, G, B;

    public Sock(int R, int G, int B) {
        this.R = R;
        this.G = G;
        this.B = B;
    }

    public Sock(Sock sock) {
        this.R = sock.R;
        this.G = sock.G;
        this.B = sock.B;
    }

    public boolean equals(Sock s) {
        return R == s.R && G == s.G && B == s.B;
    }

    public double distance(Sock s) {
        return Math.sqrt(Math.pow(this.R - s.R, 2) + Math.pow(this.G - s.G, 2) + Math.pow(this.B - s.B, 2));
    }

    @Override
    public String toString() {
        return "Sock(" + R + ", " + G + ", " + B + ")";
    }
}
