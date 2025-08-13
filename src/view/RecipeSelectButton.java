package view;

import java.awt.Color;
import javax.swing.JButton;

import definitions.Constants;

/*
 * Author: Cailean Bernard
 * Contents: 
 */

@SuppressWarnings("serial")
public class RecipeSelectButton extends JButton {
	
	public RecipeSelectButton(String title) {
		new JButton(title);
		setFocusable(true);
		setText(title);
		setBackground(Color.white);
		setFont(Constants.buttonFont);
	}

}
