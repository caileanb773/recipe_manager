package ca.prepledger.controller;

import java.net.URL;

import ca.prepledger.model.Recipe;
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

public class RecipeCardController {

	@FXML
	private ImageView recipeImage;

	@FXML
	private Label recipeName;

	@FXML
	private FlowPane tagsPane;
	
	@FXML
	private Button recipeOptionsButton;
	
	private Recipe recipe;

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
		System.out.println("editing");
	}
	
	public void attemptDeleteRecipe() {
		System.out.println("deleting");
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
}