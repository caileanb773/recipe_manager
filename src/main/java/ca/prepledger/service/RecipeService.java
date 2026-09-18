package ca.prepledger.service;

import java.util.ArrayList;

import ca.prepledger.model.Recipe;

public class RecipeService {
	
	// private RecipeRepository repository;
	
	// XXX to be removed
	private ArrayList<Recipe> dummyRecipeList;
	
	
	// XXX Methods
	
	public RecipeService() {
		dummyRecipeList = new ArrayList<>();
	}

	public void addRecipe(Recipe recipe) {
		dummyRecipeList.add(recipe);
	}
	
	public void updateRecipe(int id, Recipe recipe) {
		dummyRecipeList.set(id, recipe);
	}
	
	public void removeRecipe(Recipe recipe) {
		dummyRecipeList.remove(recipe);
	}
	
	public Recipe getRecipe(int id) {
		return dummyRecipeList.get(id);
	}
	
	public void removeRecipe(int id) {
		dummyRecipeList.remove(id);
	}
	
	public ArrayList<Recipe> getAllRecipes() {
		return dummyRecipeList;
	} 

}
