package ca.prepledger.service;

import java.util.ArrayList;
import java.util.List;

import ca.prepledger.model.Recipe;

/**
 * Contents: The recipe service is the interface for the application and how it 
 * should fetch recipes.
 */
public class RecipeService {
	
	// private RecipeRepository repository;
	
	// XXX to be removed
	private ArrayList<Recipe> dummyRecipeList;
	
	private List<RecipeChangeListener> listeners = new ArrayList<>();
	
	
	public RecipeService() {
		dummyRecipeList = new ArrayList<>();
	}

	public void addRecipe(Recipe recipe) {
		dummyRecipeList.add(recipe);
		notifyRecipesChanged();
	}
	
	public void addRecipes(List<Recipe> recipes) {
		dummyRecipeList.addAll(recipes);
		notifyRecipesChanged();
	}
	
	public void updateRecipe(int id, Recipe recipe) {
		dummyRecipeList.set(id, recipe);
		notifyRecipesChanged();
	}
	
	public void removeRecipe(Recipe recipe) {
		dummyRecipeList.remove(recipe);
		notifyRecipesChanged();
	}
	
	public Recipe getRecipe(int id) {
		return dummyRecipeList.get(id);
	}
	
	public int getRecipeIndex(Recipe recipe) {
		return dummyRecipeList.indexOf(recipe);
	}
	
	public void removeRecipe(int id) {
		dummyRecipeList.remove(id);
		notifyRecipesChanged();
	}
	
	public ArrayList<Recipe> getAllRecipes() {
		return dummyRecipeList;
	}
	
	public int getTotalRecipes() {
		return dummyRecipeList.size();
	}
	
	public void registerRecipeChangeListener(RecipeChangeListener listener) {
		listeners.add(listener);
	}
	
	public void notifyRecipesChanged() {
		for (RecipeChangeListener listener : listeners) {
			listener.onRecipesChanged();
		}
	}

}
