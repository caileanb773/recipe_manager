package init;

import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import controller.AppController;
import model.Model;
import model.NotificationService;
import util.InstanceLocker;
import view.AppFrame;

/**
 * Contents: Injection point for the application. An instance of the model,
 * the Notification Service, and the view is instantiated, and then a new 
 * Controller is created using the passed model and view.
 * @author: Cailean Bernard
 */
public class Main {
	
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	
	// XXX Don't forget to reset Mode to ONLINE in Loader (Controller initialize).

	public static void main(String[] args) {
		System.setProperty("awt.useSystemAAFontSettings","on"); // For some reason this changes the font app-wide. Test it out for a bit
		
		// Detect if an instance is already running
		if (!InstanceLocker.lockInstance("MMLock")) {
			logger.error("Application is already running.");
			JOptionPane.showMessageDialog(null,
					"There is already an instance of Macromise running.",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		// Create MVC
		Model model = new Model();
		NotificationService notificationService = new NotificationService();
		AppFrame view = new AppFrame(notificationService);
		AppController controller = new AppController(model, view);
		new Loader(controller).run();
	}
}
