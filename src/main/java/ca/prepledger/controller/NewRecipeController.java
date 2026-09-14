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
		System.out.println("Cancelling recipe");
		try {
			navigationHandler.navigateTo(ContextArea.RECIPES);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	public void onSaveRecipeButtonClicked() {
		System.out.println("Saving recipe");
	}

	@FXML
	public void onAddIngredientButtonClicked() {
		System.out.println("Adding ingredient");
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
			
			ObservableList<Node> children = ingredientsVBox.getChildren();
			
			children.add(children.size()-1, ingredientRow);
	}

	@Override
	public void setNavigationHandler(NavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;		
	}

}
