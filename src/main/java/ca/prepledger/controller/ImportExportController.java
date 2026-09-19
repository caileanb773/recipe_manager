package ca.prepledger.controller;

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
		System.out.println("click!");
	}
	
	public void setRecipeService(RecipeService recipeService) {
		this.recipeService = recipeService;
	}

}
