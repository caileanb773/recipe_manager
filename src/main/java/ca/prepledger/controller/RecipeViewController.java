package ca.prepledger.controller;

import ca.prepledger.model.Recipe;
import ca.prepledger.navigation.NavigationHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class RecipeViewController {
	
	@FXML
	private Button navBackButton;
	
	@FXML
	private Label recipeTitleHeader;
	
	@FXML
	private Button editButton;
	
	@FXML
	private Button removeButton;
	
	@FXML
	private HBox tagsHBox;
	
	private Recipe recipe;
	
	private NavigationHandler navigationHandler;
	
	
	public void setNavigationHandler(AppShellController appShellController) {
		navigationHandler = appShellController;
	}

	public void setRecipeToView(Recipe recipe) {
		this.recipe = recipe;
		
		// Set the title header
		recipeTitleHeader.setText(recipe.getTitle());
		
		// Add tags
		for (String tag : recipe.getTags()) {
			Label tagLabel = new Label(tag);
			tagLabel.getStyleClass().add("header-3");
			tagsHBox.getChildren().add(tagLabel);			
		}
	}
	
	@FXML
	private void onNavBackButtonClicked() {
		
	}
	
	@FXML
	private void onEditButtonClicked() {
		
	}
	
	@FXML
	private void onRemoveRecipeButtonClicked() {
		
	}

}
