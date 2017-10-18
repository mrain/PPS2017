package exchange.g0;

import exchange.sim.Request;
import exchange.sim.Offer;
import exchange.sim.Transaction;

import java.util.List;

public class Player extends exchange.sim.Player {
	public Player(int id, int n, int p) {
		super(id, n, p);
	}

	/*
	    Inherited from exchanage.sim.Player:
	    int id          -       Your ID, range from 0 to n-1
	    Sock[] socks    -       Your list of socks (length 2p), you need to maintain it by yourselves
	    Random random   -       Random number generator, if you need it

	    double getTotalEmbarrassment(); functions that help you calculate your total embarrassment, pair up socks 0-1, 2-3, 4-5 ... etc
	 */

	@Override
	public Offer makeOffer(List<Request> lastRequests, List<Transaction> lastTransactions) {
		// lastRequests.get(i)
		return null;
	}

	@Override
	public Request requestExchange(List<Offer> offers) {
		return null;
	}

	@Override
	public void completeTransaction(Transaction transaction) {

	}
}
