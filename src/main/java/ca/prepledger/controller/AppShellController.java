package ca.prepledger.controller;

import java.io.IOException;

import ca.prepledger.model.Recipe;
import ca.prepledger.navigation.ContextArea;
import ca.prepledger.navigation.NavigationHandler;
import ca.prepledger.service.RecipeService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

public class AppShellController implements NavigationHandler {

	@FXML
	private BorderPane appShell;

	private ContextArea currentContextArea = ContextArea.RECIPES;
	
	private RecipeService recipeService = new RecipeService();
	
	private RecipeListController recipeListController;


	@FXML
	private void initialize() {
		loadSidebar();
		loadDefaultContextArea();
	}

	private void loadSidebar() {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/fxml/sidebar/Sidebar.fxml")
					);

			Parent sidebar = loader.load();

			SidebarController controller = loader.getController();
			controller.setNavigationHandler(this);
			
			appShell.setLeft(sidebar);
			
			// Highlight the "Recipes" Sidebar nav button
			controller.setInitialScreenNavButtonSelected();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loadDefaultContextArea() {
		try {
			showRecipes();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void navigateTo(ContextArea contextArea) throws IOException {
		if (currentContextArea == contextArea) {
			return;
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
		}
	}
	
	@Override
	public void navigateToEditRecipe(Recipe recipe) throws IOException {
		currentContextArea = ContextArea.EDIT_RECIPE;
		showEditRecipe(recipe);
	}

	
	private void showRecipes() throws IOException {
	    FXMLLoader loader = new FXMLLoader(
	        getClass().getResource("/fxml/recipes/RecipeList.fxml")
	    );

	    Parent recipeList = loader.load();

	    // Register this class as the RecipeList's navigation handler
	    recipeListController = loader.getController();
	    recipeListController.setNavigationHandler(this);

		// XXX testing area
	    /*
	     * could do some pattern like 
	     * 
	     * boolean isRecipeServiceSet = false;
	     * 
	     * if (!isRecipeServiceSet)
	     * 		setRecipeService
	     * 
	     * */
		recipeListController.setRecipeService(recipeService);
		
	    appShell.setCenter(recipeList);
	    
	    recipeListController.refreshDisplayedRecipes();
	}

	private void showCosting() throws IOException {
		Parent costing = FXMLLoader.load(
				getClass().getResource("/fxml/costing/Costing.fxml")
				);

		appShell.setCenter(costing);
	}

	private void showSettings() throws IOException {
		Parent settings = FXMLLoader.load(
				getClass().getResource("/fxml/settings/Settings.fxml")
				);

		appShell.setCenter(settings);
	}
	
	private void showImportExport() throws IOException {
		Parent settings = FXMLLoader.load(
				getClass().getResource("/fxml/import-export/ImportExport.fxml")
				);

		appShell.setCenter(settings);
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
		controller.setNavigationHandler(this);
		
		// XXX testing; inject recipeservice into newrecipecontroller
		controller.setRecipeService(recipeService);
		
		controller.addNewIngredientRow(true);
		
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
		
		// XXX testing; inject recipeservice into newrecipecontroller
		controller.setRecipeService(recipeService);
		
		appShell.setCenter(addRecipe);
	}

}
