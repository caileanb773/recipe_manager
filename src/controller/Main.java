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

		// TODO resize recipe info based on window size
		// TODO add autobackup remembering to config file
		// TODO refactor the math in Fraction class and accnt for decimal multiplication
		// TODO BUG: UI freeze when importing .txt of recipes into empty list
		// TODO BUG: ensure ampersand in finishRegistration doesn't break anything
		// TODO consider implications of a single-instance app
		// TODO german doesn't work??
		// TODO sort out recipe display font. ugly
		// TODO add light/dark mode
		// TODO notification center
		// TODO UI element size (accessibility)
		// TODO update the readme
		
	}
}
