package ca.prepledger.app;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.prepledger.config.AppConfig;
import ca.prepledger.config.ConfigManager;
import ca.prepledger.controller.AppShellController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Contents: Injection point for the application. An instance of the model,
 * the Notification Service, and the view is instantiated, and then a new 
 * Controller is created using the passed model and view.
 * @author: Cailean Bernard
 */
public class Main extends Application {
	
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	private ConfigManager cfgManager;
	private AppConfig appCfg;

	@Override
	public void start(Stage primaryStage) throws Exception {
		Font.loadFont(getClass().getResourceAsStream("/font/Inter/static/Inter_18pt-Regular.ttf"), 14);
		Font.loadFont(getClass().getResourceAsStream("/font/Inter/static/Inter_18pt-Medium.ttf"), 14);
		Font.loadFont(getClass().getResourceAsStream("/font/Inter/static/Inter_18pt-SemiBold.ttf"), 14);
		Font.loadFont(getClass().getResourceAsStream("/font/Inter/static/Inter_18pt-Bold.ttf"), 14);
		
		// Get config manager to load the config
		cfgManager = new ConfigManager();
		appCfg = cfgManager.load();
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AppShell.fxml"));	
		Parent root = loader.load();
		
		// Set the AppConfig in the main controller for dependency injection later
		AppShellController controller = loader.getController();
		controller.setAppConfig(appCfg);
		controller.populateInitialWindow();
		
		Scene scene = new Scene(root, 1000, 800);
		
	    scene.getStylesheets().add(
	            getClass().getResource("/css/application.css").toExternalForm()
	        );
		
		primaryStage.setTitle("PrepLedger");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	@Override
	public void stop() {
		try {
			cfgManager.save(appCfg);
		} catch (IOException e) {
			logger.warn("IOException caught during ConfigManager.save() during Main.stop().");
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
