package Manage;

public class Goalie extends Player{
	
	private int shots;
	private int goals_against;

	public Goalie(String name, int number, double money, int a, int g, int s, int ga) {
		super(name, number, money, a, g);
		shots = s;
		goals_against = ga;
	}
	
	public Goalie(String name, int number, double money) {
		super(name,number,money);
	}

}
