package ca.prepledger.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.prepledger.config.AppConfig;
import ca.prepledger.config.Configurable;
import ca.prepledger.config.Theme;
import ca.prepledger.model.ConnectionStatus;
import ca.prepledger.model.Recipe;
import ca.prepledger.navigation.ContextArea;
import ca.prepledger.navigation.NavigationHandler;
import ca.prepledger.service.RecipeAPIService;
import ca.prepledger.service.RecipeChangeListener;
import ca.prepledger.service.RecipeService;
import ca.prepledger.session.SessionHandler;
import ca.prepledger.session.UserSession;
import ca.prepledger.util.IconManager;
import ca.prepledger.util.PrepLedgerDialog;
import javafx.beans.property.ObjectProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

/**
 * Contents: Main controller which controls fxml loading application-wide. Also
 * handles dependency injection for controllers of those fxml pages.
 */
public class AppShellController implements NavigationHandler, Configurable, RecipeChangeListener {

	@FXML private BorderPane appShell;
	
	private ContextArea currentContextArea;
	private RecipeService recipeService;
	private RecipeAPIService recipeApiService;
	private RecipeListController recipeListController;
	private SidebarController sidebarController;
	private Parent recipeList;
	private AppConfig appConfig;
	private boolean isRecipeListCached;
	private boolean shouldRefreshRecipes;
	private UserSession currentSession;
	private SessionHandler sessionHandler;
	private ObjectProperty<ConnectionStatus> connectionStatusProperty;
	private static final Logger logger = LoggerFactory.getLogger(AppShellController.class);


	@FXML
	private void initialize() {
		isRecipeListCached = false;
		shouldRefreshRecipes = true;
		currentContextArea = ContextArea.RECIPES;
		recipeService = new RecipeService();
		recipeApiService = new RecipeAPIService();
		recipeService.registerRecipeChangeListener(this);
	}
	
	public void populateInitialWindow() {
		loadSidebar();
		loadDefaultContextArea();
	}

	private void loadSidebar() {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/fxml/sidebar/Sidebar.fxml")
					);

			Parent sidebar = loader.load();

			sidebarController = loader.getController();
			sidebarController.setConnectionStatus(connectionStatusProperty);
			sidebarController.setNavigationHandler(this);
			sidebarController.cacheButtons();
			sidebarController.setUserProfileDetails(currentSession);

			appShell.setLeft(sidebar);

