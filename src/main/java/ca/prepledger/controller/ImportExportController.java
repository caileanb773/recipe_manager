package ca.prepledger.controller;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import ca.prepledger.model.Recipe;
import ca.prepledger.service.ImportExportService;
import ca.prepledger.service.RecipeService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ImportExportController {
	
	@FXML
	private Button exportBtn;
	
	private RecipeService recipeService;
	
	private ImportExportService impExpService = new ImportExportService();
	

	@FXML
	public void onExportBtnClicked() {
		try {
			attemptExportRecipes();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void attemptExportRecipes() throws JsonProcessingException {
		List<Recipe> recipes = recipeService.getAllRecipes();
		
		// check if we have any recipes
		if (recipes.size() == 0) {
			System.out.println("No recipes to export!");
			return;
		}
		
		impExpService.exportRecipes(recipes);
	}
	
	public void setRecipeService(RecipeService recipeService) {
		this.recipeService = recipeService;
	}

}
