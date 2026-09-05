package ca.prepledger.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

//import javax.swing.JOptionPane;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import controller.AppController;
//import init.AppLoadingBar;
//import model.Model;
//import service.NotificationService;
//import util.SingleInstanceLocker;
//import view.AppFrame;

/**
 * Contents: Injection point for the application. An instance of the model,
 * the Notification Service, and the view is instantiated, and then a new 
 * Controller is created using the passed model and view.
 * @author: Cailean Bernard
 */
public class Main extends Application {
	
	//private static final Logger logger = LoggerFactory.getLogger(Main.class);
	
	// XXX Don't forget to reset Mode to ONLINE in Loader (Controller initialize).

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Home.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root, 1280, 720);
		
		primaryStage.setTitle("PrepLedger");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
//		System.setProperty("awt.useSystemAAFontSettings","on"); // For some reason this changes the font app-wide. Test it out for a bit
//		
//		// Detect if an instance is already running
//		if (!SingleInstanceLocker.lockInstance("MMLock")) {
//			logger.error("Application is already running.");
//			JOptionPane.showMessageDialog(null,
//					"There is already an instance of Macromise running.",
//					"Error",
//					JOptionPane.ERROR_MESSAGE);
//			return;
//		}
//		
//		// Create MVC
//		Model model = new Model();
//		NotificationService notificationService = new NotificationService();
//		AppFrame view = new AppFrame(notificationService);
//		AppController controller = new AppController(model, view);
//		new AppLoadingBar(controller).run();
		launch(args);
	}
}
