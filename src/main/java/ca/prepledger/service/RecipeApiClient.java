package service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.util.List;

import org.postgresql.util.PSQLException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.Recipe;

public class RecipeApiClient {

	private HttpClient client;
	private final ObjectMapper mapper;
	private final String baseUrl;


	public RecipeApiClient(String baseUrl) {
		this.client = HttpClient.newHttpClient();
		this.mapper = new ObjectMapper();
		this.baseUrl = baseUrl;
	}


	////////////////////////////////////////////////////////////////////////////
	//
	// GET
	//
	////////////////////////////////////////////////////////////////////////////

	// Get recipe by id
	public Recipe getRecipe(long id)
			throws InterruptedException, IOException {	
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
	public List<Recipe> getAllRecipes()
			throws InterruptedException, IOException, PSQLException, SQLException {
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
	public List<Recipe> searchRecipes(String title)
			throws InterruptedException, IOException {
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

	// Get Heartbeat
	public String getSpringAPIHeartbeat()
			throws InterruptedException, IOException {

		String heartbeatMsg = null;

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(baseUrl + "/health"))
				.GET()
				.build();

		HttpResponse<String> response = client.send(
				request,
				HttpResponse.BodyHandlers.ofString()
				);

		heartbeatMsg = response.body();	
		return heartbeatMsg;
	}


	////////////////////////////////////////////////////////////////////////////
	//
	// POST
	//
	////////////////////////////////////////////////////////////////////////////	

	// Create recipe
	public Recipe createRecipe(Recipe recipe)
			throws InterruptedException, IOException {
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


	////////////////////////////////////////////////////////////////////////////
	//
	// PUT
	//
	////////////////////////////////////////////////////////////////////////////	

	public Recipe updateRecipe(Recipe recipe, Long existingRecipeId) 
			throws InterruptedException, IOException {
		String json = mapper.writeValueAsString(recipe);

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(baseUrl + "/recipes/" + existingRecipeId))
				.header("Content-Type", "application/json")
				.PUT(HttpRequest.BodyPublishers.ofString(json))
				.build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		if (response.statusCode() != 200) {
			throw new IOException("PUT failed. HTTP: " + response.statusCode()
			+ "\n" + response.body());
		}

		return mapper.readValue(response.body(), Recipe.class);
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// DELETE
	//
	////////////////////////////////////////////////////////////////////////////	

	public void deleteRecipe(Long id) 
			throws InterruptedException, IOException {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(baseUrl + "/recipes/" + id))
				.DELETE()
				.build();

		HttpResponse<Void> response = client.send(
				request, HttpResponse.BodyHandlers.discarding());

		// Delete failed, likely couldn't find ID
		if (response.statusCode() != 204) {
			throw new IOException("Failed to delete recipe: HTTP "
					+ response.statusCode());
		}
	}

}
