package exchange.sim;

import java.util.List;
import java.util.Random;

public abstract class Player {
	protected int id;
	protected Sock[] socks;
	protected Random random;

	public Player(int id, int n, int p) {
		this.id = id;
		random = new Random();
		socks = new Sock[2 * n];
		for (int i = 0; i < 2 * n; ++ i)
			socks[i] = new Sock(random.nextInt(255), random.nextInt(255), random.nextInt(255));
	}

	public abstract Offer makeOffer(List<Request> lastRequests, List<Transaction> lastTransactions);

	public abstract Request requestExchange(List<Offer> offers);

	public abstract void completeTransaction(Transaction transaction);

	public double getTotalEmbarrassment() {
	    double result = 0;
	    for (int i = 0; i < socks.length; i += 2)
	        result += socks[i].distance(socks[i + 1]);
	    return result;
    }
}
