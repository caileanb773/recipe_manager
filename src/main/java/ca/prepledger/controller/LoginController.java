package ca.prepledger.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.prepledger.navigation.ContextArea;
import ca.prepledger.navigation.Navigable;
import ca.prepledger.navigation.NavigationHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class LoginController implements Navigable {
	
	@FXML private TextField emailAddress;
	@FXML private TextField password;
	@FXML private CheckBox rememberMe;
	@FXML private Button forgotPassword;
	@FXML private Button signIn;
	@FXML private Button signInGoogle;
	@FXML private Button createAccount;
	
	private NavigationHandler navigationHandler;
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	
	@FXML
	private void onForgotPasswordBtnClicked() {
		System.out.println("clicked");
	}
	
	@FXML
	private void onSignInBtnClicked() {
		System.out.println("Signing in as: " + emailAddress.getText() + ", " + password.getText());
	}
	
	@FXML
	private void onSignInGoogleBtnClicked() {
		System.out.println("Signing in via Google");
	}
	
	@FXML
	private void onCreateAccountBtnClicked() {
		System.out.println("Navigating to Register.fxml");
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

}
