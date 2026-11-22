package ca.prepledger.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.prepledger.model.Recipe;
import ca.prepledger.session.ConnectionManager;
import ca.prepledger.session.UserSession;

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

	public void addRecipes(List<Recipe> recipes) {
		recipeList.addAll(recipes);
		notifyRecipesChanged();
	}
	
	/**
	 * Decide based on Connection Status whether the created recipe should be
	 * persisted to the back-end or not.
	 * 
	 * @since v0.1.3
	 */
	public Recipe createRecipe(UserSession session, Recipe recipe)
	        throws IOException, InterruptedException {

	    if (connectionManager.isOnlineMode()) {
	        recipe = recipeApiService.createRecipe(session, recipe);	   
	    }

	    recipeList.add(recipe);
	    notifyRecipesChanged();
	    return recipe;
	}

	public Recipe updateRecipe(UserSession session, Recipe recipe, int recipeIndex)
	        throws IOException, InterruptedException {

		// if online, call the relevant backend endpoint method
	    if (connectionManager.isOnlineMode()) {
	        Long id = recipe.getId();

	        if (id == null) {
	            throw new IOException("Missing backend ID for recipe.");
	        }

	        recipe = recipeApiService.updateRecipe(session, recipe, id);
	    }

	    recipeList.set(recipeIndex, recipe);
	    notifyRecipesChanged();
	    return recipe;
	}

	public void removeRecipe(UserSession session, Recipe recipe) 
			throws IOException, InterruptedException {
		
		if (connectionManager.isOfflineMode()) {
			recipeList.remove(recipe);
		} else {				

				// fail if recipe has no ID to pass to recipeservice
				if (recipe.getId() == null) {
					logger.warn("Cannot delete recipe: missing backend ID.");
					throw new IOException("Missing backend ID for recipe.");
				}
				
				recipeApiService.deleteRecipe(session, recipe.getId());
				recipeList.remove(recipe);			
		}

		notifyRecipesChanged();
	}

	public Recipe getRecipe(int id) {
		return recipeList.get(id);
	}

	public int getRecipeIndex(Recipe recipe) {
	    for (int i = 0; i < recipeList.size(); i++) {
	        if (Objects.equals(recipeList.get(i).getId(), recipe.getId())) {
	            return i;
	        }
	    }
	    return -1;
	}

	/**
	 * Decide based on Connection Status whether the imported recipes should be
	 * persisted to the back-end or not.
	 * 
	 * @since v0.1.3
	 */
	public List<Recipe> handleImportRecipes(UserSession session, List<Recipe> recipeList) 
			throws IOException, InterruptedException {
		
		if (connectionManager.isOfflineMode()) {
			return recipeList;
		} else {
			return recipeApiService.importRecipes(session, recipeList);
		}
	}

	public ArrayList<Recipe> getAllRecipes(UserSession session) 
			throws IOException, InterruptedException {
		if (connectionManager.isOfflineMode()) {
			return recipeList;			
		} else {
			return recipeApiService.fetchRecipes(session);
		}
	}
	
	public ArrayList<Recipe> getInMemoryRecipeList() {
		return recipeList;
	}

	public static ArrayList<Recipe> getRecipeList() {
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
