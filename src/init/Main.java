package init;

import controller.AppController;
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
		
		// Create MVC
		RecipeMgrModel model = new RecipeMgrModel();
		AppFrame view = new AppFrame();
		AppController controller = new AppController(model, view);
		new Loader(controller).run();
		controller.login();

		// TODO finish refactoring scaling using BigDecimal
		// TODO BUG: the change to German translation is non-functional atm
		// TODO MINOR BUG: if you have a recipe detached, and switch theme, detached recipe theme doesn't change.
		// TODO handle user clicking "X" while importing recipes. right now it will freeze the UI, throw an exception, then continue importing in the background even if the applicatin is closed
		// TODO loading screen logo/tidy up
		// TODO BUG: exporting empty recipe list should cancel and show dialog
		// TODO loader should employ a singleton design pattern
		
	}
}
