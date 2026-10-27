package ca.prepledger.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.prepledger.config.AppConfig;
import ca.prepledger.config.Configurable;
import ca.prepledger.model.Ingredient;
import ca.prepledger.model.Recipe;
import ca.prepledger.navigation.ContextArea;
import ca.prepledger.navigation.Navigable;
import ca.prepledger.navigation.NavigationHandler;
import ca.prepledger.service.RecipeAPIService;
import ca.prepledger.service.RecipeService;
import ca.prepledger.session.UserSession;
import ca.prepledger.util.PrepLedgerDialog;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class NewRecipeController implements Navigable, Configurable {

	@FXML private Button cancelButton;
	@FXML private Button saveRecipeButton;
	@FXML private Button addIngredientButton;
	@FXML private TextField recipeTitleField;
	@FXML private TextField recipeTagsField;
	@FXML private TextArea instructionsTextArea;
	@FXML private VBox ingredientsVBox;
	@FXML private Button navBackButton;
	@FXML private Label recipeCardHeader;
	
	private NavigationHandler navigationHandler;
	private List<IngredientRowController> ingredientRowControllers = new ArrayList<>();
	private RecipeService recipeService;
	private boolean editMode = false;
	private Recipe currentRecipe;	
	private AppConfig appConfig;
	private UserSession currentSession;
	private RecipeAPIService recipeApiService;
	private static final Logger logger = LoggerFactory.getLogger(NewRecipeController.class);


	//////////////////////////////
	/// 
	/// FXML Methods
	/// 
	//////////////////////////////

	@FXML
	private void initialize() {
		Platform.runLater(() -> recipeTitleField.requestFocus());
	}

	@FXML
	public void onNavBackButtonClicked() {
		try {
			navigationHandler.navigateTo(ContextArea.RECIPES);
		} catch (IOException e) {
			logger.error("onNavBackButtonClicked(): IOException encountered: {}", e);
		}
	}

	@FXML
	public void onCancelButtonClicked() {
		goBackToRecipesList();
	}

	@FXML
	public void onSaveRecipeButtonClicked() {
		List<Ingredient> ingredients = new ArrayList<>();
		List<String> errors = new ArrayList<>();
		boolean isRecipeValid = false;
		boolean doesRecipeHaveTitle = false;
		boolean areIngredientFieldsValid = false;

		// Check that user entered a recipe title	
		doesRecipeHaveTitle = hasUserEnteredRecipeTitle();

		String lastIngredient = "";
		
		// Fetch ingredients
		try {
		
		    for (IngredientRowController c : ingredientRowControllers) {
		    	lastIngredient = c.getIngredientName();
		        ingredients.add(c.getIngredient());
		    }
	    
		} catch (NumberFormatException e) {
			PrepLedgerDialog dialog = new PrepLedgerDialog();
			dialog.showInvalidRecipeDialog(
					"Invalid amount for ingredient " + lastIngredient);
			logger.info("onSaveRecipeButtonClicked(): "
					+ "Invalid amount detected for ingredient {}.", lastIngredient);
			return;
		}

		// Validate ingredients
		errors = areIngredientsValid(ingredients);
		
		// If no errors from ingredient field validation, then all ingredients are good
		areIngredientFieldsValid = errors.isEmpty();

		// At this point, determine if the recipe is valid. if not, show error
		isRecipeValid = (doesRecipeHaveTitle && areIngredientFieldsValid);
		
		// The recipe is invalid for some reason. Could be no title, malformed ingredients, or both.
		if (!isRecipeValid) {
			StringBuilder message = new StringBuilder();
			
			if (!doesRecipeHaveTitle) {
				message.append("• ").append("Please enter a recipe title.\n");
			}
				
			if (!areIngredientFieldsValid) {
				for (String e : errors) {
					message.append("• ").append(e).append("\n");
				}
			}			
			
		    Alert alert = new Alert(Alert.AlertType.WARNING);
		    alert.setTitle("Cannot Save Recipe");
		    alert.setHeaderText("Please correct the following issues:");
		    alert.getDialogPane().getStylesheets().add(
    				getClass().getResource("/css/components/alert.css").toExternalForm());
		    alert.setContentText(message.toString());
		    alert.showAndWait();
		    
			logger.info("onSaveRecipeButtonClicked(): Cannot save invalid recipe.");
			return;
		}
		
		// Construct Recipe object
		Recipe newRecipe = constructRecipeFromRemainingFields(ingredients);
		
		// propogate recipe to recipeservice
		if (editMode) {
		    // need to preserve existing id
		    newRecipe.setId(currentRecipe.getId());

		    Task<Recipe> task = new Task<>() {
		        @Override
		        protected Recipe call() throws Exception {
		            return recipeApiService.updateRecipe(
		            		currentSession,
		            		newRecipe,
		            		newRecipe.getId());
		        }
		    };

		    task.setOnSucceeded(event -> {
		        Recipe updatedRecipe = task.getValue();
		        int rcpIdx = recipeService.getRecipeIndex(currentRecipe);	        
		        recipeService.updateRecipe(rcpIdx, updatedRecipe);
		        goBackToRecipesList();
		    });

		    task.setOnFailed(event -> {
				logger.error("Failed to update recipe on backend: {}", task.getException());
				PrepLedgerDialog dialog = new PrepLedgerDialog();
				dialog.showRecipeBackendActionFailedDialog(
						task.getException().getMessage(),
						"update");
		    });

		    Thread thread = new Thread(task, "recipe-update");
		    thread.setDaemon(true);
		    thread.start();

		    return;
		} else {
			Task<Recipe> task = new Task<>() {
				@Override
				protected Recipe call() throws Exception {
					recipeApiService.createRecipe(currentSession, newRecipe);
					return null;
				}			
			};
			
			task.setOnSucceeded(e -> {
				recipeService.addRecipe(newRecipe);
				goBackToRecipesList();
			});
			
			task.setOnFailed(e -> {
				logger.error("Failed to create recipe on backend: {}", task.getException());
				PrepLedgerDialog dialog = new PrepLedgerDialog();
				dialog.showRecipeBackendActionFailedDialog(
						task.getException().getMessage(),
						"create");
			});
			
			Thread thread = new Thread(task, "recipe-create");
			thread.setDaemon(true);
			thread.start();
			return;
		}

	}

	@FXML
	public void onAddIngredientButtonClicked() {
		try {
			addNewIngredientRow(appConfig.areTooltipsOn() ? true : false);
		} catch (IOException e) {
			logger.error("onAddIngredientButtonClicked(): IOException encountered: {}", e);
		}
	}


	//////////////////////////////
	/// 
	/// Other Methods
	/// 
	//////////////////////////////	
	
	public void setRecipeToEdit(Recipe recipe) {
		this.currentRecipe = recipe;
		editMode = true;
		
		populateFields(recipe);
		recipeCardHeader.setText("Edit Recipe");
	}
	
	private void populateFields(Recipe recipe) {
		recipeTitleField.setText(recipe.getTitle());
		instructionsTextArea.setText(recipe.getDirections());
		
		// Manage tags
		String tagsStr = null;
		List<String> tags = recipe.getTags();
		
		if (!tags.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			
			for (String tag : tags) {
				if (sb.length() > 0) {
					sb.append(", ");
				}
				
				sb.append(tag);
			}
			
			tagsStr = sb.toString();
		}
		
		recipeTagsField.setText(tagsStr);
		
		// Add new ingredientrow.fxml for each ingredient
		for (Ingredient ing : recipe.getIngredients()) {
			try {
				addNewIngredientRow(ing);
			} catch (IOException e) {
				logger.error("populateFields(): IOException encountered: {}", e);
			}
		}
	}
	
	private void goBackToRecipesList() {
		try {
			navigationHandler.navigateTo(ContextArea.RECIPES);
		} catch (IOException e) {
			logger.error("goBackToRecipesList(): IOException encountered: {}", e);
		}
	}

	void addNewIngredientRow(boolean withPromptText) throws IOException {
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource("/fxml/recipes/IngredientRow.fxml"));
		Parent ingredientRow = loader.load();
		IngredientRowController controller = loader.getController();

		// Keep track of the row's controller
		ingredientRowControllers.add(controller);

		// Wire the callback for when row's delete button is pressed
		controller.setOnDelete(this::removeIngredientRow);

		// Add at 2nd last index so "Add Ingredient" button is last
		ObservableList<Node> children = ingredientsVBox.getChildren();
		children.add(children.size() - 1, ingredientRow);
		
		if (withPromptText) {
			controller.setDefaultPromptText();
		}

		// Request focus in the "name" field
		controller.requestFocusInNameTextField();
	}
	
	private void addNewIngredientRow(Ingredient ingredient) throws IOException {
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource("/fxml/recipes/IngredientRow.fxml"));
		Parent ingredientRow = loader.load();
		IngredientRowController controller = loader.getController();

		// Keep track of the row's controller
		ingredientRowControllers.add(controller);

		// Wire the callback for when row's delete button is pressed
		controller.setOnDelete(this::removeIngredientRow);
		
		// Set the ingredient fields to the data from passed ingredient
		controller.setIngredient(ingredient);

		// Add at 2nd last index so "Add Ingredient" button is last
		ObservableList<Node> children = ingredientsVBox.getChildren();
		children.add(children.size() - 1, ingredientRow);

		// Request focus in the "name" field
		controller.requestFocusInNameTextField();
	}

	private void removeIngredientRow(IngredientRowController controller) {
		int vboxElementsPlusOneIngredientMinimum = 4;
		ObservableList<Node> children = ingredientsVBox.getChildren();

		/* Every recipe should have at least one ingredient. This ensures users
		 * can't delete the only ingredient row in the screen. */
		if (children.size() > vboxElementsPlusOneIngredientMinimum) {
			ingredientRowControllers.remove(controller);
			ingredientsVBox.getChildren().remove(controller.getRoot());
		}
	}

	/**
	 * Check that the user has filled in all required fields
	 */
	private boolean hasUserEnteredRecipeTitle() {
		String rcpTitle = recipeTitleField.getText().trim();

		if (rcpTitle != null && !rcpTitle.isEmpty()) {
			return true;
		}

		return false;
	}

	private List<String> areIngredientsValid(List<Ingredient> ingredients) {
		List<String> errors = new ArrayList<>();
		
	    for (Ingredient ingredient : ingredients) {
	        if (ingredient.getName() == null) {
	            errors.add("An ingredient is missing its name.");
	        }
	        
	        String name = ingredient.getName();

	        try {
				String amount = ingredient.getAmount();
				
				if (amount == null && ingredient.getUnit() != null) {
				    errors.add("Ingredient '" + name
				    + "': a unit cannot be specified without an associated amount.");
				}
				
			} catch (ArithmeticException e) {
				errors.add("Ingredient " + name + ": the amount is malformed or invalid.");
				logger.warn("areIngredientsValid(): Invalid amount for ingredient ", name);
			}
	    }

	    return errors;
	}
	
	private Recipe constructRecipeFromRemainingFields(List<Ingredient> ingredients) {
		Recipe newRecipe = null;
		String title = recipeTitleField.getText().trim();
		String tagsText = recipeTagsField.getText();
		String[] tags = (tagsText == null || tagsText.isBlank())
		        ? new String[0]
		        : tagsText.split("\\s*,\\s*");
		String instructions = instructionsTextArea.getText().trim();
		
		newRecipe = new Recipe(title, ingredients, instructions, tags);
		
		return newRecipe;
	}
	
	public void setRecipeTooltips() {
		recipeTitleField.setPromptText("e.g. Massaman Curry");
		recipeTagsField.setPromptText("e.g. Sauce, Pastry, Seafood");
		instructionsTextArea.setPromptText("Add step-by-step instructions...");
	}

	@Override
	public void setNavigationHandler(NavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;		
	}
	
	public void setRecipeService(RecipeService recipeService) {
		this.recipeService = recipeService;
	}

	@Override
	public void setAppConfig(AppConfig appConfig) {
		this.appConfig = appConfig;
	}

	public void setCurrentSession(UserSession currentSession) {
		this.currentSession = currentSession;
	}

	public void setRecipeApiService(RecipeAPIService recipeApiService) {
		this.recipeApiService = recipeApiService;
	}

}
