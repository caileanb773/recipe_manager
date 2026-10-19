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
import javafx.scene.image.Image;
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
	private AppShellController appShellController;
	private ConfigManager cfgManager;
	private AppConfig appCfg;
	private Parent root;
	
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {	
		// Load fonts
		loadFonts();

		// Get config manager to load the config
		cfgManager = new ConfigManager();
		appCfg = cfgManager.load();

		// Load AppShellController and inject dependency
		loadAppShellController();
		injectAppShellAppConfig(appCfg);
		
		// Load "Login" screen
		showLoginScreen(primaryStage);

		// Validate login credentials
		//showAppShell(primaryStage);

		// Finalize (title bar, icon, and show the screen)
		finalizeAppShell(primaryStage);
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

	private void loadAppShellController() {
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource("/fxml/AppShell.fxml")
				);
		
		try {
			root = loader.load();
			appShellController = loader.getController();
		} catch (IOException e) {
			logger.error("loadAppShellController(): IOException caught during controller load: {}", e);
		}
	}

	private void showMainApplication(Stage primaryStage) throws IOException {
		appShellController.populateInitialWindow();

		Scene scene = new Scene(root, 1000, 800);
		scene.getStylesheets().add(
				getClass().getResource("/css/application.css").toExternalForm()
				);

		primaryStage.setScene(scene);
	}
	
	private void injectAppShellAppConfig(AppConfig appConfig) {
		appShellController.setAppConfig(appCfg);
	}
	
	private void showLoginScreen(Stage primaryStage) {
		try {
			appShellController.showLogin();
		} catch (IOException e) {
			logger.error("showLoginScreen(): IOException caught during load: {}", e);
		}

		Scene scene = new Scene(root, 800, 600);
		scene.getStylesheets().add(
				getClass().getResource("/css/application.css").toExternalForm()
				);

		primaryStage.setScene(scene);
	}
	
	private void finalizeAppShell(Stage primaryStage) {
		primaryStage.setTitle("PrepLedger");
		Image icon = new Image(getClass().getResourceAsStream("/img/icon/app-icon.png"));
		primaryStage.getIcons().add(icon);
		primaryStage.show();
	}

	private void loadFonts() {
		Font.loadFont(getClass().getResourceAsStream("/font/Inter/static/Inter_18pt-Regular.ttf"), 14);
		Font.loadFont(getClass().getResourceAsStream("/font/Inter/static/Inter_18pt-Medium.ttf"), 14);
		Font.loadFont(getClass().getResourceAsStream("/font/Inter/static/Inter_18pt-SemiBold.ttf"), 14);
		Font.loadFont(getClass().getResourceAsStream("/font/Inter/static/Inter_18pt-Bold.ttf"), 14);
	}

}
