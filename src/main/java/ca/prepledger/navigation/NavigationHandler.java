package ca.prepledger.navigation;

import java.io.IOException;

import ca.prepledger.model.Recipe;

/**
 * Contents: The methods needed to navigate between different windows in the
 * application.
 * 
 * @author Cailean Bernard
 * @since v0.1.0
 */
public interface NavigationHandler {

	void navigateTo(ContextArea contextArea) throws IOException;
	void navigateTo(ContextArea contextArea, Recipe recipe) throws IOException;
	
}
