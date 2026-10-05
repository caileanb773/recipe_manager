package ca.prepledger.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
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
	private ToggleGroup exportToggleGroup;
	
	@FXML
	private RadioButton allRecipesRadioBtn;
	
	@FXML
	private RadioButton selectedRecipesRadioBtn;

	
	@FXML
	public void onExportBtnClicked(ActionEvent event) {
	    List<Recipe> recipesToExport = recipeService.getAllRecipes();

	    // Check if we have any recipes
	    if (recipesToExport.isEmpty()) {
	        logger.info("onExportBtnClicked(): No recipes to export.");
	        showExportOperationError();
	        return;
	    }
	    
	    
	    // Check if we're exporting all or a subset of the recipes
	    if (selectedRecipesRadioBtn.isSelected()) {
	    	recipesToExport = showRecipeSelectionDialog(recipesToExport);
	    }

	    // Get a path from the user
	    FileChooser chooser = new FileChooser();
	    chooser.setTitle("Save .json File");
	    chooser.getExtensionFilters().add(
	            new ExtensionFilter("Recipe Collections", "*.json"));
	    
	    Path defaultPath = Path.of(System.getProperty("user.home"), "Documents");

	    // Set default directory if it exists
	    if (Files.isDirectory(defaultPath)) {
	        chooser.setInitialDirectory(defaultPath.toFile());
	    }

	    Window window = ((Node) event.getSource()).getScene().getWindow();
	    File selectedFile = chooser.showSaveDialog(window);

	    // User cancelled
	    if (selectedFile == null) {
	        logger.info("onExportBtnClicked(): Export cancelled.");
	        return;
	    }

	    Path path = selectedFile.toPath();

	    // Confirm before overwriting an existing file
	    if (Files.exists(path)) {
	        Alert confirm = new Alert(
	                Alert.AlertType.CONFIRMATION,
	                "The selected file already exists. Do you want to overwrite it?",
	                ButtonType.YES,
	                ButtonType.NO

	        );

	        confirm.setTitle("Confirm Export");
	        confirm.setHeaderText("File already exists");
    		confirm.getDialogPane().getStylesheets().add(
    				getClass().getResource("/css/components/alert.css").toExternalForm());

	        Optional<ButtonType> result = confirm.showAndWait();

	        if (result.isEmpty() || result.get() != ButtonType.YES) {
	            logger.info("onExportBtnClicked(): Export cancelled by user.");
	            return;
	        }
	    }

	    try {
	        attemptExportRecipes(recipesToExport, path.toString());
	        logger.info("onExportBtnClicked(): Recipes exported successfully to {}", path);
	    } catch (JsonProcessingException e) {
	        logger.error("onExportBtnClicked(): JsonProcessingException encountered: {}", e);
	        showExportOperationError();
	    }
	}
	
	private List<Recipe> showRecipeSelectionDialog(List<Recipe> recipes) {
	    Dialog<List<Recipe>> dialog = new Dialog<>();
	    dialog.setTitle("Select Recipes");
	    dialog.setHeaderText("Select the recipes you want to export.");

	    ButtonType exportButton = new ButtonType(
	            "Export",
	            ButtonBar.ButtonData.OK_DONE
	    );
	    ButtonType cancelButton = new ButtonType(
	            "Cancel",
	            ButtonBar.ButtonData.CANCEL_CLOSE
	    );

	    dialog.getDialogPane().getButtonTypes().addAll(
	            exportButton,
	            cancelButton
	    );

	    VBox recipeList = new VBox(8);

	    List<CheckBox> checkBoxes = new ArrayList<>();

	    for (Recipe recipe : recipes) {
	        CheckBox checkBox = new CheckBox(recipe.getTitle());
	        checkBoxes.add(checkBox);
	        recipeList.getChildren().add(checkBox);
	    }

	    ScrollPane scrollPane = new ScrollPane(recipeList);
	    scrollPane.setFitToWidth(true);
	    scrollPane.setPrefHeight(400);
	    scrollPane.setPrefWidth(350);

	    dialog.getDialogPane().setContent(scrollPane);

	    dialog.setResultConverter(button -> {
	        if (button == exportButton) {
	            List<Recipe> selectedRecipes = new ArrayList<>();

	            for (int i = 0; i < checkBoxes.size(); i++) {
	                if (checkBoxes.get(i).isSelected()) {
	                    selectedRecipes.add(recipes.get(i));
	                }
	            }

	            return selectedRecipes;
	        }

	        return null;
	    });

	    Optional<List<Recipe>> result = dialog.showAndWait();

	    return result.orElse(null);
	}

	@FXML
	public void onImportBtnClicked(ActionEvent event)  {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Open .json File");
		chooser.getExtensionFilters().add(
				new ExtensionFilter("Recipe Collections", "*.json"));
		Window window = ((Node)event.getSource()).getScene().getWindow();
		
	    Path defaultPath = Path.of(System.getProperty("user.home"), "Documents");

	    // Set default directory if it exists
	    if (Files.isDirectory(defaultPath)) {
	        chooser.setInitialDirectory(defaultPath.toFile());
	    }
		
		File selectedFile = chooser.showOpenDialog(window);
				
		if (selectedFile == null) {
			logger.info("onImportBtnClicked(): Import cancelled.");
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
	        	showImportOperationError("The provided file was not a valid JSON file.");
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

	private void attemptExportRecipes(List<Recipe> recipes, String path)
			throws JsonProcessingException {
		try {
			impExpService.exportRecipes(recipes, path);
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
		
		displayPostImportDialog(importedRecipes.size());
	}
	
	private void displayPostImportDialog(int numImported) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Import Complete");
		alert.setHeaderText("Recipes imported successfully");
		alert.setContentText(numImported + " recipes were imported.");
		alert.getDialogPane().getStylesheets().add(
			    getClass().getResource("/css/components/alert.css").toExternalForm()
			);
		
		alert.showAndWait();
	}
	
	private void showImportOperationError(String contentString) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Import Error");
		alert.setHeaderText("Could not complete import operation.");
		alert.setContentText(contentString);
		alert.getDialogPane().getStylesheets().add(
				getClass().getResource("/css/components/alert.css").toExternalForm());
		
		alert.showAndWait();
	}
	
	private void showExportOperationError() {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Export Error");
		alert.setHeaderText("Could not complete export operation.");
		alert.setContentText("No recipes to export!");
		alert.getDialogPane().getStylesheets().add(
				getClass().getResource("/css/components/alert.css").toExternalForm());
		
		alert.showAndWait();
	}

	public void setRecipeService(RecipeService recipeService) {
		this.recipeService = recipeService;
	}

}
