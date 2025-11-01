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
		//controller.login();

		// TODO finish refactoring scaling using BigDecimal
		// TODO language changing messes with app window size (width)
		// TODO only one instance open at a time
		// TODO login screen inputs off center
		
	}
}
