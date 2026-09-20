package ca.prepledger.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import ca.prepledger.model.Recipe;
import ca.prepledger.service.ImportExportService;
import ca.prepledger.service.RecipeService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ImportExportController {

	@FXML
	private Button exportBtn;

	@FXML
	private Button importBtn;

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

	@FXML
	public void onImportBtnClicked() {
		try {
			attemptImportRecipes(Path.of("recipes.json"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void attemptExportRecipes() throws JsonProcessingException {
		List<Recipe> recipesToExport = recipeService.getAllRecipes();

		// check if we have any recipes
		if (recipesToExport.size() == 0) {
			System.out.println("No recipes to export!");
			return;
		}

		try {
			impExpService.exportRecipes(recipesToExport);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void attemptImportRecipes(Path path) 
			throws FileNotFoundException, JsonMappingException,
			JsonProcessingException, IOException {
		List<Recipe> importedRecipes = new ArrayList<>();

		String json = Files.readString(path);

		importedRecipes = impExpService.importRecipes(json);

		for (Recipe recipe : importedRecipes) {
			recipeService.addRecipe(recipe);
		}
	}

	public void setRecipeService(RecipeService recipeService) {
		this.recipeService = recipeService;
	}

}
