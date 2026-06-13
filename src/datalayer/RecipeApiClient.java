package datalayer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import definitions.Recipe;

public class RecipeApiClient {
	
	private HttpClient client;
	private final ObjectMapper mapper;
	private final String baseUrl;
	
	
	public RecipeApiClient(String baseUrl) {
		this.client = HttpClient.newHttpClient();
		this.mapper = new ObjectMapper();
		this.baseUrl = baseUrl;
	}
	
	// Get recipe by id
	public Recipe getRecipe(long id) throws Exception {	
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(baseUrl + "/recipes/" + id))
				.GET()
				.build();
		
		HttpResponse<String> response = client.send(
				request,
				HttpResponse.BodyHandlers.ofString());
			
		return mapper.readValue(response.body(), Recipe.class);
	}
	
	// Get all recipes
	public List<Recipe> getAllRecipes() throws Exception {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(baseUrl + "/recipes"))
				.GET()
				.build();
		
		HttpResponse<String> response = client.send(
				request,
				HttpResponse.BodyHandlers.ofString());
		
	    return mapper.readValue(
	    		response.body(),
	    		new TypeReference<List<Recipe>>() {});
	}
	
	// Search recipes by title
	public List<Recipe> searchRecipes(String title) throws Exception
	{
	    String url = baseUrl + "/recipes/search?title=" + title;

	    HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create(url))
	            .GET()
	            .build();

	    HttpResponse<String> response = client.send(
	            request,
	            HttpResponse.BodyHandlers.ofString()
	    );

	    return mapper.readValue(
	            response.body(),
	            new TypeReference<List<Recipe>>() {}
	    );
	}
	
	// Create recipe (POST)
	public Recipe createRecipe(Recipe recipe) throws Exception
	{
	    String json = mapper.writeValueAsString(recipe);

	    HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create(baseUrl + "/recipes"))
	            .header("Content-Type", "application/json")
	            .POST(HttpRequest.BodyPublishers.ofString(json))
	            .build();

	    HttpResponse<String> response = client.send(
	            request,
	            HttpResponse.BodyHandlers.ofString()
	    );

	    return mapper.readValue(response.body(), Recipe.class);
	}
	
}
