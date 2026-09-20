package ca.prepledger.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.prepledger.model.Recipe;

public class ImportExportService {

	private ObjectMapper objectMapper = new ObjectMapper();


	public List<Recipe> importRecipes(String json)
			throws JsonMappingException, JsonProcessingException {
		List<Recipe> recipes = objectMapper.readValue(
				json,
				new TypeReference<List<Recipe>>() {}
				);

		return recipes;
	}

	public void exportRecipes(List<Recipe> recipes) throws IOException {
		objectMapper.writerWithDefaultPrettyPrinter()
	      .writeValue(new File("recipes.json"), recipes);
	}

}
