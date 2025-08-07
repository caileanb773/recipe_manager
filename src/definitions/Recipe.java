package definitions;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/*
 * Author: Cailean Bernard
 * Contents: Recipes are made up of a title, a List of ingredients, and a set of
 * instructions/directions. A recipe can be created using an overloaded constructor
 * with either List<Ingredient> or a String[] representing all ingredients
 * present in the recipe.
 */

public class Recipe {

	private String title;
	private List<Ingredient> ingredients;
	private String directions;
	private List<String> tags;

	public Recipe(String title, List<Ingredient> ingredients, String directions) {
		this.title = title;
		this.ingredients = ingredients;
		this.directions = directions;
		this.tags = new ArrayList<>();
	}

	public Recipe(String title, List<Ingredient> ingredients, String directions, List<String> tags) {
		this.title = title;
		this.ingredients = ingredients;
		this.directions = directions;
		this.tags = tags;
	}
	
	public Recipe(String title, List<Ingredient> ingredeints, String directions, String[] tagsArr) {
		this.title = title;
		this.ingredients = ingredeints;
		this.directions = directions;
		tags = new ArrayList<String>();
		for (String tag : tagsArr) {
			tags.add(tag);
		}
	}

	public Recipe(String title, String[] ingredientArr, String directions) {
		this.title = title;
		this.directions = directions;
		ingredients = new ArrayList<Ingredient>();

		try {
			for (int i = 0; i < ingredientArr.length; i++) {
				ingredients.add(new Ingredient(
						Float.parseFloat(ingredientArr[Constants.AMT_IDX]),
						Unit.valueOf(ingredientArr[Constants.UNIT_IDX]),
						ingredientArr[Constants.NAME_IDX]));
			}	
		} catch (IndexOutOfBoundsException e) {
			System.err.println("Malformed ingredient encountered in Recipe constructor.");
			e.printStackTrace();
		}
	}

	public List<String> getTags() {
		return tags;
	}

	public void removeTag(String tag) {
		if (tags.contains(tag)) {
			tags.remove(tag);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Recipe: " + title + "\n" + "Ingredients:\n");

		for (Ingredient ingredient : ingredients) {
			sb.append(ingredient.getAmount() + " ");
			sb.append(ingredient.getUnit() + " ");
			sb.append(ingredient.getName() + "\n");
		}

		sb.append("Directions:\n" + directions);
		return sb.toString();
	}

	public String formatRecipeForExport() {
		StringBuilder sb = new StringBuilder();
		sb.append(title + ":");
		StringJoiner sj = new StringJoiner(",");

		for (Ingredient ing : ingredients) {
			sj.add(ing.getAmount() + " " +
					ing.getUnit().toString().toLowerCase() + " " +
					ing.getName().replace(" ", "_"));
		}

		sb.append(sj.toString());
		sb.append(":" + directions);
		sj = new StringJoiner(",");
		
		if (!tags.isEmpty()) {
			
			for (String tag : tags) {
				sj.add(tag);
			}
			
			sb.append(":" + sj.toString());
		}

		return sb.toString();
	}

	public String formatRecipeForTextDisplay() {
		StringBuilder sb = new StringBuilder();
		sb.append(title + "\n\n");
		float amt;

		for (Ingredient ing : ingredients) {
			amt = ing.getAmount();

			if (amt % 1 == 0) {
				sb.append((int)amt + " ");
			} else {
				sb.append(amt + " ");
			}

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

	public String getTitle() {
		return title;
	}

	public String getDirections() {
		return directions;
	}

}
