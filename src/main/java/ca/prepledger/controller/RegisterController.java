package ca.prepledger.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.prepledger.navigation.Navigable;
import ca.prepledger.navigation.NavigationHandler;

public class RegisterController implements Navigable {
	
	private NavigationHandler navigationHandler;
	private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

	@Override
	public void setNavigationHandler(NavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}

}
