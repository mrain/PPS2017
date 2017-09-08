package escape.dedicated;

import java.util.List;
import java.util.Random;

public class Player implements escape.sim.Player {
	private Random rand;
	private int i = 0;
	private int[] move = new int[2];
	
	public Player() {
		rand = new Random();
	}
	
	@Override
	public int init(int n) {
		move[0] = rand.nextInt(n);
		move[1] = rand.nextInt(n);
		while (move[1] == move[0])
			move[1] = rand.nextInt(n);
		return move[0];
	}

	@Override
	public int attempt(List<Integer> conflicts) {
		i = 1 - i;
		return move[i];
	}

}
