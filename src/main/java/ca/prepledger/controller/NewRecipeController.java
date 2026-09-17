package ca.prepledger.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.prepledger.navigation.ContextArea;
import ca.prepledger.navigation.Navigable;
import ca.prepledger.navigation.NavigationHandler;
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
	private TextField recipeNameField;

	@FXML
	private TextField recipeTagsField;

	@FXML
	private TextArea instructionsTextArea;

	@FXML
	private VBox ingredientsVBox;

	@FXML
	private Button navBackButton;

	private NavigationHandler navigationHandler;

	private List<IngredientRowController> ingredientRowControllers = new ArrayList<>();;


	@FXML
	private void initialize() {
		try {
			addNewIngredientRow();
		} catch (IOException e) {
			// TODO: handle exception
		}
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
		try {
			navigationHandler.navigateTo(ContextArea.RECIPES);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	public void onSaveRecipeButtonClicked() {
		for (IngredientRowController c : ingredientRowControllers) {
			System.out.println(c.getIngredient().toString());
		}
	}

	@FXML
	public void onAddIngredientButtonClicked() {
		try {
			addNewIngredientRow();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addNewIngredientRow() throws IOException {
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
		
		// Request focus in the "name" field
		controller.requestFocusInNameTextField();
		
		System.out.println("Controllers: " + ingredientRowControllers.size());

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
		
		System.out.println("Controllers: " + ingredientRowControllers.size());
	}

	@Override
	public void setNavigationHandler(NavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;		
	}

}
