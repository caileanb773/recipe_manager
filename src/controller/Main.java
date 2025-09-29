package controller;

import definitions.Fraction;

/*
 * Author: Cailean Bernard
 * Contents: Injection point for the application. An instance of the model and
 * the view is instantiated, and then a new Controller is created using the passed
 * model and view.
 */
public class Main {

	public static void main(String[] args) {

		// UI Look and Feel init
//		try {
//			UIManager.setLookAndFeel( new FlatLightLaf() );
//		} catch( Exception ex ) {
//			System.err.println( "Failed to initialize LaF" );
//		}
//
//		RecipeMgrModel model = new RecipeMgrModel();
//		AppFrame view = new AppFrame();
//		new AppController(model, view);
		
		String frac = "1/2";
		Fraction f1 = new Fraction(2, 1, 2);
		System.out.println("is frac a fraction: " + Fraction.isFraction(frac));
		System.out.println(f1.multiply(4).toString());

		// TODO handle when recipe amts are 1 + 1/2, 2 1/4
		// TODO resize recipe info based on window size
		// TODO batch math (10x batch, 5x batch, etcc)
		// TODO add light/dark mode
		// TODO notification center
		// TODO UI element size (accessibility)
		// TODO BUG: UI freeze when importing .txt of recipes into empty list
		// TODO BUG: ensure ampersand in finishRegistration doesn't break anything
		// TODO consider implications of a single-instance app
		
	}
}
