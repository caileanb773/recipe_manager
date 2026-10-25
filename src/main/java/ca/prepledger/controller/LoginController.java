package ca.prepledger.controller;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.time.LocalTime;

import org.controlsfx.control.textfield.CustomPasswordField;
import org.controlsfx.control.textfield.CustomTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.prepledger.app.SessionHandler;
import ca.prepledger.app.UserSession;
import ca.prepledger.navigation.ContextArea;
import ca.prepledger.navigation.Navigable;
import ca.prepledger.navigation.NavigationHandler;
import ca.prepledger.service.AuthService;
import ca.prepledger.util.PrepLedgerDialog;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LoginController implements Navigable {

	@FXML private CustomTextField emailAddress;
	@FXML private CustomPasswordField password;
	@FXML private CheckBox rememberMe;
	@FXML private Button forgotPassword;
	@FXML private Button signIn;
	@FXML private Button signInGoogle;
	@FXML private Button createAccount;

	private AuthService auth;
	private NavigationHandler navigationHandler;
	private SessionHandler sessionHandler;
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);


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

		emailAddress.setLeft(emailIcon);
		password.setLeft(passwordIcon);
	}

	@FXML
	private void onForgotPasswordBtnClicked() {
		String email = emailAddress.getText().trim();
		PrepLedgerDialog dialog = new PrepLedgerDialog();

		// parse input
		if (email.isBlank()) {
			dialog.showForgotPasswordDialog("Please enter an email.");
			return;
		} else if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
			dialog.showForgotPasswordDialog("Please enter a valid email.");
			return;
		}
		
		// open prepledger.ca/forgot-password.html passing the email
		try {
			String url = "https://prepledger.ca/forgot-password.html?email="
					+ URLEncoder.encode(email, StandardCharsets.UTF_8);

			Desktop.getDesktop().browse(URI.create(url));
		} catch (IOException e) {
			// Show an error if the browser couldn't be opened.
		}
	}

	@FXML
	private void onSignInBtnClicked() {
		attemptLogin();
	}

	private void attemptLogin() {
		String email = emailAddress.getText().trim();
		UserSession session = null;

		// TODO ensure email/password fields are valid
		if (email.isBlank() || password.getText().isBlank()) {
			// TODO show error msg to user
			logger.warn("onSignInBtnClicked(): One or more fields blank.");
			return;
		}

		try {
			session = auth.login(email, password.getText());
		} catch (IOException | InterruptedException e) {
			logger.warn("onSignInBtnClicked(): Couldn't login.");
		}

		if (session != null) {
			logger.info("Logged in as {}", session.getEmail() + " at " + Time.valueOf(LocalTime.now()));

			sessionHandler.onLoginSuccess(session);
		} else {
			logger.warn("attemptLogin(): Login failed; session returned from AuthService == null.");
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
	private void onCreateAccountBtnClicked() {
		showRegisterScreen();
	}

	private void showRegisterScreen() {
		try {
			navigationHandler.navigateTo(ContextArea.REGISTER);
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
