package exchange.g0;

import java.util.List;

import exchange.sim.Offer;
import exchange.sim.Request;
import exchange.sim.Sock;
import exchange.sim.Transaction;

public class Player extends exchange.sim.Player {
    /*
        Inherited from exchanage.sim.Player:
        int id          -       Your ID, range from 0 to n-1
        Sock[] socks    -       Your list of socks (length 2p), you need to maintain it by yourselves
        Random random   -       Random number generator, if you need it

        double getTotalEmbarrassment(); functions that help you calculate your total embarrassment, pair up socks 0-1, 2-3, 4-5 ... etc
     */
    private int id1, id2;

    public Player(int id, int n, int p) {
        super(id, n, p);
    }

    @Override
    public Offer makeOffer(List<Request> lastRequests, List<Transaction> lastTransactions) {
        /*
			lastRequests.get(i)		-		Player i's request last round
			lastTransactions		-		All completed transactions last round.
		 */
        int test = random.nextInt(3);
        if (test == 0) {
            // In Offer object, null means no sock is offered
            return new Offer(null, null);
        } else if (test == 1) {
            // Making random offer
            id1 = random.nextInt(socks.length);
            return new Offer(socks[id1], null);
        } else if (test == 2) {
            // Making random offer
            id1 = random.nextInt(socks.length);
            id2 = random.nextInt(socks.length);
            while (id1 == id2)
                id2 = random.nextInt(socks.length);
            return new Offer(socks[id1], socks[id2]);
        }
        return null;
    }

    @Override
    public Request requestExchange(List<Offer> offers) {
		/*
			offers.get(i)			-		Player i's offer
			For each offer:
			offer.getSock(rank = 1, 2)		-		get rank's offer
			offer.getFirst()				-		equivalent to offer.getSock(1)
			offer.getSecond()				-		equivalent to offer.getSock(2)
		 */
        int test = random.nextInt(2);
        if (test == 0) {
            // In Request object, id == -1 means no request.
            return new Request(-1, -1, -1, -1);
        } else {
            // Making random requests
            int k = random.nextInt(offers.size()), retry = 100;
            while (offers.get(k).getSock(1) == null && offers.get(k).getSock(2) == null && retry > 0) {
                retry -= 1;
                k = random.nextInt(offers.size());
            }
            if (offers.get(k).getSock(1) != null)
                return new Request(k, 1, -1, -1);
            else if (offers.get(k).getSock(2) != null)
                return new Request(k, 2, -1, -1);
            else return new Request(-1, -1, -1, -1);
        }
    }

    @Override
    public void completeTransaction(Transaction transaction) {
        /*
            transaction.getFirstID()        -       first player ID of the transaction
            transaction.getSecondID()       -       Similar as above
            transaction.getFirstRank()      -       Rank of the socks for first player
            transaction.getSecondRank()     -       Similar as above
            transaction.getFirstSock()      -       Sock offered by the first player
            transaction.getSecondSock()     -       Similar as above
         */
        int rank;
        Sock newSock;
        if (transaction.getFirstID() == id) {
            rank = transaction.getFirstRank();
            newSock = transaction.getSecondSock();
        } else {
            rank = transaction.getSecondRank();
            newSock = transaction.getFirstSock();
        }
        if (rank == 1) socks[id1] = newSock;
        else socks[id2] = newSock;
    }
}
