package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FilenameUtils;

import db.RecipeDAO;
import definitions.Constants;
import definitions.Recipe;
import model.RecipeMgrModel;
import util.Config;
import view.AddRecipeDialog;
import view.AppFrame;
import view.LoginScreen;
import view.RegisterScreen;
import view.UserInterfaceScreen;

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
		view.initializeLoginButtons(this);
		view.initializeRegisterButtons(this);
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
		String[] cmdData = e.getActionCommand().split("&");
		String cmd = cmdData[0];
		String cmdOpt = null;

		if (cmdData.length > 1) {
			cmdOpt = cmdData[1];
		}

		switch (cmd) {
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
		case "export":
			System.out.println("Exporting recipe list...");
			handleExportRecipes();
			break;
		case "import":
			System.out.println("Loading recipes from .txt and pushing to database...");
			handleImportRecipes();
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
			handleCloseWindow();
			break;
		case "login":
			login();
			break;
		case "logout":
			logout();
			break;
		case "register":
			register();
			break;
		case "cancelRegister":
			cancelRegister();
			break;
		case "returnToLoginAfterRegister":
			returnToLoginAfterRegister(cmdOpt);
			break;
		default:
			System.err.println("Unrecognized button actionCommand.");
			break;
		}
	}

	public void returnToLoginAfterRegister(String newEmail) {
		view.getLoginScreen().setEmail(newEmail);
		view.switchScreen("LOGIN");
		view.getLoginScreen().initFocus("PASSWORD_FIELD");
	}

	public void register() {
		view.switchScreen("REGISTER_SCREEN");
	}

	public void cancelRegister() {
		view.getRegisterScreen().clearFields();
		view.switchScreen("LOGIN");
	}

	public void login() {
		System.out.println("Attempting to log in...");
		LoginScreen login = view.getLoginScreen();
		if (LoginScreen.isRemembering()) {
			Config.setLastEmail(login.getEmail());
		} else {
			Config.setLastEmail(null);
		}
		view.switchScreen("USER_INTERFACE");
	}

	public void logout() {
		System.out.println("Logging out...");
		view.switchScreen("LOGIN");
	}

	public void handleCloseWindow() {
		ResourceBundle bundle = view.getBundle();
		try {
			model.exportRecipeList("backup.txt");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,
					bundle.getString("export.ioerror") + "\n" + e.getMessage(),
					bundle.getString("error.title"),
					JOptionPane.ERROR_MESSAGE);
		} catch (SecurityException e) {
			JOptionPane.showMessageDialog(null,
					bundle.getString("export.securityerror"),
					bundle.getString("error.title"),
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public void filterRecipes() {
		UserInterfaceScreen ui = view.getUserInterface();
		List<String> filters = ui.getFilters();

		if (filters == null) {
			System.out.println("Cancelling filter operation...");
			return;
		}

		ui.displayRecipeButtons(filters);
	}

	public void clearFilters() {
		UserInterfaceScreen ui = view.getUserInterface();
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

		if (rcpEdited == null) {
			System.err.println("Cancelling recipe edit.");
			rcpDialog.setCreatedRecipeToNull();
			rcpDialog.dispose();
			rcpDialog = null;
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

		view.getUserInterface().clearActiveRecipe();
		view.getUserInterface().clearSelectedRecipeText();
		refreshRecipeList();
	}

	public void refreshRecipeList() {
		UserInterfaceScreen ui = view.getUserInterface();
		if (appIsOnline) {
			ui.populateRecipeSelectList(recipeDao.selectAllRecipesAsList());
		} else {
			ui.populateRecipeSelectList(model.getRecipes());
		}
		ui.displayRecipeButtons();
	}

	// TODO edit this to include JSON format eventually
	public void handleExportRecipes() {
		ResourceBundle bundle = view.getBundle();
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle(bundle.getString("export"));
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				bundle.getString("filter"), "txt");
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.addChoosableFileFilter(filter);

		int option = chooser.showSaveDialog(null);

		if (option == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();

			// Always ensure .txt extension
			String baseName = FilenameUtils.getBaseName(file.getName());
			file = new File(file.getParentFile(), baseName + ".txt");
			String filePath = file.getAbsolutePath();

			if (file.exists()) {
				int confirm = JOptionPane.showConfirmDialog(
						null,
						bundle.getString("export.fileexists"),
						bundle.getString("export.confirmoverwrite"),
						JOptionPane.YES_NO_OPTION);

				if (confirm != JOptionPane.YES_OPTION) {
					System.out.println("Export cancelled: user chose not to overwrite.");
					return;
				}
			}

			try {
				model.exportRecipeList(file.getAbsolutePath());
				JOptionPane.showMessageDialog(null,
						bundle.getString("export.success"),
						bundle.getString("export.title"),
						JOptionPane.INFORMATION_MESSAGE);
				System.out.println("Recipes exported to: " + filePath);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,
						bundle.getString("export.ioerror") + "\n" + e.getMessage(),
						bundle.getString("error.title"),
						JOptionPane.ERROR_MESSAGE);
			} catch (SecurityException e) {
				JOptionPane.showMessageDialog(null,
						bundle.getString("export.securityerror"),
						bundle.getString("error.title"),
						JOptionPane.ERROR_MESSAGE);
			}

		} else {
			System.out.println("Cancelling export...");
		}
	}

	public void handleImportRecipes() {
		ResourceBundle bundle = view.getBundle();
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle(bundle.getString("import.title"));
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				bundle.getString("filter"), "txt");
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.addChoosableFileFilter(filter);
		int option = chooser.showOpenDialog(null);

		if (option == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			String fileName = file.getName();
			String extension = FilenameUtils.getExtension(fileName);

			if (extension.equalsIgnoreCase("txt")) {

				try {
					List<Recipe> rcpList = model.importRecipeList(file.getAbsolutePath());

					if (rcpList.size() == 0) {
						JOptionPane.showMessageDialog(null,
								bundle.getString("import.norecipes"),
								bundle.getString("error.title"),
								JOptionPane.ERROR_MESSAGE);
					}

					if (appIsOnline) {
						for (Recipe rcp : rcpList) {
							int id = recipeDao.insertRecipe(rcp);
							rcp.setId(id);
						}
					}

					// no need to handle if !appIsOnline, already handled by importRecipeList()

				} catch (IOException e) {
					JOptionPane.showMessageDialog(null,
							bundle.getString("import.ioerror"),
							bundle.getString("error.title"),
							JOptionPane.ERROR_MESSAGE);
				} catch (SecurityException e) {
					JOptionPane.showMessageDialog(null,
							bundle.getString("import.securityerror"),
							bundle.getString("error.title"),
							JOptionPane.ERROR_MESSAGE);
				} catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
					JOptionPane.showMessageDialog(null,
							bundle.getString("import.parseerror"),
							bundle.getString("error.title"),
							JOptionPane.ERROR_MESSAGE);
				}

			} else {

				JOptionPane.showMessageDialog(
						null,
						bundle.getString("import.unsupportedftype"),
						bundle.getString("error.title"),
						JOptionPane.OK_OPTION);
			}
		}

		refreshRecipeList();
	}

	// TODO clean this up
	public void setLanguage(Locale locale) {
		System.out.println("Switching language to " + locale);

		Config cfg = view.getConfig();
		UserInterfaceScreen ui = view.getUserInterface();
		LoginScreen log = view.getLoginScreen();
		RegisterScreen reg = view.getRegisterScreen();

		cfg.setLocale(locale);
		cfg.setResourceBundle("MessagesBundle", locale);
		view.updateBundle();
		view.toggleLangButton(locale);
		ui.updateBundle(locale);
		ui.refreshTranslatable();
		view.refreshTranslatableText();
		log.updateBundle(locale);
		log.refreshTranslatable();
		reg.updateBundle(locale);
		reg.refreshTranslatable();
	}

}
