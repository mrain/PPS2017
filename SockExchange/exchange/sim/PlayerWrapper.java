package exchange.sim;

import java.util.List;
import java.util.concurrent.Callable;

public class PlayerWrapper {
    private Timer thread;
    private Player player;
    private int id;
    private long timeout, originalTimeout;

    public PlayerWrapper(Player player, int id, long timeout) {
        this.player = player;
        this.id = id;
        this.timeout = timeout;
        originalTimeout = timeout;
        thread = new Timer();
    }

    public int getID() {
        return id;
    }

    public Offer makeOffer(List<Request> requests, List<Transaction> lastTransactions) throws Exception {
        if (!thread.isAlive()) thread.start();
        thread.call_start(() -> player.makeOffer(requests, lastTransactions));
        Offer ret = thread.call_wait(timeout);
        long elapsedTime = thread.getElapsedTime();
        timeout -= elapsedTime;
        return ret;
    }


    public Request requestExchange(List<Offer> offers) throws Exception {
        if (!thread.isAlive()) thread.start();
        thread.call_start(() -> player.requestExchange(offers));
        Request ret = thread.call_wait(timeout);
        long elapsedTime = thread.getElapsedTime();
        timeout -= elapsedTime;
        return ret;
    }


    public void completeTransaction(Transaction transaction) throws Exception {
        if (!thread.isAlive()) thread.start();
        thread.call_start((Callable) () -> {
            player.completeTransaction(transaction);
            return null;
        });
        thread.call_wait(timeout);
        long elapsedTime = thread.getElapsedTime();
        timeout -= elapsedTime;
    }

    public long getTotalElapsedTime() {
        return originalTimeout - timeout;
    }
}
