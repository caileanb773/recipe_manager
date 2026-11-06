package ca.prepledger.navigation;

/**
 * Contents: This interface defines the contract which injects the navigation
 * handler for a class that needs to handle its own navigation (i.e., it has a
 * sub-screen or element that causes an action performed in it to initiate some
 * sort of navigation).
 * 
 * @author Cailean Bernard
 * @since v0.1.0
 */
public interface Navigable {

	public void setNavigationHandler(NavigationHandler navigationHandler);
	
}
