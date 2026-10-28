package ca.prepledger.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.prepledger.config.ApiConfig;
import ca.prepledger.model.Recipe;
import ca.prepledger.session.UserSession;

public class RecipeAPIService {

	private String Domain;
	private final HttpClient client = HttpClient.newHttpClient();;
	private final ObjectMapper mapper = new ObjectMapper();
	private static final String RECIPES_PATH = "/recipes";
	private static final Logger logger = LoggerFactory.getLogger(RecipeAPIService.class);
	

	public RecipeAPIService() {
		Domain = (ApiConfig.mode == ApiConfig.Mode.PROD) 
				? ApiConfig.RAILWAY_ROOT : ApiConfig.LOCALHOST_ROOT;
	}


	////////////////////////////////////////////////////////////////////////////
	//
	// GET
	//
	////////////////////////////////////////////////////////////////////////////

	// Get recipe by id
//	public Recipe getRecipe(long id)
//			throws InterruptedException, IOException {	
//		HttpRequest request = HttpRequest.newBuilder()
//				.uri(URI.create(baseUrl + "/recipes/" + id))
//				.GET()
//				.build();
//
//		HttpResponse<String> response = client.send(
//				request,
//				HttpResponse.BodyHandlers.ofString());
//
//		return mapper.readValue(response.body(), Recipe.class);
//	}
//
	public List<Recipe> fetchRecipes(UserSession session)
	        throws IOException, InterruptedException {

	    HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create(Domain + RECIPES_PATH))
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
//
//	// Search recipes by title
//	public List<Recipe> searchRecipes(String title)
//			throws InterruptedException, IOException {
//		String url = baseUrl + "/recipes/search?title=" + title;
//
//		HttpRequest request = HttpRequest.newBuilder()
//				.uri(URI.create(url))
//				.GET()
//				.build();
//
//		HttpResponse<String> response = client.send(
//				request,
//				HttpResponse.BodyHandlers.ofString()
//				);
//
//		return mapper.readValue(
//				response.body(),
//				new TypeReference<List<Recipe>>() {}
//				);
//	}
//
//	// Get Heartbeat
//	public String getSpringAPIHeartbeat()
//			throws InterruptedException, IOException {
//
//		String heartbeatMsg = null;
//
//		HttpRequest request = HttpRequest.newBuilder()
//				.uri(URI.create(baseUrl + "/health"))
//				.GET()
//				.build();
//
//		HttpResponse<String> response = client.send(
//				request,
//				HttpResponse.BodyHandlers.ofString()
//				);
//
//		heartbeatMsg = response.body();	
//		return heartbeatMsg;
//	}


	////////////////////////////////////////////////////////////////////////////
	//
	// POST
	//
	////////////////////////////////////////////////////////////////////////////	

	// Create recipe
	public Recipe createRecipe(UserSession session, Recipe recipe)
			throws InterruptedException, IOException {
		
		String json = mapper.writeValueAsString(recipe);
		
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(Domain + RECIPES_PATH))
				.header("Authorization", "Bearer " + session.getToken())
	            .header("Content-Type", "application/json")				
				.POST(HttpRequest.BodyPublishers.ofString(json))
				.build();
		
		HttpResponse<String> response = client.send(
				request,
				HttpResponse.BodyHandlers.ofString());

	    if (response.statusCode() != 201 && response.statusCode() != 200) {
	        throw new IOException(
	                "Failed to create recipe. HTTP status: "
	                + response.statusCode() + "\n" + response.body());
	    }
	    
	    logger.info("Successfully created recipe: {}.", recipe.getTitle());

		return mapper.readValue(response.body(), Recipe.class);
	}


	////////////////////////////////////////////////////////////////////////////
	//
	// PUT
	//
	////////////////////////////////////////////////////////////////////////////	

	public Recipe updateRecipe(UserSession session, Recipe recipe, Long existingRecipeId) 
			throws InterruptedException, IOException {
		String json = mapper.writeValueAsString(recipe);

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(Domain + RECIPES_PATH + "/" + existingRecipeId))
				.header("Authorization", "Bearer " + session.getToken())
				.header("Content-Type", "application/json")
				.PUT(HttpRequest.BodyPublishers.ofString(json))
				.build();

		HttpResponse<String> response = client.send(
				request,
				HttpResponse.BodyHandlers.ofString());

	    if (response.statusCode() != 200) {
	        throw new IOException(
	                "Failed to update recipe. HTTP status: "
	                + response.statusCode() + "\n" + response.body());
	    }
	    
	    logger.info("Successfully updated recipe: {}.", recipe.getTitle());

		return mapper.readValue(response.body(), Recipe.class);
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// DELETE
	//
	////////////////////////////////////////////////////////////////////////////	

	public void deleteRecipe(UserSession session, Long id) 
			throws InterruptedException, IOException {
		
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(Domain + RECIPES_PATH + "/" + id))
				.header("Authorization", "Bearer " + session.getToken())
				.DELETE()
				.build();

		HttpResponse<String> response = client.send(
				request, HttpResponse.BodyHandlers.ofString());

		// Delete failed, likely couldn't find ID
		if (response.statusCode() != 204 && response.statusCode() != 200) {
			throw new IOException("Failed to delete recipe: HTTP status: "
					+ response.statusCode() + "\n" + response.body());
		}
		
	    logger.info("Successfully deleted recipe.");
	}

}
