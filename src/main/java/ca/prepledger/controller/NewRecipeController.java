package ca.prepledger.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.prepledger.model.Ingredient;
import ca.prepledger.model.Recipe;
import ca.prepledger.navigation.ContextArea;
import ca.prepledger.navigation.Navigable;
import ca.prepledger.navigation.NavigationHandler;
import ca.prepledger.service.RecipeService;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class NewRecipeController implements Navigable {

	@FXML
	private Button cancelButton;

	@FXML
	private Button saveRecipeButton;

	@FXML
	private Button addIngredientButton;

	@FXML
	private TextField recipeTitleField;

	@FXML
	private TextField recipeTagsField;

	@FXML
	private TextArea instructionsTextArea;
	
	@FXML
	private VBox ingredientsVBox;
	
	@FXML
	private Button navBackButton;
	
	private NavigationHandler navigationHandler;
	
	private List<IngredientRowController> ingredientRowControllers = new ArrayList<>();
	
	private RecipeService recipeService;
	
	private static final boolean WITH_PROMPT_TXT = true;
	
	private static final boolean WITHOUT_PROMPT_TXT = false;
	
	// Edit Mode
	private boolean editMode = false;
	
	private Recipe currentRecipe;


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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	public void onCancelButtonClicked() {
		goBackToRecipesList();
	}

	@FXML
	public void onSaveRecipeButtonClicked() {
		System.out.println("aaaa");
		List<Ingredient> ingredients = new ArrayList<>();
		boolean isRecipeValid = false;
		boolean areRecipeFieldsValid = false;
		boolean areIngredientFieldsValid = false;

		// Check validity of required fields (recipe title, ingredient title)		
		areRecipeFieldsValid = areRequiredRecipeFieldsPopulated();

		// Fetch ingredients
		for (IngredientRowController c : ingredientRowControllers) {
			ingredients.add(c.getIngredient());
		}

		// Validate ingredients
		areIngredientFieldsValid = areRequiredIngredientFieldsPopulated(ingredients);

		// At this point, determine if the recipe is valid. if not, show error
		isRecipeValid = (areRecipeFieldsValid && areIngredientFieldsValid);
		
		// TODO show error if not valid, finish this later
		if (!isRecipeValid) {
			// show an error
			if (!areRecipeFieldsValid) {
				// TODO do something
			} else {
				// TODO do something
			}
			System.out.println("invalid!");
			return;
		}
		
		// Construct Recipe object
		Recipe newRecipe = constructRecipeFromRemainingFields(ingredients);
		
		// propogate recipe to recipeservice
		if (editMode) {
			System.out.println("edit mode");
			// get the index of the recipe in service
			int rcpIdx = recipeService.getRecipeIndex(currentRecipe);
			recipeService.updateRecipe(rcpIdx, newRecipe);
		} else {
			recipeService.addRecipe(newRecipe);
		}

		// send recipe to recipeservice
		System.out.println("boutta go back");
		goBackToRecipesList();
	}

	@FXML
	public void onAddIngredientButtonClicked() {
		try {
			addNewIngredientRow(WITHOUT_PROMPT_TXT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void goBackToRecipesList() {
		try {
			navigationHandler.navigateTo(ContextArea.RECIPES);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	private boolean areRequiredRecipeFieldsPopulated() {
		String rcpTitle = recipeTitleField.getText().trim();

		if (rcpTitle != null && !rcpTitle.isEmpty()) {
			return true;
		}

		return false;
	}

	private boolean areRequiredIngredientFieldsPopulated(List<Ingredient> ingredients) {
		for (Ingredient ingredient : ingredients) {
			String ingredientTitle = ingredient.getName();

			if (ingredientTitle == null) {
				return false;
			}
		}

		return true;
	}
	
	private Recipe constructRecipeFromRemainingFields(List<Ingredient> ingredients) {
		Recipe newRecipe = null;
		String title = recipeTitleField.getText().trim();
		String[] tags = recipeTagsField.getText().split("\\s*,\\s*");
		String instructions = instructionsTextArea.getText().trim();
		
		newRecipe = new Recipe(title, ingredients, instructions, tags);
		
		return newRecipe;
	}

	@Override
	public void setNavigationHandler(NavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;		
	}
	
	public void setRecipeService(RecipeService recipeService) {
		this.recipeService = recipeService;
	}

}
