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

import ca.prepledger.config.AppConfig;
import ca.prepledger.config.Configurable;
import ca.prepledger.exception.AuthException;
import ca.prepledger.navigation.ContextArea;
import ca.prepledger.navigation.Navigable;
import ca.prepledger.navigation.NavigationHandler;
import ca.prepledger.service.AuthService;
import ca.prepledger.session.SessionHandler;
import ca.prepledger.session.UserSession;
import ca.prepledger.util.PrepLedgerDialog;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LoginController implements Navigable, Configurable {

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
	private AppConfig appConfig;
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);


	@FXML
	private void initialize() {
		auth = new AuthService();
	}

	public void initializeTextFieldIcons() {
		ImageView emailIcon = new ImageView(
				new Image(getClass().getResourceAsStream(
						"/img/icon/scaled/other/email.png")));
		ImageView passwordIcon = new ImageView(
				new Image(getClass().getResourceAsStream(
						"/img/icon/scaled/other/password.png")));

		emailAddress.setLeft(emailIcon);
		password.setLeft(passwordIcon);
	}
	
	/**
	 * If appconfig was last saved with rememberme = true, then set the value of
	 * the email field to whatever the saved email is that was loaded from the 
	 * config file.
	 * 
	 * @since v0.1.2
	 */
	public void setRememberedEmail() {
		if (appConfig.isRememberingMe()) {
			String email = appConfig.getEmail();
			
			if (email != null) {
				emailAddress.setText(email.toLowerCase());
				rememberMe.setSelected(true);
			}
		}
	}

	@FXML
	private void onForgotPasswordBtnClicked() {
		String email = emailAddress.getText().trim();

		// parse input
		if (email.isBlank()) {
			PrepLedgerDialog.showForgotPasswordDialog("Please enter an email.");
			return;
		} else if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
			PrepLedgerDialog.showForgotPasswordDialog("Please enter a valid email.");
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

		// fail on empty email/password
		if (email.isBlank() || password.getText().isBlank()) {
			if (email.isBlank()) {
				PrepLedgerDialog.showLoginError("Please enter an email.");				
			} else if (password.getText().isBlank()) {
				PrepLedgerDialog.showLoginError("Please enter a password.");
			}
			return;
		}

		// fail on invalid email (i.e., doesn't match name@url.domain)
		if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
			PrepLedgerDialog.showLoginError("Please enter a valid email.");
			return;
		}
		
		// try to log in
		try {
			session = auth.login(email, password.getText());
		} catch (AuthException e) {

		    if (e.getCode().equals("PENDING_VERIFICATION")) {
		        if (PrepLedgerDialog.showVerificationRequired()) {
		            try {
		                auth.resendVerification(email);

		                PrepLedgerDialog.showGenericInfo("A new verification email "
		                		+ "has been sent. Please check your inbox."
		                );
		            } catch (IOException | InterruptedException ex) {
		            	PrepLedgerDialog.showLoginError("Unable to resend "
		            			+ "verification email. Please try again later."
		                );
		            }
		        }
		    }

		    return;
		} catch (IOException | InterruptedException e) {
		    PrepLedgerDialog.showLoginError(e.getMessage());
		    return;
		}
		
		if (session != null) {
			logger.info("Logged in as {}", 
					session.getEmail() + " at " + Time.valueOf(LocalTime.now()));

			// handle email remembering here
			if (rememberMe.isSelected()) {
				appConfig.setRememberMe(true);
				appConfig.setEmail(email);
			}
			
			// tell our session handler (main) to dispose this window and open PrepLedger
			sessionHandler.onLoginSuccess(session);
		} else {
			PrepLedgerDialog.showLoginError("Invalid credentials.");
		}
	}

	@FXML
	private void onSignInGoogleBtnClicked() {
		PrepLedgerDialog.showGenericInfo("This feature is coming in a future release.");
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

	@Override
	public void setAppConfig(AppConfig appConfig) {
		this.appConfig = appConfig;
	}

}
