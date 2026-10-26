package ca.prepledger.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.prepledger.config.ApiConfig;
import ca.prepledger.model.Recipe;
import ca.prepledger.session.UserSession;

/**
 * Contents: The recipe service is the interface for the application and how it 
 * should fetch recipes.
 */
public class RecipeService {
	
	private ArrayList<Recipe> recipeList;
	private List<RecipeChangeListener> listeners = new ArrayList<>();
	private static final String RECIPES_URL = ApiConfig.RAILWAY_ROOT + "/recipes";
	private HttpClient client = HttpClient.newHttpClient();
	private ObjectMapper mapper = new ObjectMapper();
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
	
	/////////////////////////////
	/// 
	/// Spring Backend Methods
	/// 
	/////////////////////////////

	public List<Recipe> fetchRecipes(UserSession session)
	        throws IOException, InterruptedException {

	    HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create(RECIPES_URL))
	            .header("Authorization", "Bearer " + session.getToken())
	            .GET()
	            .build();

	    HttpResponse<String> response = client.send(
	            request, HttpResponse.BodyHandlers.ofString());

	    if (response.statusCode() != 200) {
	        throw new IOException(
	                "Failed to fetch recipes. HTTP status: "
	                + response.statusCode());
	    }
	    
	    Recipe[] recipes = mapper.readValue(response.body(), Recipe[].class);
	    logger.info("Successfully fetched {} recipes from backend.",  recipes.length);

	    return Arrays.asList(recipes);
	}
	
}
