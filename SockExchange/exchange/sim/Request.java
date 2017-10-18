package exchange.sim;

import java.util.List;

public class Request {
    private int firstOrderID, firstOrderRank;
    private int secondOrderID, secondOrderRank;

    public Request(int firstOrderID, int firstOrderRank, int secondOrderID, int secondOrderRank) {
        this.firstOrderID = firstOrderID;
        this.firstOrderRank = firstOrderRank;
        this.secondOrderID = secondOrderID;
        this.secondOrderRank = secondOrderRank;
    }

    public int getFirstOrderID() {
        return firstOrderID;
    }

    public int getFirstOrderRank() {
        return firstOrderRank;
    }

    public int getSecondOrderID() {
        return secondOrderID;
    }

    public int getSecondOrderRank() {
        return secondOrderRank;
    }
}
