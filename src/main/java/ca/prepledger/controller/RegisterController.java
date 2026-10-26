package ca.prepledger.controller;

import java.io.IOException;

import org.controlsfx.control.textfield.CustomPasswordField;
import org.controlsfx.control.textfield.CustomTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.prepledger.navigation.ContextArea;
import ca.prepledger.navigation.Navigable;
import ca.prepledger.navigation.NavigationHandler;
import ca.prepledger.service.AuthService;
import ca.prepledger.session.SessionHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class RegisterController implements Navigable {

	@FXML private CustomTextField emailAddress;
	@FXML private CustomPasswordField password;
	@FXML private CustomPasswordField confirmPassword;
	@FXML private Button createAccount;
	@FXML private Button signInGoogle;
	@FXML private Button signIn;

	private AuthService auth;
	private NavigationHandler navigationHandler;
	private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);


	@FXML
	private void initialize() {
		auth = new AuthService();
	}
	
	public void initializeTextFieldIcons() {
		ImageView emailIcon = new ImageView(
				new Image(getClass().getResourceAsStream(
						"/img/icon/scaled/other/email-gray-scaled.png")));
		ImageView passwordIcon = new ImageView(
				new Image(getClass().getResourceAsStream(
						"/img/icon/scaled/other/password-gray-scaled.png")));
		
		ImageView confirmPasswordIcon = new ImageView(
				new Image(getClass().getResourceAsStream(
						"/img/icon/scaled/other/password-gray-scaled.png")));

		emailAddress.setLeft(emailIcon);
		password.setLeft(passwordIcon);
		confirmPassword.setLeft(confirmPasswordIcon);
	}

	@FXML
	private void onCreateAccountButtonClicked() {
		if (areFormFieldsValid(emailAddress.getText().trim())) {
			attemptRegister();
		} else {
			// TODO show alert
			System.out.println("Fields not valid.");
		}
	}

	// TODO replace with real validation
	public boolean areFormFieldsValid(String email) {		
		if (email.isBlank() || password.getText().isBlank() || confirmPassword.getText().isBlank()) {
			return false;
		}

		if (!password.getText().equals(confirmPassword.getText())) {
			// TODO display password mismatch error
			return false;
		}

		return true;
	}

	private void attemptRegister() {
		try {
			auth.register(
					emailAddress.getText().trim(),
					password.getText()
					);

			// Registration succeeded; return to login
			navigationHandler.navigateTo(ContextArea.LOGIN);

		} catch (IOException e) {
			logger.warn("Registration failed: {}", e.getMessage());
			// TODO display e.getMessage() to the user
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			logger.warn("Registration request interrupted.");
		}
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

}
