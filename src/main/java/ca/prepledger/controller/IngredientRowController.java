package ca.prepledger.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class IngredientRowController {
	
	@FXML
	private Button addIngredientButton;
	
	@FXML
	private TextField ingredientNameField;
	
	@FXML
	private ComboBox<String> ingredientUnitComboBox;
	
	@FXML
	private TextField ingredientAmountField;
	
	
	@FXML
	public void onDeleteIngredientButtonClicked() {
		System.out.println("Deleting ingredient...");
	}

}
