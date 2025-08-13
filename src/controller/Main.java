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

		// TODO light mode/dark mode
		// TODO tag/name filtering
		// TODO database instead of local file
		// TODO notification center
		// TODO UI element size (accessibility)
		// TODO icon, make the UI not look so bad
		// TODO add a button that just opens the readme in whatever language
	}
}
