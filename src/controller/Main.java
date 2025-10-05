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

		// UI Look and Feel init
		try {
			UIManager.setLookAndFeel( new FlatLightLaf() );
		} catch( Exception ex ) {
			System.err.println( "Failed to initialize LaF" );
		}

		RecipeMgrModel model = new RecipeMgrModel();
		AppFrame view = new AppFrame();
		new AppController(model, view);

		// TODO handle when recipe amts are 1 + 1/2, 2 1/4
		// TODO resize recipe info based on window size
		
		// TODO batch math (10x batch, 5x batch, etcc)
		// TODO ensure importing recipes works
		// TODO ensure adding new recipes and exporting recipes works (i.e. identical format)
		// TODO ensure newly added recipes are synced with the db
		// parseingredientfromstrarr, exportrecipelist, importrecipelist in model
		// formatrecipeforexport, formatrecipefortextdisplay, stringifyingredeints, 
		// TODO clean up recipe constructors, see which ones are never used
		// TODO check the methods in the dao are still functional (adding recipes)
		// TODO turn "export on close" back on
		// TODO exception encountered when ingredient amount was null on export
		
		// TODO add light/dark mode
		// TODO notification center
		// TODO UI element size (accessibility)
		// TODO BUG: UI freeze when importing .txt of recipes into empty list
		// TODO BUG: ensure ampersand in finishRegistration doesn't break anything
		// TODO consider implications of a single-instance app
		
	}
}
