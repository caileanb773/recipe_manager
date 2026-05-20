package init;

import javax.swing.JOptionPane;
import controller.AppController;
import model.Model;
import util.InstanceLocker;
import view.AppFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Author: Cailean Bernard
 * Contents: Injection point for the application. An instance of the model and
 * the view is instantiated, and then a new Controller is created using the passed
 * model and view.
 */
public class Main {
	
	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		
		// Detect if an instance is already running
		if (!InstanceLocker.lockInstance("MMLock")) {
			logger.warn("Application is already running.");
			JOptionPane.showMessageDialog(null,
					"There is already an instance of Macromise running.",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		// Create MVC
		Model model = new Model();
		AppFrame view = new AppFrame();
		AppController controller = new AppController(model, view);
		new Loader(controller).run();
			
		// TODO (8) implement half and quarter-step scaling of recipes
		// TODO (5) expected behaviour when toggling notifications selected 
	}
}
