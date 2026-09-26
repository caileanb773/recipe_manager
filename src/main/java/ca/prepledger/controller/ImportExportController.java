package ca.prepledger.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import ca.prepledger.model.Recipe;
import ca.prepledger.service.ImportExportService;
import ca.prepledger.service.RecipeService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

public class ImportExportController {

	@FXML
	private Button exportBtn;
	
	@FXML
	private Button importBtn;
	
	@FXML
	private VBox importDropzone;

	private RecipeService recipeService;

	private ImportExportService impExpService = new ImportExportService();
	
	private static final Logger logger = LoggerFactory.getLogger(ImportExportController.class);


	@FXML
	public void onExportBtnClicked() {
		try {
			attemptExportRecipes();
		} catch (JsonProcessingException e) {
			logger.error("onExportBtnClicked(): JsonProcessingException encountered: {}", e);
		}
	}

	@FXML
	public void onImportBtnClicked(ActionEvent event)  {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Open .json File");
		chooser.getExtensionFilters().add(
				new ExtensionFilter("Recipe Collections", "*.json"));
		Window window = ((Node)event.getSource()).getScene().getWindow();
		File selectedFile = chooser.showOpenDialog(window);
		
		if (selectedFile == null) {
			// TODO error dialog
			logger.warn("onImportBtnClicked(): Selected file is null, aborting.");
			return;
		}
		
		try {
			attemptImportRecipes(Path.of(selectedFile.getAbsolutePath()));
		} catch (IOException e) {
			logger.error("onImportBtnClicked(): IOException encountered: {}", e);
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
					logger.error("onImportZoneDragDropped(): IOException encountered: {}", e);
				}
	        } else {
	        	// TODO user dialog
				logger.error("onImportBtnClicked(): File dropped into import zone was not a .json file.");
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
			logger.error("attemptExportRecipes(): IOException encountered: {}", e);
		}
	}

	private void attemptImportRecipes(Path path) 
			throws FileNotFoundException, JsonMappingException,
			JsonProcessingException, IOException {
		List<Recipe> importedRecipes = new ArrayList<>();

		String json = Files.readString(path);

		try {
			importedRecipes = impExpService.importRecipes(json);
		} catch (JsonMappingException e) {
			logger.error("attemptImportRecipes(): JsonMappingException encountered: {}", e);
		} catch (JsonProcessingException e) {
			logger.error("attemptImportRecipes(): JsonProcessingException encountered: {}", e);
		}
		

		for (Recipe recipe : importedRecipes) {
			recipeService.addRecipe(recipe);
		}
	}

	public void setRecipeService(RecipeService recipeService) {
		this.recipeService = recipeService;
	}

}
