package escape.sim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

public class PlayerWrapper {
	private Timer thread;
	private Player player;
	private int id, lastMove;
	private int[] playerIds, handleIds;
	private long timeout, originalTimeout;
	
	public PlayerWrapper(Player player, int id, long timeout) {
		this.player = player;
		this.id = id;
		this.timeout = timeout;
		originalTimeout = timeout;
		thread = new Timer();
	}
	
	public int init(int n) throws Exception {
		Log.record("Initializing player " + id);
		// Initializing ID mapping array
		playerIds = new int[n];
		handleIds = new int[n];
		for (int i = 0; i < n; ++ i) {
			playerIds[i] = i;
			handleIds[i] = i;
		}
		Collections.shuffle(Arrays.asList(playerIds));
		int zero = -1;
		for (int i = 0; i < n; ++ i) {
			if (playerIds[i] == 0) {
				zero = i;
				break;
			}
		}
		int temp = playerIds[zero];
		playerIds[zero] = playerIds[id];
		playerIds[id] = temp;
		Collections.shuffle(Arrays.asList(handleIds));
		// Calling player.init(n);
		if (!thread.isAlive()) thread.start();
		thread.call_start(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return player.init(n);
			}
		});
		int ret = thread.call_wait(timeout);
		long elapsedTime = thread.getElapsedTime();
		timeout -= elapsedTime;
		Log.record("Player " + id + " initialized (" + elapsedTime + "ms)");
		lastMove = handleIds[ret];
		return lastMove;
	}
	
	public int attempt(List<Integer> conflicts) throws Exception {
		Log.record("Player " + id + " attempting");
		List<Integer> c = new ArrayList<Integer>();
		for (Integer p : conflicts) {
			if (p != id)
				c.add(playerIds[p]);
		}
		// Calling player.attempt(c)
		if (!thread.isAlive()) thread.start();
		thread.call_start(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return player.attempt(c);
			}
		});
		int ret = thread.call_wait(timeout);
		long elapsedTime = thread.getElapsedTime();
		timeout -= elapsedTime;
		Log.record("Player " + id + " attempts completed (" + elapsedTime + "ms)");
		if (handleIds[ret] == lastMove) 
			throw new IllegalArgumentException("Cannot attempt the same handle twice");
		lastMove = handleIds[ret];
		return lastMove;
	}
	
	public long getTotalElapsedTime() {
		return originalTimeout - timeout;
	}
}
