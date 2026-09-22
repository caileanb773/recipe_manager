package ca.prepledger.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.prepledger.model.Ingredient;
import ca.prepledger.model.Recipe;
import ca.prepledger.navigation.ContextArea;
import ca.prepledger.navigation.NavigationHandler;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class RecipeViewController {

	@FXML
	private Button navBackButton;

	@FXML
	private Label recipeTitleHeader;

	@FXML
	private Button editButton;

	@FXML
	private Button removeButton;

	@FXML
	private HBox tagsHBox;

	@FXML
	private Tab overviewTab;

	@FXML
	private Tab ingredientsTab;

	@FXML
	private Tab directionsTab;

	@FXML
	private VBox ingredientsVBox;
	
	@FXML
	private TextArea directionsTextArea;
	
	// Specific to the "Overview" tabpane
	@FXML
	private VBox recipeOverviewIngredientsVBox;

	// Specific to the "Overview" tabpane
	@FXML
	private TextArea recipeOverviewDirectionsTextArea;
	
	private Recipe recipe;

	private NavigationHandler navigationHandler;

	private boolean hasOverviewTabBeenClicked = false;
	
	private boolean hasIngredientsTabBeenClicked = false;
	
	private boolean hasDirectionsTabBeenClicked = false;

	private ArrayList<CollapsibleIngredientRowController> ingredientRowControllers = new ArrayList<>();

	
	public void setNavigationHandler(AppShellController appShellController) {
		navigationHandler = appShellController;
	}

	public void setRecipeToView(Recipe recipe) {
		this.recipe = recipe;

		// Set the title header
		recipeTitleHeader.setText(recipe.getTitle());

		// Add tags
		List<String> tags = recipe.getTags();
		int numTags = tags.size();

		if (numTags > 0) {
			for (int i = 0; i < numTags; i++) {
				Label tagLabel;

				if (i < numTags - 1) {
					tagLabel = new Label(tags.get(i) + ", ");
				} else {
					tagLabel = new Label(tags.get(i));
				}

				tagLabel.getStyleClass().add("sub-header");
				tagsHBox.getChildren().add(tagLabel);
			}
		}

	}

	public void addNewIngredientRow(
			Ingredient ingredient,  ObservableList<Node> children)
					throws IOException {
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource("/fxml/recipes/CollapsibleIngredientRow.fxml"));
		Parent ingredientRow = loader.load();
		CollapsibleIngredientRowController controller = loader.getController();

		// Keep track of the row's controller
		ingredientRowControllers.add(controller);

		// Wire the callback for when row's delete button is pressed
		controller.setIngredient(ingredient);

		children.add(ingredientRow);
	}

	@FXML
	private void onNavBackButtonClicked() {
		try {
			navigationHandler.navigateTo(ContextArea.RECIPES);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	private void onEditButtonClicked() {
		try {
			navigationHandler.navigateTo(ContextArea.EDIT_RECIPE, recipe);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	private void onRemoveRecipeButtonClicked() {

	}

	@FXML
	private void onIngredientsSelectionChanged() {
		populateIngredients();
	}
	
	private void populateIngredients() {
		if (recipe == null) {
			return;
		}
		
		if (!ingredientsTab.isSelected()) {
			return;
		}
		
		ObservableList<Node> children = ingredientsVBox.getChildren();

		// Only load elements once
		if (!hasIngredientsTabBeenClicked) {
			hasIngredientsTabBeenClicked = true;

			// Add new ingredientrow.fxml for each ingredient
			for (Ingredient ing : recipe.getIngredients()) {
				try {
					addNewIngredientRow(ing, children);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@FXML
	private void onOverviewSelectionChanged() {
		populateOverview();
	}
	
	public void populateOverview() {
		if (recipe == null) {
			return;
		}
		
		if (!overviewTab.isSelected()) {
			return;
		}
		
		// Only load elements once
		if (!hasOverviewTabBeenClicked) {
			hasOverviewTabBeenClicked = true;
			
			recipeOverviewDirectionsTextArea.setText(recipe.getDirections());
			
			// Add new ingredientrow.fxml for each ingredient
			
			ObservableList<Node> children = recipeOverviewIngredientsVBox.getChildren();
			for (Ingredient ing : recipe.getIngredients()) {
				try {
					addNewIngredientRow(ing, children);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@FXML
	private void onDirectionsSelectionChanged() {
		populateDirections();
	}
	
	private void populateDirections() {
		if (recipe == null) {
			return;
		}
		
		if (!directionsTab.isSelected()) {
			return;
		}
		
		// Only load elements once
		if (!hasDirectionsTabBeenClicked) {
			hasDirectionsTabBeenClicked = true;
			
			directionsTextArea.setText(recipe.getDirections());
		}
	}

}