			// Highlight the "Recipes" Sidebar nav button
			sidebarController.setInitialScreenNavButtonSelected();

		} catch (IOException e) {
			logger.error("loadSidebar(): IOException caught while loading Sidebar.fxml: {}", e);
		}
	}

	private void loadDefaultContextArea() {
		try {
			showRecipes();
		} catch (IOException e) {
			logger.error("loadDefaultContextArea(): IOException caught during showRecipes(): {}", e);
		}
	}
	
	public void initializeThemeDependencies() {
		appConfig.setThemeChangeListener(this::applyTheme);
		applyTheme(appConfig.getTheme());
	}
	
	@Override
	public void navigateTo(ContextArea contextArea) throws IOException {
		if (currentContextArea == contextArea) {
			return;
		}
		
		if (recipeListController != null && currentContextArea == ContextArea.RECIPES) {
			recipeListController.resetSearchFilter();
		}

		currentContextArea = contextArea;

		switch (contextArea) {
		case RECIPES:
			showRecipes();
			break;

		case SETTINGS:
			showSettings();
			break;

		case IMPORT_EXPORT:
			showImportExport();
			break;

		case NOTIFICATIONS:
			showNotifications();
			break;

		case PREP_LISTS:
			showPrepLists();
			break;

		case ADD_RECIPE:
			showAddRecipe();
			break;
			
		case COSTING:
			showCosting();
			break;
			
		case EDIT_RECIPE:
			logger.error("navigateTo(): Tried to navigate to EDIT_RECIPE without an associated Recipe object.");
			break;
			
		case VIEW_RECIPE:
			logger.error("navigateTo(): Tried to navigate to VIEW_RECIPE without an associated Recipe object.");
			break;
			
		default:
			break;			
		}
		
		if (sidebarController != null) {
			sidebarController.setActiveButtonStyling(contextArea.toString().toLowerCase());			
		}
	}

	@Override
	public void navigateTo(ContextArea contextArea, Recipe recipe) throws IOException {
		if (currentContextArea == contextArea) {
			return;
		}
		
		if (currentContextArea == ContextArea.RECIPES) {
			recipeListController.resetSearchFilter();
		}
		
		currentContextArea = contextArea;

		switch (contextArea) {
		case EDIT_RECIPE:
			showEditRecipe(recipe);
			break;
		case VIEW_RECIPE:
			showViewRecipe(recipe);
			break;
		case RECIPES:
			removeRecipeFromRecipeViewContext(recipe);
			break;
		default:
			logger.error("navigateTo(Recipe recipe): Tried to navigate to a page with a recipe that doesn't require a recipe object.");
			break;
		}
		
		sidebarController.setActiveButtonStyling(contextArea.toString().toLowerCase());
	}

	private void showRecipes() throws IOException {
		/* Only initialize the recipelist controller once. This way we can keep
		 * the reference and don't have to re-create all the recipe FXML cards,
		 * which is slow and costly. */
		if (!isRecipeListCached) {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/fxml/recipes/RecipeList.fxml")
					);
			
			recipeList = loader.load();
			recipeListController = loader.getController();
			recipeListController.setNavigationHandler(this);
			recipeListController.setAppConfig(appConfig);
			recipeListController.initializeViewingMode();
			recipeListController.initializeSearchField();
			recipeListController.setRecipeService(recipeService);
			recipeListController.setSession(currentSession);			
			recipeListController.setRecipeApiService(recipeApiService);
			
			isRecipeListCached = true;
		}
		
		appShell.setCenter(recipeList);
		IconManager.applyThemeToTree(recipeList, appConfig.getTheme(), null);

		if (shouldRefreshRecipes) {
			recipeListController.refreshDisplayedRecipes();	
			shouldRefreshRecipes = false;
		}
	}

	private void showCosting() throws IOException {
		Parent costing = FXMLLoader.load(
				getClass().getResource("/fxml/costing/Costing.fxml")
				);

		appShell.setCenter(costing);
	}

	private void showSettings() throws IOException {
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource("/fxml/settings/Settings.fxml"));
		
		Parent settings = loader.load();
		SettingsController controller = loader.getController();
		
		// Inject dependency to appconfig
		controller.setAppConfig(appConfig);
		
		// Initialize
		controller.setSettingsFieldsFromConfig();
		controller.setVersionLabel();
		controller.setSessionHandler(sessionHandler);
		
		appShell.setCenter(settings);
		IconManager.applyThemeToTree(settings, appConfig.getTheme(), null);
	}

	private void showImportExport() throws IOException {
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource("/fxml/import-export/ImportExport.fxml"));

		Parent importExport = loader.load();

		ImportExportController controller = loader.getController();

		// Inject dependency to recipeservice
		controller.setRecipeService(recipeService);

		appShell.setCenter(importExport);
		IconManager.applyThemeToTree(importExport, appConfig.getTheme(), null);
	}

	private void showNotifications() throws IOException {
		Parent notifications = FXMLLoader.load(
				getClass().getResource("/fxml/notifications/Notifications.fxml")
				);

		appShell.setCenter(notifications);
	}

	private void showPrepLists() throws IOException {
		Parent prepLists = FXMLLoader.load(
				getClass().getResource("/fxml/prep-lists/PrepLists.fxml")
				);

		appShell.setCenter(prepLists);
		IconManager.applyThemeToTree(prepLists, appConfig.getTheme(), null);
	}

	private void showAddRecipe() throws IOException {
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource("/fxml/recipes/NewRecipe.fxml"));

		Parent addRecipe = loader.load();

		// Register this class as the RecipeList's navigation handler
		NewRecipeController controller = loader.getController();

		// inject dependencies
		injectNewRecipeControllerDependencies(controller);

		// start with a blank ingredientrow
		if (appConfig.areTooltipsOn()) {
			controller.setRecipeTooltips();
			controller.addNewIngredientRow(true);
		} else {
			controller.addNewIngredientRow(false);
		}

		appShell.setCenter(addRecipe);
		IconManager.applyThemeToTree(addRecipe, appConfig.getTheme(), null);
	}

	private void showEditRecipe(Recipe recipe) throws IOException {
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource("/fxml/recipes/NewRecipe.fxml"));

		Parent addRecipe = loader.load();

		// Register this class as the RecipeList's navigation handler
		NewRecipeController controller = loader.getController();
		injectNewRecipeControllerDependencies(controller);
		controller.setRecipeToEdit(recipe);

		// Inject dependencies
		controller.setRecipeService(recipeService);

		appShell.setCenter(addRecipe);
		IconManager.applyThemeToTree(addRecipe, appConfig.getTheme(), null);
	}
	
	private void injectNewRecipeControllerDependencies(NewRecipeController controller) {
		controller.setNavigationHandler(this);
		controller.setRecipeService(recipeService);
		controller.setAppConfig(appConfig);
		controller.setCurrentSession(currentSession);
		controller.setRecipeApiService(recipeApiService);
	}
	
	private void showViewRecipe(Recipe recipe) throws IOException {
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource("/fxml/recipes/RecipeView.fxml"));

		Parent viewRecipe = loader.load();

		// Register this class as the RecipeList's navigation handler
		RecipeViewController controller = loader.getController();
		controller.setNavigationHandler(this);
		controller.setRecipeToView(recipe);
		controller.populateOverview();
		controller.setScaleProperties();

		appShell.setCenter(viewRecipe);
		IconManager.applyThemeToTree(viewRecipe, appConfig.getTheme(), null);
	}
		
	/** 
	 * This method is called from within the RecipeView.fxml context, when the
	 * user clicks "Remove Recipe" and confirms their choice. The recipe is
	 * removed from the repository, and the user must be navigated back to
	 * RecipeList.fxml, as the recipe they were viewing is no longer valid.
	 * 
	 * Fetches the currently viewed recipe from the 
	 * 
	 * @since 0.1.2
	 */
	public void removeRecipeFromRecipeViewContext(Recipe recipe) throws IOException {
		// fail early if recipe has no ID to pass to backend
		if (recipe.getId() == null) {
		    logger.warn("Cannot delete recipe: missing backend ID.");
		    PrepLedgerDialog errorDialog = new PrepLedgerDialog();
		    errorDialog.showRecipeBackendActionFailedDialog(
		            "Missing backend ID.",
		            "delete");
		    return;
		}

		Task<Void> task = new Task<>() {
		    @Override
		    protected Void call() throws Exception {
		        recipeApiService.deleteRecipe(
		                currentSession, recipe.getId());
		        return null;
		    }
		};

		// only delete from local memory if this thread succeeded
		task.setOnSucceeded(event -> {
		    recipeService.removeRecipe(recipe);
			try {
				showRecipes();
			} catch (IOException e) {
				logger.error("call(), showRecipes(): IOException encountered: {}", e);
			}

		});

		task.setOnFailed(event -> {
		    logger.error("Failed to delete recipe from backend.",
		            task.getException());

		    PrepLedgerDialog errorDialog = new PrepLedgerDialog();
		    errorDialog.showRecipeBackendActionFailedDialog(
		            task.getException().getMessage(),
		            "delete");
		});

		Thread thread = new Thread(task, "recipe-delete");
		thread.setDaemon(true);
		thread.start();
	}

	@Override
	public void onRecipesChanged() {
		shouldRefreshRecipes = true;
	}
	
	public void applyTheme(Theme theme) {
	    String stylesheet = switch (theme) {
	        case LIGHT -> "/css/theme.css";
	        case DARK -> "/css/theme-midnight.css";
	    };
	    
	    logger.info("Switching theme to: {}", theme);

	    var scene = appShell.getScene();

	    if (scene == null) {
	        return;
	    }

	    scene.getStylesheets().removeIf(s ->
	        s.contains("/css/theme.css") ||
	        s.contains("/css/theme-midnight.css")
	    );

	    var resource = getClass().getResource(stylesheet);

	    if (resource == null) {
	        throw new IllegalStateException(
	            "Theme stylesheet not found: " + stylesheet
	        );
	    }

	    scene.getStylesheets().add(resource.toExternalForm());

	    appShell.applyCss();
	    appShell.requestLayout();
	    IconManager.applyThemeToTree(appShell, theme, appShell.getLeft());
	}
	
	public ContextArea getCurrentContextArea() {
		return this.currentContextArea;
	}	
	
	@Override
	public void setAppConfig(AppConfig appConfig) {
		this.appConfig = appConfig;
	}

	public void setCurrentSession(UserSession currentSession) {
	    this.currentSession = currentSession;
	    getRecipesAsync();
	}
	
	private void getRecipesAsync() {
		if (currentSession != null) {
		    Task<List<Recipe>> task = new Task<>() {
		        @Override
		        protected List<Recipe> call() throws Exception {
		            return recipeApiService.fetchRecipes(currentSession);
		        }
		    };

		    task.setOnSucceeded(event -> {
		        recipeService.addRecipes(task.getValue());
		        recipeListController.refreshDisplayedRecipes();
		        shouldRefreshRecipes = false;
		    });

		    task.setOnFailed(event -> {
		        logger.error("Failed to fetch recipes from backend.",
		                task.getException());
		    });

		    Thread thread = new Thread(task, "recipe-fetch");
		    thread.setDaemon(true);
		    thread.start();
		}
	}
	
	public void setSessionHandler(SessionHandler sessionHandler) {
		this.sessionHandler = sessionHandler;
	}
	
	public void setConnectionStatus(
	        ObjectProperty<ConnectionStatus> status) {
	    this.connectionStatusProperty = status;
	}
		
}
