package ca.prepledger.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.prepledger.model.Recipe;
import ca.prepledger.session.ConnectionManager;

/**
 * Contents: The recipe service is the interface for the application and how it 
 * should fetch recipes.
 * 
 * @author Cailean Bernard
 * @since v0.1.1
 */
public class RecipeService {

	// TODO revisit whether this needs to be static still
	private static ArrayList<Recipe> recipeList;
	private List<RecipeChangeListener> listeners = new ArrayList<>();
	private ConnectionManager connectionManager;
	private RecipeAPIService recipeApiService;

	private static final Logger logger = LoggerFactory.getLogger(RecipeService.class);

	public RecipeService(
			ConnectionManager connectionManager,
			RecipeAPIService recipeApiService) {
		this.connectionManager = connectionManager;
		this.recipeApiService = recipeApiService;
		recipeApiService = new RecipeAPIService();
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

	public static ArrayList<Recipe> getAllRecipes() {
		return recipeList;
	}

	public int getTotalRecipes() {
		return recipeList.size();
	}

	public void registerRecipeChangeListener(RecipeChangeListener listener) {
		listeners.add(listener);
	}

	public void notifyRecipesChanged() {
		logger.info("notifyRecipesChanged(): Notifying listeners...");

		for (RecipeChangeListener listener : listeners) {
			listener.onRecipesChanged();
		}
	}

	public RecipeAPIService getRecipeApiService() {
		return this.recipeApiService;
	}

}
