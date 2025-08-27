package definitions;

import java.util.Objects;

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
	
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (!(o instanceof Ingredient)) return false;

	    Ingredient other = (Ingredient) o;
	    return Objects.equals(amount, other.amount) &&
	           unit == other.unit &&
	           Objects.equals(name, other.name);
	}

	@Override
	public int hashCode() {
	    return Objects.hash(amount, unit, name);
	}


}
