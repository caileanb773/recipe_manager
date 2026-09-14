package ca.prepledger.controller;

import java.util.function.Consumer;

import ca.prepledger.model.Fraction;
import ca.prepledger.model.Ingredient;
import ca.prepledger.model.Unit;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class IngredientRowController {
		
	@FXML
	private GridPane rootNode;
	
	@FXML
	private Button deleteIngredientButton;
	
	@FXML
	private TextField ingredientNameField;
	
	@FXML
	private ComboBox<String> ingredientUnitComboBox;
	
	@FXML
	private TextField ingredientAmountField;
	
	private Consumer<IngredientRowController> onDelete;
	
	
	@FXML
	public void onDeleteIngredientButtonClicked() {
		if (onDelete != null) {
			onDelete.accept(this);
		} else {
			// TODO replace with actual logging
			System.err.println("IngredientRowController: onDelete == NULL.");
		}
	}
	
	public void setOnDelete(Consumer<IngredientRowController> onDelete) {
		this.onDelete = onDelete;
	}
	
	public GridPane getRoot() {
		return rootNode;
	}
	
	
	// Getters & Setters
	
	public Ingredient getIngredient() {
		Fraction frac = getIngredientAmount();
		Unit unit = getIngredientUnit();
		String name = getIngredientName();
		
		return new Ingredient(frac, unit, name);
	}
	
	public String getIngredientName() {
		return ingredientAmountField.getText();
	}
	
	public Fraction getIngredientAmount() {
		String amountStr = ingredientAmountField.getText();
		return new Fraction(amountStr);
	}

	public Unit getIngredientUnit() {
		String unitStr = ingredientUnitComboBox.getValue();
		return Unit.valueOf(unitStr);
	}
	
	public void setIngredient(Ingredient ingredient) {
		ingredientNameField.setText(ingredient.getName());
		ingredientUnitComboBox.setValue(ingredient.getUnit().toString());
		ingredientAmountField.setText(ingredient.getAmount().toString());
	}
	
	public void setIngredientName(String name) {
		// TODO
	}
	
	public void setIngredientAmount(Fraction amount) {
		// TODO
	}
	
	public void setIngredientUnit(Unit unit) {
		// TODO
	}
	
}
