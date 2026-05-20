package controller;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.io.FilenameUtils;
import db.RecipeDAO;
import definitions.Constants;
import definitions.Ingredient;
import definitions.Notification;
import definitions.NotificationType;
import definitions.Recipe;
import definitions.StaffMember;
import definitions.Theme;
import model.Model;
import util.Config;
import util.ProgressListener;
import view.AddRecipeDialog;
import view.AppFrame;
import view.LoginScreen;
import view.NotificationScreen;
import view.RecipeScreen;
import view.RegisterScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/*
 * Author: Cailean Bernard
 * Contents: The controller extends ActionListener and registers itself as a listener
 * to necessary buttons contained in the User Interface. When those buttons are
 * clicked, it filters by button and handles the events accordingly.
 */
public class AppController implements ActionListener {

	// Swing
	private Model model;
	private AppFrame view;
	private AddRecipeDialog rcpDialog;

	// Other
	private boolean appIsOnline = true;
	private RecipeDAO recipeDao;
	private ResourceBundle bundle;
	private ProgressListener progressListener;
	private static final Logger logger = LoggerFactory.getLogger(AppController.class);


	public AppController(Model model, AppFrame view) {
		this.model = model;
		this.view = view;
		this.recipeDao = new RecipeDAO();
		//		if (appIsOnline) {
		//			initialize(ONLINE);
		//		} else {
		//			initialize(OFFLINE);
		//		}
	}

	public void initialize(boolean mode) {
		reportProgress(40, "Initializing controller...");
		bundle = view.getBundle();

		if (mode == Constants.ONLINE) {
			reportProgress(50, "Loading recipes...");
			recipeDao.init();
			model.setRecipes(recipeDao.selectAllRecipesAsList());
		} if (mode == Constants.OFFLINE) { // XXX for now, this is never called.
			reportProgress(50, "Loading recipes...");
			model.initModelOffline("backup.rcp");
		}

		reportProgress(60, "Recipes loaded.");
		view.registerController(this);
		view.registerControllerInSubscreens(this);
		reportProgress(70, "Finishing up...");
		view.populateRecipeList(model.getRecipes());
		initAllButtons();
		initTheme();
		reportProgress(80, "Theme initialized.");
	}

	private void initAllButtons() {
		view.initiRecipeScreenButtons();
		view.initLoginScreenButtons();
		view.initRegisterScreenButtons();
		view.initCloseBtn();
		view.addButtonListeners();
	}

	private void initTheme() {
		view.fireThemeChangeEvent(view.getConfig().getTheme());
		view.setViewVisible(true);
	}

	public void setProgressListener(ProgressListener progressListener) {
		if (progressListener != null) {
			this.progressListener = progressListener;
		}
	}

