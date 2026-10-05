package ca.prepledger.model;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import com.fasterxml.jackson.annotation.JsonSetter;

/*
 * Author: Cailean Bernard
 * Contents: Recipes are made up of a title, a List of ingredients, and a set of
 * instructions/directions. A recipe can be created using an overloaded constructor
 * with either List<Ingredient> or a String[] representing all ingredients
 * present in the recipe.
 */

public class Recipe {

	private Long id;
	private String title;
	private List<Ingredient> ingredients;
	private String directions;
	private List<String> tags;
	
	
	/**
	 * No arg constructor.
	 */
	public Recipe() {}
	
	/**
	 * Constructs a new Recipe object (without tags).
	 */
	public Recipe(String title, List<Ingredient> ingredients, String directions) {
		this.title = title;
		this.ingredients = ingredients;
		this.directions = directions;
		this.tags = new ArrayList<>();
	}

	/**
	 * Constructs a new Recipe object (with tags).
	 */
	public Recipe(String title, List<Ingredient> ingredients, String directions, String[] tagsArr) {
		this.title = title;
		this.ingredients = ingredients;
		this.directions = directions;
		tags = new ArrayList<String>();
		for (String tag : tagsArr) {
			tags.add(tag);
		}
	}
	
	public Recipe(Long id, String title, List<Ingredient> ingredients, String directions, List<String> tagsList) {
		this.id = id;
		this.title = title;
		this.ingredients = ingredients;
		this.directions = directions;
		tags = tagsList;
	}
	
	public List<String> getTags() {
		return tags;
	}

	public void removeTag(String tag) {
		if (tags.contains(tag)) {
			tags.remove(tag);
		}
	}
	
	public String stringifyIngredients() {
		StringJoiner sj = new StringJoiner("\n");

		for (Ingredient ing : ingredients) {
			sj.add(ing.toString());
		}

		return sj.toString();
	}

	public String stringifyTags() {
		StringJoiner sj = new StringJoiner(", ");

		for (String tag : tags) {
			sj.add(tag);
		}

		return sj.toString();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Recipe: " + title + "\n" + "Ingredients:\n");

		for (Ingredient ingredient : ingredients) {
			if (ingredient.getAmount() != null) {
				sb.append(ingredient.getAmount().toString() + " ");
			} else {
				sb.append("");
			}
			sb.append(ingredient.getUnit() + " ");
			sb.append(ingredient.getName() + "\n");
		}

		sb.append("Directions:\n" + directions);
		sb.append("\n");
		sb.append("Tags: ");
		
		for (String tag : tags) {
			sb.append("\n");
			sb.append(tag);
		}
		return sb.toString();
	}

	public String getTitle() {
		return title;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public String getDirections() {
		return directions;
	}
	
	public List<Ingredient> getIngredients() {
		return ingredients;
	}
	
	@JsonSetter("tags")
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	
}
