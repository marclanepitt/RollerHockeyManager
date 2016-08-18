package Manage;

public class Player {
	
	private String name;
	private int number;
	private double money_owed;
	private int assists;
	private int goals;
	
	public Player(String name, int number, double money, int a, int g) {
		this.name = name;
		this.number = number;
		money_owed = money;
		assists = a;
		goals = g;
	}
	
	public Player(String name, int number, double money) {
		this.name = name;
		this.number = number;
		money_owed = money;
	}

}
