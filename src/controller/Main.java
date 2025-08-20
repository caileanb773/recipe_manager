package controller;

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
		RecipeMgrModel model = new RecipeMgrModel();
		AppFrame view = new AppFrame();
		AppController controller = new AppController(model, view);

		// TODO resize recipe info based on window size
		// TODO batch math (10x batch, 5x batch, etcc)
		// TODO light mode/dark mode
		// TODO database instead of local file
		// TODO notification center
		// TODO UI element size (accessibility)
		// TODO icon, make the UI not look so bad
		
		/*
		 * Database Flow:
		 * 
		 * on startup, query the database to see how many records are present.
		 * if there are 0, don't bother loading anything from DB, just start the
		 * app as normal.
		 * if there are some records, load each into local memory (model).
		 * app usage continues like normal. each time a recipe is added, directly
		 * update the db first, then persist the data in local memory.
		 * 
		 * */
		
	}
}
