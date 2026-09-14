package ca.prepledger.controller;

import java.io.IOException;

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

	private NavigationHandler navigationHandler;


	@FXML
	private void initialize() {
		
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
			
			// Wire the callback for when row's delete button is pressed
			IngredientRowController controller = loader.getController();
			controller.setOnDelete(this::removeIngredientRow);
			
			// Add at the 2nd last index so that the "Add Ingredient" button is last
			ObservableList<Node> children = ingredientsVBox.getChildren();
			children.add(children.size()-1, ingredientRow);
	}
	
	private void removeIngredientRow(IngredientRowController controller) {
		ingredientsVBox.getChildren().remove(controller.getRoot());
	}

	@Override
	public void setNavigationHandler(NavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;		
	}
	
}
