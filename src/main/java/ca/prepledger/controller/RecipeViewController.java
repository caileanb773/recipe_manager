package ca.prepledger.controller;

import java.io.IOException;
import java.util.List;

import ca.prepledger.model.Recipe;
import ca.prepledger.navigation.ContextArea;
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
		List<String> tags = recipe.getTags();
		int numTags = tags.size();
		
		if (numTags > 0) {
			for (int i = 0; i < numTags; i++) {
				Label tagLabel;

				if (i < numTags - 1) {
					tagLabel = new Label(tags.get(i) + ", ");
				} else {
					tagLabel = new Label(tags.get(i));
				}
				
				tagLabel.getStyleClass().add("header-4");
				tagsHBox.getChildren().add(tagLabel);
			}
		}
		

	}
	
	@FXML
	private void onNavBackButtonClicked() {
		try {
			navigationHandler.navigateTo(ContextArea.RECIPES);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@FXML
	private void onEditButtonClicked() {
		try {
			navigationHandler.navigateTo(ContextArea.EDIT_RECIPE, recipe);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@FXML
	private void onRemoveRecipeButtonClicked() {
		
	}

}
