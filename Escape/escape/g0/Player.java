package escape.g0;

import java.util.List;
import java.util.Random;

public class Player implements escape.sim.Player {
	private Random rand;
	private int n = 0;
	private int lastMove;

	public Player() {
		rand = new Random();
	}

	@Override
	public int init(int n) {
		this.n = n;
		lastMove = rand.nextInt(n);
		return lastMove;
	}

	// Strategy: (Just for demostration, may not work.)
	// 1. If no one grabs your handle, grab the next handle.
	// 2. Grab the i-th handle where i is the id of some player who grab the same handle as you do.
	@Override
	public int attempt(List<Integer> conflicts) {
		if (conflicts.size() == 0)
			lastMove = (lastMove + 1) % n;
		else {
			if (lastMove == conflicts.get(0)) {
				if (conflicts.size() > 1)
					lastMove = conflicts.get(1);
				else
					lastMove = (lastMove + 1) % n;
			} else lastMove = conflicts.get(0);
		}
		return lastMove;
	}

}
