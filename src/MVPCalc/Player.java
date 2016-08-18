package MVPCalc;

public class Player {

	private String name;
	protected int goals;
	protected int assists;

	public Player(String n, int g, int a) {
		name = n;
		goals = g;
		assists = a;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public double getMVPValue() {
		return (goals + (assists*.5));
	}
}
