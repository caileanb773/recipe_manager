package ca.prepledger.controller;

import java.net.URL;

import ca.prepledger.model.Recipe;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
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

	// XXX Placeholder method
	public void setRecipe(Recipe recipe) {
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
		
		/*
		 * Image image;
		 * 
		 * if (recipe.hasImage())
		 * 		image = fetchImage()
		 * else
		 * 		image = getDefaultImage()
		 * 
		 * recipeImage.setImage(image)
		 * */
		
		recipeImage.setImage(getDefaultImage());
	}
	
	// XXX need defensive programming for people who delete the image by mistake
	private Image getDefaultImage() {
		URL url = getClass().getResource("/img/temp/missing-image.png");
		Image image = new Image(url.toExternalForm());
		return image;
	}
}