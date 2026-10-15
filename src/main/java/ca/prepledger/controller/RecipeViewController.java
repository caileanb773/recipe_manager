package ca.prepledger.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.prepledger.model.Fraction;
import ca.prepledger.model.Ingredient;
import ca.prepledger.model.Recipe;
import ca.prepledger.navigation.ContextArea;
import ca.prepledger.navigation.NavigationHandler;
import ca.prepledger.util.RecipeDialog;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class RecipeViewController {

	// FXML
	@FXML private Button navBackButton;
	@FXML private Label recipeTitleHeader;
	@FXML private Button editButton;
	@FXML private Button removeButton;
	@FXML private HBox tagsHBox;
	@FXML private HBox scalingHBox;
	@FXML private Label batchScaleLabel;
	@FXML private Button scaleMinusButton;
	@FXML private TextField scaleAdjustmentField;
	@FXML private Button scalePlusBtn;
	@FXML private Button helpBtn;
	@FXML private Tab overviewTab;
	@FXML private Tab ingredientsTab;
	@FXML private Tab directionsTab;
	@FXML private VBox ingredientsVBox;
	@FXML private TextArea directionsTextArea;

	// Specific to the "Overview" tabpane
	@FXML private VBox recipeOverviewIngredientsVBox; 	
	@FXML private TextArea recipeOverviewDirectionsTextArea;

	// Other
	private Recipe recipe;
	private BigDecimal currentScale;
	private BigDecimal adjustmentFactor;
	private static final BigDecimal MINIMUM_SCALE = new BigDecimal("0.1");
	private static final BigDecimal MAXIMUM_SCALE = new BigDecimal("100");
	private NavigationHandler navigationHandler;
	private boolean hasOverviewTabBeenClicked = false;
	private boolean hasIngredientsTabBeenClicked = false;
	private boolean hasDirectionsTabBeenClicked = false;
	private ArrayList<CollapsibleIngredientRowController> ingredientRowControllers;
	private static final Logger logger = LoggerFactory.getLogger(RecipeViewController.class);


	@FXML
	private void initialize() {
		ingredientRowControllers = new ArrayList<>();
	}
	
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
	
	public Recipe getViewedRecipe() {
		return this.recipe;
	}

	public void setScaleProperties() {
		resetScaleAndAdjustmentFactor();
		applyScaleAndAdjustment();

		scaleAdjustmentField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				onScaleAdjustmentFieldChange();
			}
		});
	}
	
	private void resetScaleAndAdjustmentFactor() {
		currentScale = BigDecimal.ONE;
		adjustmentFactor = new BigDecimal("0.5");
	}
	
	private void applyScaleAndAdjustment() {
		batchScaleLabel.setText(currentScale.stripTrailingZeros().toPlainString() + "×");
		scaleAdjustmentField.setText(adjustmentFactor.toString());
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
		controller.initializeIngredientFields();

		children.add(ingredientRow);
	}

	@FXML
	private void onNavBackButtonClicked() {
		try {
			navigationHandler.navigateTo(ContextArea.RECIPES);
		} catch (IOException e) {
			logger.error("onNavBackButtonClicked(): IOException encountered: {}", e);
		}
	}

	@FXML
	private void onEditButtonClicked() {
		try {
			navigationHandler.navigateTo(ContextArea.EDIT_RECIPE, recipe);
		} catch (IOException e) {
			logger.error("onEditButtonClicked(): IOException encountered: {}", e);
		}
	}

	@FXML
	private void onRemoveRecipeButtonClicked() {
		RecipeDialog dialog = new RecipeDialog();
		RecipeDialog.Choice choice = dialog.confirmDeletion(recipe);

		if (choice == RecipeDialog.Choice.CANCEL) {
			logger.info("attemptDeleteRecipe(): Cancelling delete of recipe: "
					+ recipe.getTitle());
			return;
		}
		
		try {
			navigationHandler.navigateTo(ContextArea.RECIPES, recipe);
		} catch (IOException e) {
			logger.error("onRemoveRecipeButtonClicked(): IOException encountered: {}", e);
		}
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
					logger.error("populateIngredients(): IOException encountered: {}", e);
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
					logger.error("populateOverview(): IOException encountered: {}", e);
				}
			}
		}
	}

	@FXML
	private void onDirectionsSelectionChanged() {
		populateDirections();
	}

	@FXML
	private void onScaleAdjustmentFieldChange() {
		parseAdjustmentFieldInput();
	}

	private void parseAdjustmentFieldInput() {
	    String input = scaleAdjustmentField.getText().trim();
	    BigDecimal parsedValue;

	    try {
	        if (Fraction.isFraction(input)) {
	            Fraction parsedFraction = Fraction.parseFraction(input);
	            parsedValue = Fraction.toBigDecimal(parsedFraction);
	        } else {
	            parsedValue = new BigDecimal(input);
	        }
	    } catch (NumberFormatException e) {
	        logger.warn("parseAdjustmentFieldInput(): Invalid input.");
	        resetAdjustmentField();
	        return;
	    }

	    if (parsedValue.compareTo(MINIMUM_SCALE) < 0
	            || parsedValue.compareTo(MAXIMUM_SCALE) > 0) {
	        logger.warn("parseAdjustmentFieldInput(): Adjustment out of range.");
	        resetAdjustmentField();
	        return;
	    }

	    adjustmentFactor = parsedValue;
	    resetAdjustmentField();
	}

	private void resetAdjustmentField() {
	    scaleAdjustmentField.setText(
	            adjustmentFactor.stripTrailingZeros().toPlainString());
	}
	
	@FXML
	private void onScaleMinusBtnClicked() {
		// subtract the current scale by the new scale
		BigDecimal newScale = currentScale.subtract(adjustmentFactor);
		
		// check that the number isn't below minimum (0.1)
		if (newScale.compareTo(MINIMUM_SCALE) < 0) {
			newScale = MINIMUM_SCALE;
		}

		// call method that sets ingredient amounts based on new scale
		currentScale = newScale;
		adjustIngredientDisplayScale();
	}


	@FXML
	private void onScalePlusBtnClicked() {
		// add the current scale by the new scale
		BigDecimal newScale = currentScale.add(adjustmentFactor);
		
		// check that the number isn't above maximum (100)
		if (newScale.compareTo(MAXIMUM_SCALE) > 0) {
			newScale = MAXIMUM_SCALE;
		}

		// call method that sets ingredient amounts based on new scale
		currentScale = newScale;
		adjustIngredientDisplayScale();
	}
	
	@FXML
	private void onScaleResetBtnClicked() {
		resetScaleAndAdjustmentFactor();
		applyScaleAndAdjustment();
		
		for (CollapsibleIngredientRowController c : ingredientRowControllers) {
			c.initializeIngredientFields();
		}
	}

	private void adjustIngredientDisplayScale() {
	    batchScaleLabel.setText(currentScale.stripTrailingZeros().toPlainString() + "×");

	    for (CollapsibleIngredientRowController c : ingredientRowControllers) {
	        c.setScale(currentScale);
	    }
	}

	@FXML
	private void onHelpBtnClicked() {
		Alert help = new Alert(
				Alert.AlertType.INFORMATION,
				"Adjust the quantities in your recipe without changing the original.\n\n"
				+ "• Scale factor: Set the amount to increase or decrease the recipe. "
				+ "The default is 0.5x.\n"
				+ "• Increase (+): Add the scale factor to the current batch size.\n"
				+ "• Decrease (-): Subtract the scale factor from the current batch "
				+ "size.\n"
				+ "• Batch Size: Displays the recipe's current multiplier (e.g., "
				+ "2x represents twice the original recipe).\n"
				+ "• Reset: Return the scale factor and batch size to their defaults."
				);
		
		help.getDialogPane().setPrefWidth(500);
		help.setTitle("Recipe Scaling Help");
		help.setHeaderText("How Recipe Scaling Works");
		help.getDialogPane().getStylesheets().add(
				getClass().getResource("/css/components/alert.css").toExternalForm()
				);
		help.getDialogPane().setPrefWidth(450);
		help.showAndWait();
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

	public BigDecimal getCurrentScale() {
		return currentScale;
	}

	public void setCurrentScale(BigDecimal currentScale) {
		this.currentScale = currentScale;
	}

	public BigDecimal getAdjustmentFactor() {
		return adjustmentFactor;
	}

	public void setAdjustmentFactor(BigDecimal adjustmentFactor) {
		this.adjustmentFactor = adjustmentFactor;
	}
	
}
