package init;

import javax.swing.JOptionPane;
import controller.AppController;
import model.RecipeMgrModel;
import util.InstanceLocker;
import view.AppFrame;

/*
 * Author: Cailean Bernard
 * Contents: Injection point for the application. An instance of the model and
 * the view is instantiated, and then a new Controller is created using the passed
 * model and view.
 */
public class Main {

	public static void main(String[] args) {
		
		// Detect if an instance is already running
		if (!InstanceLocker.lockInstance("MMLock")) {
			System.err.println("Application is already running.");
			JOptionPane.showMessageDialog(null,
					"There is already an instance of Macromise running.",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		// Create MVC
		RecipeMgrModel model = new RecipeMgrModel();
		AppFrame view = new AppFrame();
		AppController controller = new AppController(model, view);
		new Loader(controller).run();

		// TODO finish refactoring scaling using BigDecimal
		// TODO logging functionality
		// TODO dsktp / laptop are 2 diff java ver. consider j17
		
	}
}
