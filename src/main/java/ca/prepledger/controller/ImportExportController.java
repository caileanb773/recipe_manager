package ca.prepledger.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import ca.prepledger.model.Recipe;
import ca.prepledger.service.ImportExportService;
import ca.prepledger.service.RecipeAPIService;
import ca.prepledger.service.RecipeService;
import ca.prepledger.session.UserSession;
import ca.prepledger.util.PrepLedgerDialog;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

/**
 * Controller class which handles input from the view and delegates actions.
 * 
 * @author Cailean Bernard
 * @since v0.1.0
 */
public class ImportExportController {

	@FXML private Button exportBtn;
	@FXML private Button importBtn;
	@FXML private VBox importDropzone;
	@FXML private ToggleGroup exportToggleGroup;
	@FXML private RadioButton allRecipesRadioBtn;
	@FXML private RadioButton selectedRecipesRadioBtn;

	private RecipeService recipeService;
	private RecipeAPIService recipeApiService;
	private UserSession userSession;
	private static final Logger logger = LoggerFactory.getLogger(ImportExportController.class);

	
	@FXML
	public void onExportBtnClicked(ActionEvent event) {
	    List<Recipe> recipesToExport = recipeService.getAllRecipes();

	    // Check if we have any recipes
	    if (recipesToExport.isEmpty()) {
	        logger.info("onExportBtnClicked(): No recipes to export.");
	        PrepLedgerDialog.showExportOperationError();
	        return;
	    }
	    
	    // Check if we're exporting all or a subset of the recipes
	    if (selectedRecipesRadioBtn.isSelected()) {
	    	recipesToExport = PrepLedgerDialog.showRecipeSelectionDialog(
	    			recipesToExport);
	    	
	    	if (recipesToExport == null || recipesToExport.isEmpty()) {
		        logger.info("onExportBtnClicked(): Export cancelled.");
	    		return;
	    	}
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

	    try {
	        attemptExportRecipes(recipesToExport, path.toString());
	        logger.info("onExportBtnClicked(): Recipes exported successfully to {}", path);
	    } catch (JsonProcessingException e) {
	        logger.error("onExportBtnClicked(): JsonProcessingException encountered: {}", e);
	        PrepLedgerDialog.showExportOperationError();
	    }
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
		} catch (JsonProcessingException e) {
			logger.error("onImportBtnClicked(): JsonProcessingException encountered.");
			PrepLedgerDialog.showCorruptJSONImportErrorDialog();
		} catch (IOException e) {
			logger.error("onImportBtnClicked(): IOException encountered.");
			PrepLedgerDialog.showImportOperationError("An Input/Output error "
					+ "occurred during import. Please report this error along "
					+ "with a description of what caused it to the bug reporting "
					+ "service.");
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
	        	} catch (JsonProcessingException e) {
	        		logger.error("onImportZoneDragDropped(): IOException encountered");
	        		PrepLedgerDialog.showCorruptJSONImportErrorDialog();
				} catch (IOException e) {
					logger.error("onImportZoneDragDropped(): IOException encountered.");
					PrepLedgerDialog.showCorruptJSONImportErrorDialog();
				}
	        } else {
				logger.error("onImportBtnClicked(): File dropped into import zone was not a .json file.");
				PrepLedgerDialog.showImportOperationError(
						"The provided file was not a valid JSON file.");
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
			ImportExportService.exportRecipes(recipes, path);
		} catch (IOException e) {
			logger.error("attemptExportRecipes(): IOException encountered: {}", e);
		}
	}

	private void attemptImportRecipes(Path path)
	        throws IOException {

	    String json = Files.readString(path);
	    List<Recipe> importedRecipes = ImportExportService.importRecipes(json);

	    Task<List<Recipe>> task = new Task<>() {
	        @Override
	        protected List<Recipe> call() throws Exception {
	            return recipeApiService.importRecipes(userSession, importedRecipes);
	        }
	    };

	    task.setOnSucceeded(e -> {
	        List<Recipe> savedRecipes = task.getValue();

	        recipeService.addRecipes(savedRecipes);
	        PrepLedgerDialog.displayPostImportDialog(savedRecipes.size());
	    });

	    task.setOnFailed(e -> {
	        logger.error("Failed to import recipes to backend: {}", task.getException());

	        PrepLedgerDialog.showRecipeBackendActionFailedDialog(
	                task.getException().getMessage(),
	                "import"
	        );
	    });

	    Thread thread = new Thread(task, "recipe-import");
	    thread.setDaemon(true);
	    thread.start();
	}
	
	public void setRecipeService(RecipeService recipeService) {
		this.recipeService = recipeService;
	}
	
	public void setUserSession(UserSession userSession) {
		this.userSession = userSession;
	}
	
	public void setRecipeApiService(RecipeAPIService recipeApiService) {
		this.recipeApiService = recipeApiService;
	}

}
