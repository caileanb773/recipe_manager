package definitions;

/**
 * Author: Cailean Bernard
 * Contents: Recipe definition and helper methods.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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

	private int id;
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
	
	public Recipe(int id, String title, List<Ingredient> ingredients, String directions, List<String> tagsList) {
		this.id = id;
		this.title = title;
		this.ingredients = ingredients;
		this.directions = directions;
		tags = tagsList;
	}
	
//	public Recipe(int id, String title, List<Ingredient> ingredients, String directions, String tagsList) {
//		this.id = id;
//		this.title = title;
//		this.ingredients = ingredients;
//		this.directions = directions;
//		List<String> tags = new ArrayList<>();
//		
//		for (String t : tagsList.split(",")) {
//			//t.trim();
//			tags.add(t);
//		}
//	}	

	public List<String> getTags() {
		return tags;
	}

	public void removeTag(String tag) {
		if (tags.contains(tag)) {
			tags.remove(tag);
		}
	}

	public String formatRecipeForExport() {
		StringBuilder sb = new StringBuilder();
		sb.append(title.replace(" ", "_")).append(Constants.RECIPE_SECT_DELIM);
		StringJoiner sj = new StringJoiner(Constants.ING_TAG_DELIM);

		for (Ingredient ing : ingredients) {
			sj.add(ing.getAmount().toString() + Constants.INGREDIENT_SECT_DELIM +
					ing.getUnit().toString().toLowerCase() + Constants.INGREDIENT_SECT_DELIM +
					ing.getName().replace(" ", "_"));
		}

		sb.append(sj);
		String safeDirections = directions.replace("\r\n", "\\n")
				.replace("\n", "\\n");
		sb.append(Constants.RECIPE_SECT_DELIM).append(safeDirections);
		sj = new StringJoiner(Constants.ING_TAG_DELIM);
		
		if (!tags.isEmpty()) {
			for (String tag : tags) {
				sj.add(tag);
			}
			sb.append(Constants.RECIPE_SECT_DELIM).append(sj);
		}

		return sb.toString();
	}

	public String formatRecipeForTextDisplay() {
		StringBuilder sb = new StringBuilder();
		sb.append(title + "\n\n");

		for (Ingredient ing : ingredients) {
			sb.append(ing.getAmount().toString() + " ");
			sb.append(ing.getUnit().toString().toLowerCase() + " ");
			sb.append(ing.getName() + "\n");
		}

		sb.append("\n" + directions + "\n\n");

		if (tags != null && !tags.isEmpty()) {
			sb.append("Tags: " + stringifyTags());
		}

		return sb.toString();
	}
	
	public String formatScaledRecipeForTextDisplay(List<Ingredient> scaledIngredients) {
		StringBuilder sb = new StringBuilder();
		sb.append(title + "\n\n");

		for (Ingredient ing : scaledIngredients) {
			sb.append(ing.getAmount().toString() + " ");
			sb.append(ing.getUnit().toString().toLowerCase() + " ");
			sb.append(ing.getName() + "\n");
		}

		sb.append("\n" + directions + "\n\n");

		if (!tags.isEmpty()) {
			sb.append("Tags: " + stringifyTags());
		}

		return sb.toString();
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
			sb.append(ingredient.getAmount().toString() + " ");
			sb.append(ingredient.getUnit() + " ");
			sb.append(ingredient.getName() + "\n");
		}

		sb.append("Directions:\n" + directions);
		return sb.toString();
	}

	public String getTitle() {
		return title;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
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
	
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (!(o instanceof Recipe)) return false;
	    Recipe other = (Recipe) o;
	    return this.id == other.id;
	}

	@Override
	public int hashCode() {
	    return Objects.hash(id);
	}


}
