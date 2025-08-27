package controller;

import model.RecipeMgrModel;
import view.AppFrame;

/*
 * Author: Cailean Bernard
 * Contents: Injection point for the application. An instance of the model and
 * the view is instantiated, and then a new Controller is created using the passed
 * model and view.
 */

public class Main {

	public static void main(String[] args) {
		RecipeMgrModel model = new RecipeMgrModel();
		AppFrame view = new AppFrame();
		AppController controller = new AppController(model, view);

		// TODO handle when recipe amts are 1 + 1/2, 2 1/4
		// TODO resize recipe info based on window size
		// TODO batch math (10x batch, 5x batch, etcc)
		// TODO light mode/dark mode
		// TODO notification center
		// TODO UI element size (accessibility)
		// TODO icon, make the UI not look so bad
	}
}
