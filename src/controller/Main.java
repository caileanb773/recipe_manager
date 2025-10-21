package controller;

import javax.swing.UIManager;

import com.formdev.flatlaf.FlatLightLaf;

import model.RecipeMgrModel;
import util.ProgressListener;
import view.AppFrame;

/*
 * Author: Cailean Bernard
 * Contents: Injection point for the application. An instance of the model and
 * the view is instantiated, and then a new Controller is created using the passed
 * model and view.
 */
public class Main {

	public static void main(String[] args) {

		ProgressListener progressListener = new ProgressListener();
		// Default theme is light
		try {
			UIManager.setLookAndFeel(new FlatLightLaf());
		} catch (Exception e) {
			System.err.println("Error while initializing FlatLAF: " + e.getMessage());
		}
		
		RecipeMgrModel model = new RecipeMgrModel();
		model.setProgressListener(progressListener);
		AppFrame view = new AppFrame();
		AppController controller = new AppController(model, view);
		//controller.login(); //for debugging

		// TODO refactor the math in Fraction class and accnt for decimal multiplication
		// TODO BUG: the change to German translation is non-functional atm
		// TODO recipe buttons should just go '...' if they are too long. currently long rcp names widen the entire window
		// TODO BUG: if you have a recipe detached, and switch theme, detached recipe theme doesn't change.
		// TODO loading screen translations
		// TODO handle user clicking "X" while importing recipes. right now it will freeze the UI, throw an exception, then continue importing in the background even if the applicatin is closed
		// TODO splash screen
		// TODO BUG: exporting empty recipe list should cancel and show dialog
		
	}
}
