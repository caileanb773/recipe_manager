package ca.prepledger.controller;

import java.util.function.Consumer;

import ca.prepledger.model.Fraction;
import ca.prepledger.model.Ingredient;
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

	@FXML
	private TextField ingredientNotesField;
	
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

	// Getters & Setters
	
	public GridPane getRoot() {
		return rootNode;
	}

	public Ingredient getIngredient() {
		Fraction frac = getIngredientAmount();
		String unit = getIngredientUnit();
		String name = getIngredientName();
		String notes = getIngredientNotes();

		return new Ingredient(frac, unit, name, notes);
	}

	public String getIngredientName() {
		return ingredientAmountField.getText();
	}

	public Fraction getIngredientAmount() {
		String amountStr = ingredientAmountField.getText();
		return new Fraction(amountStr);
	}

	public String getIngredientUnit() {
		return ingredientUnitComboBox.getValue();
	}

	public String getIngredientNotes() {
		return ingredientNotesField.getText();
	}

	public void setIngredient(Ingredient ingredient) {
		ingredientNameField.setText(ingredient.getName());
		ingredientUnitComboBox.setValue(ingredient.getUnit().toString());
		ingredientAmountField.setText(ingredient.getAmount().toString());
		ingredientNotesField.setText(ingredient.getNotes());
	}

	public void setIngredientName(String name) {
		ingredientNameField.setText(name);
	}

	public void setIngredientAmount(Fraction amount) {
		ingredientAmountField.setText(amount.toString());
	}

	public void setIngredientUnit(String unit) {
		ingredientUnitComboBox.setValue(unit);
	}

	public void setIngredientNotes(String notes) {
		ingredientNotesField.setText(notes);
	}

}
