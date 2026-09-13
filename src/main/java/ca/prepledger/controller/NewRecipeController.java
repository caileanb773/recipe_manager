package ca.prepledger.controller;

import java.io.IOException;

import ca.prepledger.navigation.ContextArea;
import ca.prepledger.navigation.Navigable;
import ca.prepledger.navigation.NavigationHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

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
	
	private NavigationHandler navigationHandler;
	
	
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
	}

	@Override
	public void setNavigationHandler(NavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;		
	}

}
