package definitions;

/*
 * Author: Cailean Bernard
 * Contents: 
 */

public class Ingredient {
	
	private float amount;
	private Unit unit;
	private String name;

	
	public Ingredient(float a, Unit u, String n) {
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
	
	public float getAmount() {
		return amount;
	}
	
	@Override
	public String toString() {
		return amount + " " + unit.toString().toLowerCase() + " " + name;
	}

}
