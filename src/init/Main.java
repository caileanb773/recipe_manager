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

		// TODO finish refactoring scaling using BigDecimal
		// TODO MINOR BUG: if you have a recipe detached, and switch theme, detached recipe theme doesn't change.
		// TODO loading screen logo
		// TODO language changing messes with app window size (width)
		
	}
}