	public void reportProgress(int percent, String msg) {
		if (progressListener != null) {
			progressListener.onProgress(percent, msg);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String[] cmdData = e.getActionCommand().split("&");
		String cmd = cmdData[Constants.CMD_IDX];
		String cmdOpt = null;

		if (cmdData.length > Constants.MAX_VALID_DATA_LEN) {
			cmdOpt = cmdData[Constants.DATA_IDX];
		}

		switch (cmd) {
		case "add":
			logger.info("Attempting to add recipe");
			displayCreateRecipeDialog();
			break;
		case "remove":
			logger.info("Attempting to remove recipe");
			handleRemoveRecipe();
			break;
		case "edit":
			logger.info("Attempting to edit recipe");
			displayEditRecipeDialog();
			break;
		case "confirmAdd":
			logger.info("Recipe created. Updating model/view");
			handleAddRecipe(rcpDialog.getCreatedRecipe());
			break;
		case "confirmEdit":
			logger.info("Confirming edits to recipe");
			confirmEditRecipe();
			break;
		case "export":
			logger.info("Preparing to export recipes");
			handleExportRecipes();
			break;
		case "import":
			logger.info("Preparing to import recipes");
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
		case "closeWithBackup":
			handleCloseWithBackup();
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
		case "switchTheme":
			handleThemeSwitch(cmdData[Constants.DATA_IDX]);
			break;
		case "notifications":
			notifications();
			break;
		case "showRcpScreen":
			showRcpScreen();
			break;

			// XXX Debug Options, remove when finished
		case "dbgAddNotif":
			dbgAddNotif();
			break;
		default:
			logger.warn("Unrecognized button actionCommand.");
			break;
		}
	}

	public void handleThemeSwitch(String themeTitle) {
		logger.info("Switching theme: " + themeTitle);
		view.changeThemeInChildren(Theme.valueOf(themeTitle));
	}

	public void returnToLoginAfterRegister(String newEmail) {
		LoginScreen loginScreen = view.getLoginScreen();
		loginScreen.setEmail(newEmail);
		view.switchScreen("LOGIN");
		loginScreen.grabFocus("PASSWORD_FIELD");
		loginScreen.setRemembering(true);
	}

	public void register() {
		view.switchScreen("REGISTER_SCREEN");
	}

	public void cancelRegister() {
		view.getRegisterScreen().clearFields();
		view.switchScreen("LOGIN");
	}

	public void login() {
		logger.info("Attempting to log in");
		if (LoginScreen.isRemembering()) {
			Config.setLastEmail(view.getLoginScreen().getEmail());
		} else {
			Config.setLastEmail(null);
		}
		view.switchScreen("RECIPE_SCREEN");
	}

	public void showRcpScreen() {
		logger.info("Switching to Recipe Screen...");
		if (LoginScreen.isRemembering()) {
			Config.setLastEmail(view.getLoginScreen().getEmail());
		} else {
			Config.setLastEmail(null);
		}
		view.switchScreen("RECIPE_SCREEN");
	}

	public void logout() {
		logger.info("Logging out");
		view.switchScreen("LOGIN");
		view.getLoginScreen().grabFocus("PASSWORD_FIELD");
	}

	public void notifications() {
		logger.info("Switching to Notification Center...");
		view.switchScreen("NOTIFICATIONS");
	}

	public void handleCloseWithBackup() {
		bundle = view.getBundle();

		try {
			model.exportRecipeList("backup.rcp");
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
		RecipeScreen ui = view.getRecipeScreen();
		List<String> filters = ui.getFilters();

		if (filters == null) {
			logger.info("Cancelling filter operation.");
			return;
		}

		ui.displayRecipeButtons(filters);
	}

	public void clearFilters() {
		RecipeScreen ui = view.getRecipeScreen();
		ui.clearFilters();
		ui.displayRecipeButtons();
	}

	public void displayCreateRecipeDialog() {
		rcpDialog = new AddRecipeDialog(this, Constants.ADD_MODE, null, view.getBundle());
		rcpDialog.setVisible(true);
	}

	public void displayEditRecipeDialog() {
		Recipe activeRecipe = view.getRecipeScreen().getActiveRecipe();
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
			view.getRecipeScreen().setActiveRecipe(newRecipe);
			refreshRecipeList();
			logger.info("Adding {} to recipe list", newRecipe.getTitle());
			rcpDialog.setCreatedRecipeToNull();
			rcpDialog.dispose();
			rcpDialog = null;
		} else {
			logger.warn("Created recipe is null inside New Recipe Dialog.");
		}
	}

	public void confirmEditRecipe() {
		RecipeScreen recipeScreen = view.getRecipeScreen();
		Recipe rcpEditing = recipeScreen.getActiveRecipe();
		List<Recipe> recipes = model.getRecipes();

		if (rcpEditing == null) {
			logger.warn("Active recipe in the view does not exist in the model.");
			JOptionPane.showMessageDialog(null,
					"Select a recipe you wish to edit, then click \"edit\".",
					"No Recipe Selected", JOptionPane.ERROR_MESSAGE);
		}

		int idx = recipes.indexOf(rcpEditing);
		Recipe rcpEdited = rcpDialog.getCreatedRecipe();

		if (rcpEdited == null) {
			logger.info("Cancelling recipe edit.");
			rcpDialog.setCreatedRecipeToNull();
			rcpDialog.dispose();
			rcpDialog = null;
			return;
		}

		if (appIsOnline) {
			recipeDao.updateRecipe(rcpEdited);
		}

		recipes.set(idx, rcpEdited);
		recipeScreen.setActiveRecipe(rcpEdited);
		refreshRecipeList();
		rcpDialog.setCreatedRecipeToNull();
		rcpDialog.dispose();
		rcpDialog = null;
	}

	public void handleRemoveRecipe() {
		RecipeScreen recipeScreen = view.getRecipeScreen();
		Recipe recipeToRemove = recipeScreen.getActiveRecipe();

		if (recipeToRemove != null && model.getRecipes().contains(recipeToRemove)) {
			if (appIsOnline) {
				recipeDao.removeRecipe(recipeToRemove.getId());
			}
			model.removeRecipe(recipeToRemove);
			logger.info("Removing {}", recipeToRemove.getTitle());
		} else {
			logger.warn("Recipe == null or not found in local memory.");
			return;
		}

		recipeScreen.clearActiveRecipe();
		recipeScreen.clearSelectedRecipeText();
		refreshRecipeList();
		recipeScreen.focusFirstRecipe();
	}

	public void refreshRecipeList() {
		RecipeScreen ui = view.getRecipeScreen();
		if (appIsOnline) {
			ui.populateRecipeSelectList(recipeDao.selectAllRecipesAsList());
		} else {
			ui.populateRecipeSelectList(model.getRecipes());
		}

		ui.displayRecipeButtons();
	}

	// TODO edit this to include JSON format eventually
	public void handleExportRecipes() {
		bundle = view.getBundle();

		if (model.getRecipes().isEmpty()) {
			JOptionPane.showMessageDialog(null,
					bundle.getString("export.noRecipes"),
					bundle.getString("error.title"),
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle(bundle.getString("export"));
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				bundle.getString("filter"), "rcp");
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.addChoosableFileFilter(filter);
		int option = chooser.showSaveDialog(null);

		if (option == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();

			// Always ensure .rcp extension
			String baseName = FilenameUtils.getBaseName(file.getName());
			file = new File(file.getParentFile(), baseName + ".rcp");
			String filePath = file.getAbsolutePath();

			if (file.exists()) {
				int confirm = JOptionPane.showConfirmDialog(
						null,
						bundle.getString("export.fileexists"),
						bundle.getString("export.confirmoverwrite"),
						JOptionPane.YES_NO_OPTION);

				if (confirm != JOptionPane.YES_OPTION) {
					logger.info("Export cancelled: user chose not to overwrite.");
					return;
				}
			}

			try {
				model.exportRecipeList(file.getAbsolutePath());
				JOptionPane.showMessageDialog(null,
						bundle.getString("export.success"),
						bundle.getString("export.title"),
						JOptionPane.INFORMATION_MESSAGE);
				logger.info("Recipes exported to: {}", filePath);
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
			logger.info("Cancelling export.");
		}
	}

	public void handleImportRecipes() {
		bundle = view.getBundle();
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle(bundle.getString("import.title"));
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				bundle.getString("filter"), "rcp");
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.addChoosableFileFilter(filter);
		int option = chooser.showOpenDialog(null);

		if (option == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			String fileName = file.getName();
			String extension = FilenameUtils.getExtension(fileName);

			if (extension.equalsIgnoreCase("rcp")) {

				try {
					int totalRecipes = countLines(file.getAbsolutePath());
					List<Recipe> rcpList = model.importRecipeList(file.getAbsolutePath());
					logger.info("Preparing to import " + totalRecipes + " recipes.");

					if (rcpList.size() == 0) {
						JOptionPane.showMessageDialog(null,
								bundle.getString("import.norecipes"),
								bundle.getString("error.title"),
								JOptionPane.ERROR_MESSAGE);
					}

					if (appIsOnline) {

						// XXX Loading bar for recipes
						JDialog progressDialog = new JDialog((JFrame) null,
								bundle.getString("importLoading"), true);
						JProgressBar progressBar = new JProgressBar(0, totalRecipes);
						progressBar.setStringPainted(true);
						progressDialog.add(progressBar, BorderLayout.CENTER);
						progressDialog.setSize(300, 75);
						progressDialog.setLocationRelativeTo(null);
						progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

						// Swingworker for adding to the DB
						SwingWorker<Void, Integer> worker = new SwingWorker<>() {
							@Override
							protected Void doInBackground() throws Exception {
								int progress = 0;

								for (Recipe rcp : rcpList) {
									int id = recipeDao.insertRecipe(rcp);
									rcp.setId(id);
									publish(++progress);
								}
								return null;
							}

							@Override
							protected void process(List<Integer> chunks) {
								int latestProgress = chunks.get(chunks.size() -1);
								progressBar.setValue(latestProgress);
							}

							@Override
							protected void done() {
								progressDialog.dispose();
								JOptionPane.showMessageDialog(null,
										bundle.getString("importSucc"),
										bundle.getString("importSuccTitle"),
										JOptionPane.INFORMATION_MESSAGE);
							}
						};

						//						progressDialog.addWindowListener(new WindowAdapter() {
						//							@Override
						//							public void windowClosing(WindowEvent e) {
						//								worker.cancel(true);
						//								progressDialog.dispose();
						//
						//								// TODO show "cancelled" dialog
						//								// TODO actually "cancel" the import. load into 
						//								// temporary array and only commit the import if 
						//								// it wasn't cancelled so things aren't
						//							}
						//						});

						worker.execute();
						progressDialog.setVisible(true);
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

	private int countLines(String fileName) {
		int lines = 0;
		try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
			while (reader.readLine() != null) {
				lines++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}

	public void setLanguage(Locale locale) {
		logger.info("Switching language to {}", locale);
		Config cfg = view.getConfig();
		RecipeScreen rcp = view.getRecipeScreen();
		LoginScreen log = view.getLoginScreen();
		RegisterScreen reg = view.getRegisterScreen();
		cfg.setLocale(locale);
		cfg.setResourceBundle("MessagesBundle", locale);
		view.updateBundle();
		view.toggleLangButton(locale);
		view.refreshTranslatableText();
		rcp.updateBundle(locale);
		rcp.refreshTranslatable();
		log.updateBundle(locale);
		log.refreshTranslatable();
		reg.updateBundle(locale);
		reg.refreshTranslatable();
	}

	public Model getModel() {
		return this.model;
	}

	public AppFrame getView() {
		return this.view;
	}

	////////////////////////////////////////////////////////////////////////////
	// 
	// D E B U G G I N G  M E T H O D S
	//
	////////////////////////////////////////////////////////////////////////////
	public void dbgAddNotif() {
		NotificationScreen notifScreen = view.getNotificationScreen();
		ArrayList<Notification> notifs = notifScreen.getNotifications();

		// First
		LocalDateTime timeSent = LocalDateTime.now().minusDays(1);
		StaffMember staff = new StaffMember(0, "bob@gmail.com", "Bob");
		NotificationType type = NotificationType.ADD;
		Recipe rcp = new Recipe("Burger", new ArrayList<Ingredient>(), "Make burger");
		String optNotes = "This is a test of how long optional notes can be";
		Notification n = new Notification(timeSent, staff, type, rcp, optNotes);
		notifScreen.addNotification(n);

		timeSent = LocalDateTime.now().plusDays(3);
		staff = new StaffMember(0, "frank@gmoil.com", "Frank");
		type = NotificationType.EDIT;
		rcp = new Recipe("Aioli", new ArrayList<Ingredient>(), "Mix it all up");
		optNotes = "No notes";
		n = new Notification(timeSent, staff, type, rcp, optNotes);
		notifScreen.addNotification(n);
		
		timeSent = LocalDateTime.now();
		staff = new StaffMember(0, "derek@gmoil.com", "Derek");
		type = NotificationType.ADD;
		rcp = new Recipe("Secret Sauce", new ArrayList<Ingredient>(), "Wahoo");
		optNotes = "Mix it good";
		n = new Notification(timeSent, staff, type, rcp, optNotes);
		notifScreen.addNotification(n);
		
		// Refresh
		notifScreen.populateNotificationButtonList(notifs);
		notifScreen.displayNotifications();
	}

}
