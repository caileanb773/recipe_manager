package ca.prepledger.service;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.prepledger.model.Recipe;

public class ImportExportService {
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	
	public List<Recipe> importRecipes() {
		// TODO complete
		return null;
	}
	
	public void exportRecipes(List<Recipe> recipes) throws JsonProcessingException {
		String json = objectMapper
				.writerWithDefaultPrettyPrinter()
				.writeValueAsString(recipes);
		
		System.out.println(json);
	}

}
