package definitions;

import java.util.Objects;

/*
 * Author: Cailean Bernard
 * Contents: Ingredient definition.
 */
public class Ingredient {
	
	private Fraction quantity;
	private Unit unit;
	private String name;

	
	public Ingredient(Fraction amount, Unit u, String n) {
		quantity = amount;
		name = n;
		unit = u;
	}
	
	public String getName() {
		return name;
	}
	
	public Unit getUnit() {
		return unit;
	}
	
	public Fraction getQuantity() {
		return quantity;
	}
	
	@Override
	public String toString() {
		return quantity.toString() + " " + unit.toString().toLowerCase() + " " + name;
	}
	
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (!(o instanceof Ingredient)) return false;

	    Ingredient other = (Ingredient) o;
	    return Objects.equals(quantity, other.quantity) &&
	           unit == other.unit &&
	           Objects.equals(name, other.name);
	}

	@Override
	public int hashCode() {
	    return Objects.hash(quantity, unit, name);
	}

}
