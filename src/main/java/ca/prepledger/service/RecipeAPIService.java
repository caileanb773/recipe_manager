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

	private HttpClient client;
	private final ObjectMapper mapper;
	private static final String RECIPES_URL = ApiConfig.RAILWAY_ROOT + "/recipes";
	private static final Logger logger = LoggerFactory.getLogger(RecipeAPIService.class);
	

	public RecipeAPIService() {
		this.client = HttpClient.newHttpClient();
		this.mapper = new ObjectMapper();
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
				.uri(URI.create(RECIPES_URL + "/recipes"))
				.header("Authorization", "Bearer " + session.getToken())
	            .header("Content-Type", "application/json")				
				.POST(HttpRequest.BodyPublishers.ofString(json))
				.build();
		
		HttpResponse<String> response = client.send(
				request,
				HttpResponse.BodyHandlers.ofString());

	    if (response.statusCode() != 200 && response.statusCode() != 200) {
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

//	public Recipe updateRecipe(Recipe recipe, Long existingRecipeId) 
//			throws InterruptedException, IOException {
//		String json = mapper.writeValueAsString(recipe);
//
//		HttpRequest request = HttpRequest.newBuilder()
//				.uri(URI.create(baseUrl + "/recipes/" + existingRecipeId))
//				.header("Content-Type", "application/json")
//				.PUT(HttpRequest.BodyPublishers.ofString(json))
//				.build();
//
//		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//		if (response.statusCode() != 200) {
//			throw new IOException("PUT failed. HTTP: " + response.statusCode()
//			+ "\n" + response.body());
//		}
//
//		return mapper.readValue(response.body(), Recipe.class);
//	}

	////////////////////////////////////////////////////////////////////////////
	//
	// DELETE
	//
	////////////////////////////////////////////////////////////////////////////	

//	public void deleteRecipe(Long id) 
//			throws InterruptedException, IOException {
//		HttpRequest request = HttpRequest.newBuilder()
//				.uri(URI.create(baseUrl + "/recipes/" + id))
//				.DELETE()
//				.build();
//
//		HttpResponse<Void> response = client.send(
//				request, HttpResponse.BodyHandlers.discarding());
//
//		// Delete failed, likely couldn't find ID
//		if (response.statusCode() != 204) {
//			throw new IOException("Failed to delete recipe: HTTP "
//					+ response.statusCode());
//		}
//	}

}
