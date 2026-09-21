package ca.prepledger.navigation;

import java.io.IOException;

import ca.prepledger.model.Recipe;

public interface NavigationHandler {

	void navigateTo(ContextArea contextArea) throws IOException;
	void navigateTo(ContextArea contextArea, Recipe recipe) throws IOException;
	
}
