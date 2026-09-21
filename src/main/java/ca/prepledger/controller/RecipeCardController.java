package ca.prepledger.controller;

import java.io.IOException;
import java.net.URL;

import ca.prepledger.model.Recipe;
import ca.prepledger.navigation.ContextArea;
import ca.prepledger.navigation.Navigable;
import ca.prepledger.navigation.NavigationHandler;
import ca.prepledger.service.RecipeService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

public class RecipeCardController implements Navigable {

	@FXML
	private ImageView recipeImage;

	@FXML
	private Label recipeName;

	@FXML
	private FlowPane tagsPane;
	
	@FXML
	private Button recipeOptionsButton;
	
	private Recipe recipe;
	
	private RecipeService recipeService;

	private Runnable onRecipeDeleted;
	
	private NavigationHandler navigationHandler;
	
	
	// XXX Placeholder method
	public void setRecipe(Recipe recipe) {
		this.recipe = recipe;
		
		// Set the fields of the card
		recipeName.setText(recipe.getTitle());
		ObservableList<Node> tagsList = tagsPane.getChildren();
		recipe.getTags().stream()
	      .limit(3)
	      .forEach(tag -> {
	    	  Label tagLabel = new Label(tag);
	    	  tagLabel.setMaxWidth(80);
	    	  tagLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
	    	  tagLabel.setTooltip(new Tooltip(tag));
	    	  tagsList.add(tagLabel);
	      });
		
		// Get/set image
		recipeImage.setImage(getDefaultImage());
		
		/* Eventually:
		 * Image image;
		 * 
		 * if (recipe.hasImage())
		 * 		image = fetchImage()
		 * else
		 * 		image = getDefaultImage()
		 * 
		 * recipeImage.setImage(image)
		 */
	}
	
	@FXML
	private void onRecipeOptionsButtonClicked() {
		openContextMenu();
	}
	
	@FXML
	private void onRecipeCardClicked() {
		System.out.println("Recipe " + recipe.getTitle() + " clicked.");
	}
	
	private void openContextMenu() {
		ContextMenu menu = new ContextMenu();

		MenuItem editItem = new MenuItem("Edit");
		MenuItem deleteItem = new MenuItem("Delete");

		editItem.setOnAction(e -> {
			attemptEditRecipe();
		});
		
		deleteItem.setOnAction(e -> {
			attemptDeleteRecipe();
		});
		
		menu.getItems().addAll(editItem, deleteItem);
		menu.show(recipeOptionsButton, Side.BOTTOM, 0, 0);
	}
	
	public void attemptEditRecipe() {
		try {
			navigationHandler.navigateTo(ContextArea.EDIT_RECIPE, recipe);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void attemptDeleteRecipe() {
		// TODO dialog asking the user to confirm choice
		
		// Delete the recipe contained in this class from RecipeService's memory
		recipeService.removeRecipe(recipe);
		
		// Tell RecipeListController to refresh
		onRecipeDeleted.run();
	}
	
	// XXX need defensive programming for people who delete the image by mistake
	private Image getDefaultImage() {
		URL url = getClass().getResource("/img/temp/missing-image.png");
		Image image = new Image(url.toExternalForm());
		return image;
	}
	
	public Recipe getRecipe() {
		return this.recipe;
	}
	
	public void setRecipeService(RecipeService recipeService) {
		this.recipeService = recipeService;
	}
	
	public void setOnRecipeDeleted(Runnable onRecipeDeleted) {
		this.onRecipeDeleted = onRecipeDeleted;
	}

	@Override
	public void setNavigationHandler(NavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}
	
}