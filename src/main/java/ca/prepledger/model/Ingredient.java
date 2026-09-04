package ca.prepledger.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * Author: Cailean Bernard
 * Contents: Ingredient definition.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ingredient {
	
	private Fraction amount;
	private Unit unit;
	private String name;

	
	public Ingredient() {}
	
	public Ingredient(Fraction amount, Unit u, String n) {
		this.amount = amount;
		name = n;
		unit = u;
	}
	
	public String getName() {
		return name;
	}
	
	public Unit getUnit() {
		return unit;
	}
	
	@JsonIgnore
	public Fraction getAmountFraction() {
		return amount;
	}
	
	@JsonProperty("amount")
	public String getAmount() {
		return amount.toString();
	}
	
	@Override
	public String toString() {
		return amount.toString() + " " + unit.toString().toLowerCase() + " " + name;
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
