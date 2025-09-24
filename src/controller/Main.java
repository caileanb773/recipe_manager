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
		AppController controller = new AppController(model, view);

		// TODO handle when recipe amts are 1 + 1/2, 2 1/4
		// TODO resize recipe info based on window size
		// TODO batch math (10x batch, 5x batch, etcc)
		// TODO add light/dark mode
		// TODO notification center
		// TODO UI element size (accessibility)
		// TODO registerController(ActionListener listener) for the model/view and others that need it
		// TODO separate concerns of initializing buttons and registering the listener
		// TODO don't need two methods for checking if email exists if were using an int constant as return (emailexists, validatepassword)
		// TODO figure out a good way to represent that a pw in register screen is strong enough
		// TODO remove references to "missing" in translations
		// TODO clean up/simplify init methods
		
	}
}
