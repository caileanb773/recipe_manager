package ca.prepledger.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.prepledger.model.Recipe;

/**
 * Contents: The recipe service is the interface for the application and how it 
 * should fetch recipes.
 */
public class RecipeService {
	
	private ArrayList<Recipe> recipeList;
	private List<RecipeChangeListener> listeners = new ArrayList<>();

	private static final Logger logger = LoggerFactory.getLogger(RecipeService.class);
	
	public RecipeService() {
		recipeList = new ArrayList<>();
	}

	public void addRecipe(Recipe recipe) {
		recipeList.add(recipe);
		notifyRecipesChanged();
	}
	
	public void addRecipes(List<Recipe> recipes) {
		recipeList.addAll(recipes);
		notifyRecipesChanged();
	}
	
	public void updateRecipe(int id, Recipe recipe) {
		recipeList.set(id, recipe);
		notifyRecipesChanged();
	}
	
	public void removeRecipe(Recipe recipe) {
		recipeList.remove(recipe);
		notifyRecipesChanged();
	}
	
	public Recipe getRecipe(int id) {
		return recipeList.get(id);
	}
	
	public int getRecipeIndex(Recipe recipe) {
		return recipeList.indexOf(recipe);
	}
	
	public void removeRecipe(int id) {
		recipeList.remove(id);
		notifyRecipesChanged();
	}
	
	public ArrayList<Recipe> getAllRecipes() {
		return recipeList;
	}
	
	public int getTotalRecipes() {
		return recipeList.size();
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
