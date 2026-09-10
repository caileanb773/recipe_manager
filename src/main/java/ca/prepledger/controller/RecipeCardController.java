package ca.prepledger.controller;

import java.net.URL;

import ca.prepledger.model.Recipe;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class RecipeCardController {

	@FXML
	private ImageView recipeImage;

	@FXML
	private Label recipeName;

	@FXML
	private Label ratingLabel;

	@FXML
	private Label timeLabel;

	// XXX Placeholder method
	public void setRecipe(Recipe recipe) {
		recipeName.setText("Chicken Tikka");
		ratingLabel.setText("★ " + 5);
		timeLabel.setText(100 + " min");

		URL url = getClass().getResource("/img/temp/chicken-tikka.png");
		Image image = new Image(url.toExternalForm());
		recipeImage.setImage(image);
	}
}