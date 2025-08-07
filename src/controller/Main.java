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

		// implement internationalization, settings saving
	}
}
