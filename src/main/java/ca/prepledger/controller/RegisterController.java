package ca.prepledger.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.prepledger.app.SessionHandler;
import ca.prepledger.navigation.ContextArea;
import ca.prepledger.navigation.Navigable;
import ca.prepledger.navigation.NavigationHandler;
import ca.prepledger.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class RegisterController implements Navigable {

	@FXML private TextField emailAddress;
	@FXML private TextField password;
	@FXML private TextField confirmPassword;
	@FXML private Button createAccount;
	@FXML private Button signInGoogle;
	@FXML private Button signIn;

	private AuthService auth;
	private NavigationHandler navigationHandler;
	private SessionHandler sessionHandler;
	private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);


	@FXML
	private void initialize() {
		auth = new AuthService();
	}

	@FXML
	private void onCreateAccountButtonClicked() {
		attemptCreateAccount();
	}

	private void attemptCreateAccount() {

	}

	@FXML
	private void onSignInGoogleBtnClicked() {
		Alert alert = new Alert(
				AlertType.INFORMATION,
				"This feature is coming in a future release."
				);

		alert.getDialogPane().getStylesheets().add(
				getClass().getResource("/css/components/alert.css").toExternalForm());
		alert.showAndWait();
	}

	@FXML
	private void onSignInBtnClicked() {
		showLoginScreen();
	}

	private void showLoginScreen() {		
		try {
			navigationHandler.navigateTo(ContextArea.LOGIN);
		} catch (IOException e) {
			logger.error("onCreateAccountBtnClicked(): IOException encountered {}", e);
		}
	}

	@Override
	public void setNavigationHandler(NavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}

	public void setSessionHandler(SessionHandler sessionHandler) {
		this.sessionHandler = sessionHandler;
	}

}
