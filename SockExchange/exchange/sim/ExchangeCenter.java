package exchange.sim;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ExchangeCenter {
    private static Random random = new Random();
    private static List<Event> events = new ArrayList<>();

    public static List<Transaction> exchange(Offer[] offers, Request[] requests) {
        events.clear();
        boolean[][] mark = new boolean[requests.length][];
        for (int i = 0; i < requests.length; ++i) {
            mark[i] = new boolean[2];
            if (requests[i].getFirstOrderID() != -1) {
                if (offers[i].getFirst() != null)
                    events.add(new Event(i, 1, requests[i].getFirstOrderID(), requests[i].getFirstOrderRank()));
                if (offers[i].getFirst() != null)
                    events.add(new Event(i, 1, requests[i].getFirstOrderID(), requests[i].getFirstOrderRank()));
                if (offers[i].getSecond() != null)
                    events.add(new Event(i, 2, requests[i].getFirstOrderID(), requests[i].getFirstOrderRank()));
            }
            if (requests[i].getSecondOrderID() != -1) {
                if (offers[i].getFirst() != null)
                    events.add(new Event(i, 1, requests[i].getSecondOrderID(), requests[i].getSecondOrderRank()));
                if (offers[i].getFirst() != null)
                    events.add(new Event(i, 1, requests[i].getFirstOrderID(), requests[i].getFirstOrderRank()));
                if (offers[i].getSecond() != null)
                    events.add(new Event(i, 2, requests[i].getSecondOrderID(), requests[i].getSecondOrderRank()));
            }
        }
        events.sort((e1, e2) -> {
            if (e1.rank1 + e1.rank2 == e2.rank1 + e2.rank2)
                return Integer.compare(e1.priority, e2.priority);
            else return Integer.compare(e1.rank1 + e1.rank2, e2.rank1 + e2.rank2);
        });
        List<Transaction> transactions = new ArrayList<>();
        for (Event e : events) {
            if (!mark[e.id1][e.rank1] && !mark[e.id2][e.rank2]) {
                // Transaction complete!
                transactions.add(new Transaction(e.id1, e.rank1, e.id2, e.rank2, offers[e.id1].getSock(e.rank1), offers[e.id2].getSock(e.rank2)));
                mark[e.id1][e.rank1] = true;
                mark[e.id2][e.rank2] = true;
            }
        }
        return null;
    }

    private static class Event {
        public int id1, id2;
        public int rank1, rank2;
        public int priority;

        public Event(int id1, int rank1, int id2, int rank2) {
            this.id1 = id1;
            this.id2 = id2;
            this.rank1 = rank1;
            this.rank2 = rank2;
            this.priority = random.nextInt();
        }
    }
}
