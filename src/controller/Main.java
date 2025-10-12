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

		// Init FlatLAF
		try {
			UIManager.setLookAndFeel( new FlatLightLaf() );
		} catch( Exception ex ) {
			System.err.println( "Failed to initialize LaF" );
		}

		RecipeMgrModel model = new RecipeMgrModel();
		AppFrame view = new AppFrame();
		AppController controller = new AppController(model, view);
		//controller.login(); //for debugging

		// TODO refactor the math in Fraction class and accnt for decimal multiplication
		// TODO german doesn't work??
		// TODO add one more theme to match alternate banner
		// TODO clean up register screen pw requirements label
		// TODO load screen for importing recipes
		// TODO recipe buttons should just go '...' if they are too long. currently long rcp names widen the entire window
		// TODO add text/translations about illegal characters to register fail jdialog
		
	}
}
