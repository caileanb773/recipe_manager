package controller;

import javax.swing.UIManager;
import com.formdev.flatlaf.FlatLightLaf;
import model.RecipeMgrModel;
import view.AppFrame;

/*
 * Author: Cailean Bernard
 * Contents: Injection point for the application. An instance of the model and
 * the view is instantiated, and then a new Controller is created using the passed
 * model and view.
 */
public class Main {

	public static void main(String[] args) {

		// Default theme is light
		try {
			UIManager.setLookAndFeel(new FlatLightLaf());
		} catch (Exception e) {
			System.err.println("Error while initializing FlatLAF: " + e.getMessage());
		}
		
		RecipeMgrModel model = new RecipeMgrModel();
		AppFrame view = new AppFrame();
		AppController controller = new AppController(model, view);
		//controller.login(); //for debugging

		// TODO refactor the math in Fraction class and accnt for decimal multiplication
		// TODO BUG: the change to German translation is non-functional atm
		// TODO load screen for importing recipes
		// TODO recipe buttons should just go '...' if they are too long. currently long rcp names widen the entire window
		// TODO UI hanging might be due to the fact that every time a recipe is added, the entire list is refreshed. If importing, don't refresh until the end
		// TODO BUG: if you have a recipe detached, and switch theme, detached recipe theme doesn't change.
		
	}
}
