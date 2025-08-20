package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Locale;

import javax.swing.JOptionPane;

import db.RecipeDAO;
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
	private RecipeDAO recipeDao;
	private boolean appIsOnline = true;


	public AppController(RecipeMgrModel model, AppFrame view) {
		this.model = model;
		this.view = view;
		this.recipeDao = new RecipeDAO();
		// if internet is available
		if (appIsOnline) {
			initialize();
		} else {
			initializeOffline();
		}
	}

	public void initialize() {
		recipeDao.init();
		model.setRecipes(recipeDao.selectAllRecipesAsList());
		view.initializeMainScreen(model.getRecipes());
		view.initializeUIButtons(this);
		view.addButtonListeners(this);
		view.initCloseBtn(this);
	}

	public void initializeOffline() {
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
		case "sync":
			System.out.println("Loading recipes from .txt and pushing to database...");
			syncRecipesFromTxtToDB();
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
	
	public void syncRecipesFromTxtToDB() {
		// clear the db first
		// load all recipes into model from txt
		// for each recipe in the model, add it to the db
		recipeDao.clearRecipes();
		model.setRecipes(model.importRecipeList("recipes.txt"));
		for (Recipe recipe : model.getRecipes()) {
			recipeDao.insertRecipe(recipe);
		}
		refreshRecipeList();
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
			if (appIsOnline) {
				int newRcpId = recipeDao.insertRecipe(newRecipe);
				newRecipe.setId(newRcpId);
			}

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

		if (rcpEditing == null) {
			System.out.println("Active recipe in the view does not exist in the model.");
			JOptionPane.showMessageDialog(null,
					"Select a recipe you wish to edit, then click \"edit\".",
					"No Recipe Selected", JOptionPane.ERROR_MESSAGE);
		}

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

		if (appIsOnline) {
			recipeDao.updateRecipe(rcpEdited);
		}

		recipes.set(idx, rcpEdited);
		view.getUserInterface().setActiveRecipe(rcpEdited);
		refreshRecipeList();
		rcpDialog.setCreatedRecipeToNull();
		rcpDialog.dispose();
		rcpDialog = null;			
	}

	public void handleRemoveRecipe() {
		Recipe recipeToRemove = view.getUserInterface().getActiveRecipe();

		if (recipeToRemove != null && model.getRecipes().contains(recipeToRemove)) {
			if (appIsOnline) {
				recipeDao.removeRecipe(recipeToRemove.getId());
			}
			model.removeRecipe(recipeToRemove);
			System.out.println("Removing " + recipeToRemove.getTitle());
		} else {
			System.out.println("Recipe == null or not found in local memory.");
			return;
		}

		// tell the UI to update, reset activeRecipe
		// TODO check that clearActiveRecipe didn't break anything.
		view.getUserInterface().clearActiveRecipe();
		view.getUserInterface().clearSelectedRecipeText();
		refreshRecipeList();
	}

	public void refreshRecipeList() {
		UserInterface ui = view.getUserInterface();
		if (appIsOnline) {
			ui.populateRecipeSelectList(recipeDao.selectAllRecipesAsList());
		} else {
			ui.populateRecipeSelectList(model.getRecipes());
		}
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
