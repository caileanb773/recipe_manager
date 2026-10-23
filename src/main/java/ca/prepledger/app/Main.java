package ca.prepledger.app;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.prepledger.config.AppConfig;
import ca.prepledger.config.ConfigManager;
import ca.prepledger.controller.AppShellController;
import ca.prepledger.controller.LoginController;
import ca.prepledger.controller.RegisterController;
import ca.prepledger.model.Recipe;
import ca.prepledger.navigation.ContextArea;
import ca.prepledger.navigation.NavigationHandler;
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
public class Main extends Application implements SessionHandler, NavigationHandler {

	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	private AppShellController appShellController;
	private ConfigManager cfgManager;
	private AppConfig appCfg;
	private Stage primaryStage;
	private SessionManager sessionManager;
	private UserSession currentSession;
	private Parent root;


	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		this.sessionManager = new SessionManager();

		// Load fonts
		loadFonts();

		// Get config manager to load the config
		cfgManager = new ConfigManager();
		appCfg = cfgManager.load();

		UserSession savedSession = sessionManager.load();

		// Load AppShellController and inject dependencies
		loadAppShellController();
		injectAppShellAppConfig();
		injectSessionHandler();

		if (savedSession != null) {
			currentSession = savedSession;
			showMainApplication(primaryStage);
		} else {
			showLoginScreen(primaryStage);
		}

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
		appShellController.setCurrentSession(currentSession);
		appShellController.populateInitialWindow();

		Scene scene = new Scene(root, 1000, 800);
		scene.getStylesheets().add(
				getClass().getResource("/css/application.css").toExternalForm()
				);

		primaryStage.setScene(scene);
	}

	private void injectAppShellAppConfig() {
		appShellController.setAppConfig(appCfg);
	}
	
	private void injectSessionHandler() {
		appShellController.setSessionHandler(this);
	}

	private void showLoginScreen(Stage primaryStage) throws IOException {
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource("/fxml/userauth/Login.fxml")
				);

		Parent root = loader.load();

		LoginController controller = loader.getController();
		controller.setSessionHandler(this);
		controller.setNavigationHandler(this);
		controller.initializeTextFieldIcons();

		Scene scene = new Scene(root, 800, 600);
		scene.getStylesheets().add(
				getClass().getResource("/css/application.css").toExternalForm()
				);

		primaryStage.setScene(scene);
	}
	
	private void showRegisterScreen(Stage primaryStage) throws IOException {
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource("/fxml/userauth/Register.fxml")
				);

		Parent root = loader.load();

		RegisterController controller = loader.getController();
		controller.setNavigationHandler(this);
		controller.initializeTextFieldIcons();

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

	@Override
	public void navigateTo(ContextArea contextArea) throws IOException {
		switch (contextArea) {
		case LOGIN:
			showLoginScreen(primaryStage);
			break;
		case REGISTER:
			showRegisterScreen(primaryStage);
			break;
		default:
			break;
		}
	}

	@Override
	public void navigateTo(ContextArea contextArea, Recipe recipe) throws IOException {
		return;
	}

	@Override
	public void onLoginSuccess(UserSession session) {
		currentSession = session;

		try {
			sessionManager.save(session);
			showMainApplication(primaryStage);
		} catch (IOException e) {
			logger.error("onLoginSuccess(): Failed to open appplication after login.", e);
		}
	}
	
	@Override
	public void onLogout() {
		
	}

}
