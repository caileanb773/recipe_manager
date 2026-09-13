package ca.prepledger.controller;

import java.io.IOException;

import ca.prepledger.navigation.ContextArea;
import ca.prepledger.navigation.NavigationHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

public class AppShellController implements NavigationHandler {

	@FXML
	private BorderPane appShell;

	private ContextArea currentContextArea = ContextArea.RECIPES;


	public void initialize() {
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
			//controller.setAppShellController(this);
			controller.setNavigationHandler(this);
			
			appShell.setLeft(sidebar);

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
			//showImportExport();
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
		}
	}
	
	private void showRecipes() throws IOException {
	    FXMLLoader loader = new FXMLLoader(
	        getClass().getResource("/fxml/recipes/RecipeList.fxml")
	    );

	    Parent recipeList = loader.load();

	    // Register this class as the RecipeList's navigation handler
	    RecipeListController controller = loader.getController();
	    controller.setNavigationHandler(this);

	    appShell.setCenter(recipeList);
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
		Parent addRecipe = FXMLLoader.load(
				getClass().getResource("/fxml/recipes/NewRecipe.fxml"));

		appShell.setCenter(addRecipe);
	}

}
