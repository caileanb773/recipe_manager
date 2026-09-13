package ca.prepledger.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	@Override
	public void start(Stage primaryStage) throws Exception {
		Font.loadFont(getClass().getResourceAsStream("/font/Inter/static/Inter_18pt-Regular.ttf"), 14);
		Font.loadFont(getClass().getResourceAsStream("/font/Inter/static/Inter_18pt-Medium.ttf"), 14);
		Font.loadFont(getClass().getResourceAsStream("/font/Inter/static/Inter_18pt-SemiBold.ttf"), 14);
		Font.loadFont(getClass().getResourceAsStream("/font/Inter/static/Inter_18pt-Bold.ttf"), 14);
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AppShell.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root, 1280, 720);
		
	    scene.getStylesheets().add(
	            getClass().getResource("/css/application.css").toExternalForm()
	        );
		
		primaryStage.setTitle("PrepLedger");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
