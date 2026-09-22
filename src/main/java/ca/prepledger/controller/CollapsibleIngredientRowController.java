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
		ingredientName.setText(ingredient.getName());
		ingredientAmtUnit.setText(ingredient.getAmount() + " " + ingredient.getUnit());
		ingredientNotes.setText(ingredient.getNotes());
	}
	
	@FXML
	private void onExpandIngredientNotesClicked() {
		if (!isNotesVisible) {
			isNotesVisible = true;
			ingredientNotes.setVisible(isNotesVisible);
			expandIngredientNotes.setRotate(POINT_DOWN);
		} else {
			isNotesVisible = false;
			ingredientNotes.setVisible(isNotesVisible);
			expandIngredientNotes.setRotate(POINT_RIGHT);
		}
	}
	
}
