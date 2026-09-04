package view;

import java.awt.Font;
import java.util.List;
import javax.swing.JButton;

import model.Recipe;

/*
 * Author: Cailean Bernard
 * Contents: A clickable entry in the list of recipes on the Recipe List screen.
 */
@SuppressWarnings("serial")
public class RecipeSelectButton extends JButton {
	
	private Recipe btnRecipe;
	
	
	public RecipeSelectButton(Recipe recipe, Font font) {
		new JButton();
		btnRecipe = recipe;
		setFocusable(true);
		setText(recipe.getTitle());
		setFont(font);
	}
	
	public List<String> getTags() {
		return btnRecipe.getTags();
	}
	
	public Recipe getBtnRecipe() {
		return btnRecipe;
	}

}
