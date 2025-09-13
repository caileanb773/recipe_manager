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
		
		// Preamble, don't delete
		try {
		    UIManager.setLookAndFeel( new FlatLightLaf() );
		} catch( Exception ex ) {
		    System.err.println( "Failed to initialize LaF" );
		}
		
		RecipeMgrModel model = new RecipeMgrModel();
		AppFrame view = new AppFrame();
		AppController controller = new AppController(model, view);

		// TODO fix gradient for login when screen is expanded
		// TODO handle when recipe amts are 1 + 1/2, 2 1/4
		// TODO resize recipe info based on window size
		// TODO batch math (10x batch, 5x batch, etcc)
		// TODO add light/dark mode
		// TODO notification center
		// TODO UI element size (accessibility)
		// TODO registerController(ActionListener listener) for the model/view and others that need it
		// TODO separate concerns of initializing buttons and registering the listener
		
		/* for storing passwords, don't store the password itself. store the hash,
		 * then when the user enters their password, check that hashed password
		 * against the stored hash */
	}
}
