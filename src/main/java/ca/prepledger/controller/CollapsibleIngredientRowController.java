package ca.prepledger.controller;

import ca.prepledger.model.Ingredient;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class CollapsibleIngredientRowController {
	
	@FXML
	private Button expandIngredientNotes;
	
	@FXML
	private Label ingredientName;
	
	@FXML
	private Label ingredientAmtUnit;
	
	@FXML
	private Label ingredientNotes;
	
	private boolean isNotesVisible = false;
	
	private final double POINT_DOWN = 90.0;
	
	private final double POINT_RIGHT = 0.0;
	
	
	public void setIngredient(Ingredient ingredient) {
		String name = ingredient.getName();
		String amount = ingredient.getAmount();
		String unit = ingredient.getUnit();
		String notes = ingredient.getNotes();
		
		if (name != null) {
			ingredientName.setText(name);
		}

		if (amount != null) {
			if (unit != null) {
				ingredientAmtUnit.setText(amount + " " + unit);
			} else {
				ingredientAmtUnit.setText(amount);
			}
		}

		if (notes != null) {
			ingredientNotes.setText(notes);
		}
		
		// Ingredients should not show their notes by default
		setDefaultCollapsedState();
	}
	
	public void setDefaultCollapsedState() {
		ingredientNotes.setVisible(false);
		ingredientNotes.setManaged(false);
	}
	
	@FXML
	private void onExpandIngredientNotesClicked() {
		if (!isNotesVisible) {
			isNotesVisible = true;
			ingredientNotes.setVisible(isNotesVisible);
			ingredientNotes.setManaged(isNotesVisible);
			expandIngredientNotes.setRotate(POINT_DOWN);
		} else {
			isNotesVisible = false;
			ingredientNotes.setVisible(isNotesVisible);
			ingredientNotes.setManaged(isNotesVisible);
			expandIngredientNotes.setRotate(POINT_RIGHT);
		}
	}
	
}
