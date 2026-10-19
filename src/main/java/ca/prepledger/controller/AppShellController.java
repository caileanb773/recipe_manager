package ca.prepledger.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.prepledger.config.AppConfig;
import ca.prepledger.config.Configurable;
import ca.prepledger.model.Recipe;
import ca.prepledger.navigation.ContextArea;
import ca.prepledger.navigation.NavigationHandler;
import ca.prepledger.service.RecipeChangeListener;
import ca.prepledger.service.RecipeService;
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
	private RecipeListController recipeListController;
	private SidebarController sidebarController;
	private Parent recipeList;
	private AppConfig appConfig;
	private boolean isRecipeListCached;
	private boolean shouldRefreshRecipes;
	private static final Logger logger = LoggerFactory.getLogger(AppShellController.class);


	@FXML
	private void initialize() {
		isRecipeListCached = false;
		shouldRefreshRecipes = true;
		currentContextArea = ContextArea.RECIPES;
		recipeService = new RecipeService();
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
			sidebarController.setNavigationHandler(this);
			sidebarController.cacheButtons();

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
			
			isRecipeListCached = true;
		}
		
		appShell.setCenter(recipeList);

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
		
		appShell.setCenter(settings);
	}

	private void showImportExport() throws IOException {
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource("/fxml/import-export/ImportExport.fxml"));

		Parent importExport = loader.load();

		ImportExportController controller = loader.getController();

		// Inject dependency to recipeservice
		controller.setRecipeService(recipeService);

		appShell.setCenter(importExport);
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
	}

	private void showAddRecipe() throws IOException {
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource("/fxml/recipes/NewRecipe.fxml"));

		Parent addRecipe = loader.load();

		// Register this class as the RecipeList's navigation handler
		NewRecipeController controller = loader.getController();

		// inject dependencies
		controller.setNavigationHandler(this);
		controller.setRecipeService(recipeService);
		controller.setAppConfig(appConfig);

		// start with a blank ingredientrow
		if (appConfig.areTooltipsOn()) {
			controller.setRecipeTooltips();
			controller.addNewIngredientRow(true);
		} else {
			controller.addNewIngredientRow(false);
		}

		appShell.setCenter(addRecipe);
	}

	private void showEditRecipe(Recipe recipe) throws IOException {
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource("/fxml/recipes/NewRecipe.fxml"));

		Parent addRecipe = loader.load();

		// Register this class as the RecipeList's navigation handler
		NewRecipeController controller = loader.getController();
		controller.setNavigationHandler(this);
		controller.setRecipeToEdit(recipe);
		controller.setAppConfig(appConfig);

		// Inject dependencies
		controller.setRecipeService(recipeService);

		appShell.setCenter(addRecipe);
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
		// Delete recipe from the repository
		recipeService.removeRecipe(recipe);
		
		// Boot the user back to RecipeList.fxml
		showRecipes();
	}

	@Override
	public void setAppConfig(AppConfig appConfig) {
		this.appConfig = appConfig;
	}

	@Override
	public void onRecipesChanged() {
		shouldRefreshRecipes = true;
	}
	
	public ContextArea getCurrentContextArea() {
		return this.currentContextArea;
	}
	
}
