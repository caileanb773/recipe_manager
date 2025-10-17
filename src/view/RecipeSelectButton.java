package view;

import java.awt.Font;
import java.util.List;
import javax.swing.JButton;
import definitions.Recipe;

/*
 * Author: Cailean Bernard
 * Contents: 
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

}
