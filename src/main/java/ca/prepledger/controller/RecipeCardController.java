package ca.prepledger.controller;

import ca.prepledger.model.Recipe;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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

    public void setRecipe(Recipe recipe) {
        recipeName.setText(recipe.getTitle());
        ratingLabel.setText("★ " + 5);
        timeLabel.setText(100 + " min");

        // Load image, tags, etc.
    }
}