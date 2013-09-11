package gunslinger.dumb5;

import java.util.*;

// An example player
// Extends gunslinger.sim.Player to start with your player
//
public class Player extends gunslinger.sim.Player
{
	// keep track of rounds
	private int round = 0;

	// track the history of all the rounds
	private int[][] history;

	// Current Neutral-turned enemies
	ArrayList<Integer> current_alive_turned_enemies = new ArrayList<Integer>();

	// total versions of the same player
	private static int versions = 0;
	// my version no
	private int version = versions++;

	// A simple fixed shoot rate strategy used by the dumb player
	private static double ShootRate = 0.8;

	// name of the team
	//
	public String name()
	{
		return "dumb0" + (versions > 1 ? " v" + version : "");
	}

	// Initialize the player
	//
	public void init(int nplayers, int[] friends, int enemies[])
	{
		// Note:
		//  Seed your random generator carefully
		//  if you want to repeat the same random number sequence
		//  pick your favourite seed number as the seed
		//  Or you can simply use the clock time as your seed     
		//       
		gen = new Random(System.currentTimeMillis());
		// long seed = 12345;
		// gen = new Random(seed);

		this.nplayers = nplayers;
		this.friends = friends.clone();
		this.enemies = enemies.clone();

		history = new int[nplayers][nplayers];
		for (int i = 0; i < nplayers; i++)
		{
			for (int j = 0; j < nplayers; j++)
			{
				history[i][j] = 0;
			}
		}
	}

	// Pick a target to shoot
	// Parameters:
	//  prevRound - an array of previous shoots, prevRound[i] is the player that player i shot
	//              -1 if player i did not shoot
	//  alive - an array of player's status, true if the player is still alive in this round
	// Return:
	//  int - the player id to shoot, return -1 if do not shoot anyone
	//
	int target;
	public int shoot(int[] prevRound, boolean[] alive)
	{
		System.out.println("Players: " + nplayers);

		/* Strategy used by the dumb player:
		   Decide whether to shoot or not with a fixed shoot rate
		   If decided to shoot, randomly pick one alive that is not your friend */
		round++;            
		int alive_friends = 0;
		int alive_players = 0;
		ArrayList<Integer> current_alive_friends = new ArrayList<Integer>();

		// Shoot or not in this round?
		for (int i=0 ; i<nplayers ; i++)
		{
			if (alive[i])
			{
				alive_players++;
				for (int j=0 ; j<friends.length ; j++)
				{
					if (friends[j] == i)
					{
						current_alive_friends.add(i);
						alive_friends++;
					}
				}

				for (int j=0 ; j<enemies.length ; j++)
				{

				}
			}
		}

		if (round == 1)
		{
			double initial_prob = 1.0 - ((1.0*alive_friends)/(alive_players - 1));
			System.out.println("Initial Prob. of DUMB5: " + initial_prob + "\n");

			boolean shoot = true;
			//boolean shoot = gen.nextDouble() < ShootRate;
			if (initial_prob > 0.5)
				shoot = true;
			else
			{
				shoot = false;
				return -1;
			}
		}

		else
		{
			for (int i=0 ; i<nplayers ; i++)
				System.out.println("Prev Round: " + prevRound[i]);
		
			// Update History
			System.out.println("Updating History...");
			for (int i = 0; i < nplayers; i++)
			{
				if(prevRound[i] != -1)
				{
					(history[i][prevRound[i]])++;
					System.out.println(i + " " + prevRound[i]+ "  " + history[i][prevRound[i]]);
				}
			}

			// Print History
			System.out.println("Printing History...");
			for (int i = 0; i < nplayers; i++)
			{
				for (int j = 0; j < nplayers; j++)
				{
					System.out.print(history[i][j] + " ");
				}
				System.out.println();
			}

			// Make my shooter my prime target
			for (int i=0 ; i < nplayers ; i++)
			{
				for (int j=0 ; j<alive.length ; j++)
				{
					if (alive[i] == true)
					{
						if(prevRound[i] == id)
						{
							target = i;
							System.out.println("New Target: " + i);

							// Add current hitter to my enemy list
							current_alive_turned_enemies.add(i);
							
							return target;
						}
					}
				}
			}
		}

		// Populate alive enemies
		// keep track of current alive enemies
		ArrayList<Integer> current_alive_enemies = new ArrayList<Integer>();
		HashMap<Integer, Integer> current_alive_enemy_score = new HashMap<Integer, Integer>();
		// Iterate through all players to find other alive players
		for (int i = 0; i != nplayers; ++i)
		{
			// If alive
			if (i != id && alive[i] )
			{
				// Find enemies
				for (int j=0 ; j<enemies.length ; j++)
				{
					// If current player is an enemy
					if (enemies[j] == i)
					{
						// If current enemy has hit a friend before, add its score
						for (int k=0 ; k<current_alive_friends.size() ; k++)
						{
							if (history[i][current_alive_friends.get(k)] > 0)
							{
								int score = history[i][current_alive_friends.get(k)] + current_alive_enemy_score.get(i);
								current_alive_enemy_score.put(i,score);

								System.out.println("Added " + i + "to enemy list since it shot " + current_alive_friends.get(k));
								current_alive_enemies.add(i);
							}
						}
					}
				}
			}
		}

		// Pick first target and shoot
		//int target = current_alive_enemies.get(gen.nextInt(current_alive_enemies.size()));
		if (current_alive_enemies.size() > 0)
		{
			int target = current_alive_enemies.get(0);
			return target;
		}
		return -1;
	}

	private Random gen;
	private int nplayers;
	private int[] friends;
	private int[] enemies;
}
