package definitions;

import java.util.Objects;

/*
 * Author: Cailean Bernard
 * Contents: Ingredient definition.
 */
public class Ingredient {
	
	//private String amount;
	private Fraction fracAmount;
	private Unit unit;
	private String name;

	
	public Ingredient(Fraction amount, Unit u, String n) {
		fracAmount = amount;
		name = n;
		unit = u;
	}
	
	public String getName() {
		return name;
	}
	
	public Unit getUnit() {
		return unit;
	}
	
	public Fraction getAmount() {
		return fracAmount;
	}
	
	@Override
	public String toString() {
		return fracAmount.toString() + " " + unit.toString().toLowerCase() + " " + name;
	}
	
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (!(o instanceof Ingredient)) return false;

	    Ingredient other = (Ingredient) o;
	    return Objects.equals(fracAmount, other.fracAmount) &&
	           unit == other.unit &&
	           Objects.equals(name, other.name);
	}

	@Override
	public int hashCode() {
	    return Objects.hash(fracAmount, unit, name);
	}

}
