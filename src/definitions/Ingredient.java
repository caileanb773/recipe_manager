package definitions;

/*
 * Author: Cailean Bernard
 * Contents: 
 */

public class Ingredient {
	
	private String amount;
	private Unit unit;
	private String name;

	
	public Ingredient(String a, Unit u, String n) {
		name = n;
		unit = u;
		amount = a;
	}
	
	public String getName() {
		return name;
	}
	
	public Unit getUnit() {
		return unit;
	}
	
	public String getAmount() {
		return amount;
	}
	
	@Override
	public String toString() {
		return amount + " " + unit.toString().toLowerCase() + " " + name;
	}

}
