package ca.prepledger.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.prepledger.model.Recipe;

/**
 * Contents: The service which handles the mapping of JSON into a list of recipe
 * objects and vice-versa.
 * 
 * @author Cailean Bernard
 * @since v0.1.0
 */
public class ImportExportService {

	private static ObjectMapper objectMapper = new ObjectMapper();
	
	
	private ImportExportService() {}

	public static List<Recipe> importRecipes(String json)
			throws JsonMappingException, JsonProcessingException {
		List<Recipe> recipes = objectMapper.readValue(
				json,
				new TypeReference<List<Recipe>>() {}
				);

		return recipes;
	}

	public static void exportRecipes(List<Recipe> recipes, String path) throws IOException {
		objectMapper.writerWithDefaultPrettyPrinter()
	      .writeValue(new File(path), recipes);
	}

}
