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
	private void initialize() {
		setUnitComboBoxItems();
	}
	
	// XXX eventually replace this with some feature that allows users to define pref. units
	public void setUnitComboBoxItems() {		
		ingredientUnitComboBox.getItems().add("g");
		ingredientUnitComboBox.getItems().add("kg");
		ingredientUnitComboBox.getItems().add("mg");
		ingredientUnitComboBox.getItems().add("oz");
		ingredientUnitComboBox.getItems().add("lbs");
		ingredientUnitComboBox.getItems().add("cup");
		ingredientUnitComboBox.getItems().add("ml");
		ingredientUnitComboBox.getItems().add("liter");
		ingredientUnitComboBox.getItems().add("fl. oz");
		ingredientUnitComboBox.getItems().add("%");
	}
	
	@FXML
	public void onDeleteIngredientButtonClicked() {
		if (onDelete != null) {
			onDelete.accept(this);
		} else {
			// TODO replace with actual logging
		}
	}

	public void requestFocusInNameTextField() {
		ingredientNameField.requestFocus(); 
	}

	// Getters & Setters
	
	public GridPane getRoot() {
		return rootNode;
	}
	
	public void setOnDelete(Consumer<IngredientRowController> onDelete) {
		this.onDelete = onDelete;
	}
	
	public void setDefaultPromptText() {
		ingredientNameField.setPromptText("e.g. Smoked Paprika");
		ingredientUnitComboBox.setPromptText("Select Unit");
		ingredientAmountField.setPromptText("e.g. 50");
		ingredientNotesField.setPromptText("e.g. crushed");
	}

	public Ingredient getIngredient() {
		Fraction frac = getIngredientAmount();
		String unit = getIngredientUnit();
		String name = getIngredientName();
		String notes = getIngredientNotes();
		
		return new Ingredient(frac, unit, name, notes);
	}

	public String getIngredientName() {
		String val = ingredientNameField.getText();
		
		if (val == null || val.trim().isEmpty()) {
			return null;
		}		
		
		return val;
	}

	public Fraction getIngredientAmount() {
		String amountStr = ingredientAmountField.getText();
		
		if (amountStr == null || amountStr.trim().isEmpty()) {
			return null;
		}
		
		return new Fraction(amountStr);
	}

	public String getIngredientUnit() {
		String val = ingredientUnitComboBox.getValue();
		
		if (val == null || val.trim().isEmpty()) {
			return null;
		}
		
		return val;
	}

	public String getIngredientNotes() {
		String val = ingredientNotesField.getText(); 
		
		if (val == null || val.trim().isEmpty()) {
			return null;
		}
		
		return val;
	}

	public void setIngredient(Ingredient ingredient) {
		String name = ingredient.getName();
		String unit = ingredient.getUnit();
		String amt = ingredient.getAmount();
		String note = ingredient.getNotes();
		ingredientNameField.setText((name == null) ? null : name);
		ingredientUnitComboBox.setValue((unit == null) ? null : unit);
		ingredientAmountField.setText((amt == null) ? null : amt);
		ingredientNotesField.setText((note == null) ? null : note);
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
