package MVPCalc;

public class Goalie extends Player{
	
	private int shots;
	private int goals_against;

	public Goalie(String n, int g, int a, int s, int ga) {
		super(n, g, a);
		shots = s;
		goals_against = ga;
		
	}
	
	public double getMVPValue() {
		return this.getPointsValue() + this.getSaveValue();
	}
	
	public double getPointsValue() {
		return (goals * 6) + (assists *2);
	}
	
	public double getSaveValue() {
		int saves = shots - goals_against;
		return saves/8;
	}

}
