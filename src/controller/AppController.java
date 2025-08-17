package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Locale;
import javax.swing.JOptionPane;
import definitions.Constants;
import definitions.Recipe;
import model.RecipeMgrModel;
import view.AddRecipeDialog;
import view.AppFrame;
import view.UserInterface;

/*
 * Author: Cailean Bernard
 * Contents: The controller extends ActionListener and registers itself as a listener
 * to necessary buttons contained in the User Interface. When those buttons are
 * clicked, it filters by button and handles the events accordingly.
 */

public class AppController implements ActionListener {

	private RecipeMgrModel model;
	private AppFrame view;
	private AddRecipeDialog rcpDialog;

	public AppController(RecipeMgrModel model, AppFrame view) {
		this.model = model;
		this.view = view;
		initialize();
	}

	public void initialize() {
		model.initModelOffline("recipes.txt");
		view.initializeMainScreen(model.getRecipes());
		view.initializeUIButtons(this);
		view.addButtonListeners(this);
		view.initCloseBtn(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String btnCommand = e.getActionCommand();

		switch (btnCommand) {
		case "add":
			System.out.println("Attempting to add recipe...");
			displayCreateRecipeDialog();
			break;
		case "remove":
			System.out.println("Attempting to remove recipe...");
			handleRemoveRecipe();
			break;
		case "edit":
			System.out.println("Attempting to edit recipe...");
			displayEditRecipeDialog();
			break;
		case "confirmAdd":
			System.out.println("Recipe created. Updating model/view...");
			handleAddRecipe(rcpDialog.getCreatedRecipe());
			break;
		case "confirmEdit":
			System.out.println("Confirming edits to recipe...");
			confirmEditRecipe();
			break;
		case "save":
			System.out.println("Saving recipe list to \"recipes.txt\"");
			handleSaveRecipes();
			break;
		case "applyFilter":
			filterRecipes();
			break;
		case "clearFilter":
			clearFilters();
			break;
		case "english":
			setLanguage(Locale.ENGLISH);
			break;
		case "french":
			setLanguage(Locale.FRENCH);
			break;
		case "german":
			setLanguage(Locale.GERMAN);
			break;
		case "closeWindow":
			model.exportRecipeList("recipes.txt");
			break;
		default:
			System.err.println("Unrecognized button actionCommand.");
			break;
		}
	}
	
	public void filterRecipes() {
		UserInterface ui = view.getUserInterface();
		List<String> filters = ui.getFilters();
		
		if (filters == null) {
			System.out.println("Cancelling filter operation...");
			return;
		}
		
		ui.displayRecipeButtons(filters);
	}
	
	public void clearFilters() {
		UserInterface ui = view.getUserInterface();
		ui.clearFilters();
		ui.displayRecipeButtons();
	}

	public void displayCreateRecipeDialog() {
		rcpDialog = new AddRecipeDialog(this, Constants.ADD_MODE, null, view.getBundle());
		rcpDialog.setVisible(true);
	}

	public void displayEditRecipeDialog() {
		Recipe activeRecipe = view.getUserInterface().getActiveRecipe();
		rcpDialog = new AddRecipeDialog(this, Constants.EDIT_MODE, activeRecipe, view.getBundle());
		rcpDialog.setVisible(true);
	}

	public void handleAddRecipe(Recipe newRecipe) {
		if (newRecipe != null) {
			model.addRecipe(newRecipe);
			view.getUserInterface().setActiveRecipe(newRecipe);
			refreshRecipeList();
			System.out.println("Adding " + newRecipe.getTitle() + " to recipe list...");
			rcpDialog.setCreatedRecipeToNull();
			rcpDialog.dispose();
			rcpDialog = null;
		} else {
			System.out.println("Created recipe is null inside New Recipe Dialog.");
		}
	}

	public void confirmEditRecipe() {
		Recipe rcpEditing = view.getUserInterface().getActiveRecipe();
		List<Recipe> recipes = model.getRecipes();

		// XXX this conditional can likely be removed
		if (recipes.contains(rcpEditing)) {
			int idx = recipes.indexOf(rcpEditing);
			Recipe rcpEdited = rcpDialog.getCreatedRecipe();
			
			// XXX this lazy, but works for now
			if (rcpEdited == null) {
				System.out.println("GetCreatedRecipe() returned null.");
				rcpDialog.dispose();
				rcpDialog = new AddRecipeDialog(this, Constants.EDIT_MODE, rcpEditing, view.getBundle());
				rcpDialog.setVisible(true);
				return;
			}
			
			recipes.set(idx, rcpEdited);
			view.getUserInterface().setActiveRecipe(rcpEdited);
			refreshRecipeList();
			rcpDialog.setCreatedRecipeToNull();
			rcpDialog.dispose();
			rcpDialog = null;			
		} else {
			System.out.println("Active recipe in the view does not exist in the model.");
			JOptionPane.showMessageDialog(null,
					"Select a recipe you wish to edit, then click \"edit\".",
					"No Recipe Selected", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void handleRemoveRecipe() {
		Recipe recipeToRemove = view.getUserInterface().getActiveRecipe();

		if (recipeToRemove == null) {
			System.out.println("No active recipe to remove.");
			return;
		}

		if (model.getRecipes().contains(recipeToRemove)) {
			model.removeRecipe(recipeToRemove);
			System.out.println("Removing " + recipeToRemove.getTitle());
		}

		// tell the UI to update
		view.getUserInterface().clearSelectedRecipeText();
		refreshRecipeList();
	}

	public void refreshRecipeList() {
		UserInterface ui = view.getUserInterface();
		ui.populateRecipeSelectList(model.getRecipes());
		ui.displayRecipeButtons();
	}

	public void handleSaveRecipes() {
		// XXX this should be replaced by a filechooser to get custom paths
		// for now its fine
		model.exportRecipeList("recipes.txt");
	}

	public void handleWindowClosed() {

	}

	public void setLanguage(Locale locale) {
		System.out.println("Switching language to " + locale);
		view.getConfig().setLocale(locale);
		view.getConfig().setResourceBundle("resources.MessagesBundle", locale);
		view.updateBundle();
		view.toggleLangButton(locale);
		view.getUserInterface().updateBundle(locale);
		view.getUserInterface().refreshTranslatableText();
		view.refreshTranslatableText();
		view.packFrame();
	}

}
