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
		new AppController(model, view);

		// TODO resize recipe info based on window size
		// TODO refactor the math in Fraction class and accnt for decimal multiplication
		// TODO BUG: UI freeze when importing .txt of recipes into empty list
		// TODO BUG: ensure ampersand in finishRegistration doesn't break anything
		// TODO german doesn't work??
		// TODO add one more theme to match alternate banner
		// TODO save import/export and backup files as proprietary .rcp to align with readme
		
	}
}
