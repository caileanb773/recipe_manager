package ca.prepledger.controller;

import java.io.File;
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
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;

public class ImportExportController {

	@FXML
	private Button exportBtn;
	
	@FXML
	private Button importBtn;
	
	@FXML
	private VBox importDropzone;

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
	
	@FXML
	private void onImportZoneDragOver(DragEvent event) {
	    if (event.getDragboard().hasFiles()) {
	        event.acceptTransferModes(TransferMode.COPY);
	    }

	    event.consume();
	}
	
	@FXML
	private void onImportZoneDragDropped(DragEvent event) {
	    System.out.println("drag dropped");

	    Dragboard db = event.getDragboard();

	    if (db.hasFiles()) {
	        File file = db.getFiles().get(0);
	        
	        // validate that it's actually json
	        if (file.getName().toLowerCase().endsWith(".json")) {
	        	try {
					attemptImportRecipes(Path.of(file.getAbsolutePath()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        } else {
	        	// TODO show user error that they didn't put a json file in the zone
	        }
	        event.setDropCompleted(true);
	    } else {
	        event.setDropCompleted(false);
	    }

	    event.consume();
	}
	
	@FXML
	private void onImportZoneDragEntered(DragEvent event) {
	    if (event.getDragboard().hasFiles()) {
			importDropzone.getStyleClass().add("import-zone-drag-over");
	    }
	}
	
	@FXML
	private void onImportZoneDragExited(DragEvent event) {
	    if (event.getDragboard().hasFiles()) {
	    	importDropzone.getStyleClass().remove("import-zone-drag-over");
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
