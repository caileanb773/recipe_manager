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
	private String unit;
	private String name;
	private String notes;


	public Ingredient() {}

	public Ingredient(
			Fraction amount,
			String unit,
			String name,
			String notes) {
		this.amount = amount;
		this.unit = unit;
		this.name = name;
		this.notes = notes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
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
		String amt = (amount == null) ? null : amount.toString();
		return amt + " " + unit + " of  " + name + ": " + notes;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Ingredient)) return false;

		Ingredient other = (Ingredient) o;
		return Objects.equals(amount, other.amount) &&
				Objects.equals(unit, other.unit) &&
				Objects.equals(name, other.name) &&
				Objects.equals(notes, other.notes);
	}

	@Override
	public int hashCode() {
		return Objects.hash(amount, unit, name, notes);
	}

}
