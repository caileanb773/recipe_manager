package ca.prepledger.controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class AppShellController {

	@FXML
	private BorderPane appShell;

	@FXML
	private StackPane contextArea;

	private enum ContextArea {
		RECIPES, SETTINGS, IMPORT_EXPORT
	};

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
				controller.setAppShellController(this);

				appShell.setLeft(sidebar);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loadDefaultContextArea() {
		try {
			Parent recipeList = FXMLLoader.load(
					getClass().getResource("/fxml/recipes/RecipeList.fxml")
					);

			appShell.setCenter(recipeList);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void showRecipes() throws IOException {
		if (currentContextArea == ContextArea.RECIPES) {
			return;
		}
		
		currentContextArea = ContextArea.RECIPES;

		Parent view = FXMLLoader.load(
				getClass().getResource("/fxml/recipes/RecipeList.fxml")
				);

		contextArea.getChildren().setAll(view);
	}

	public void showImportExport() throws IOException {
		if (currentContextArea == ContextArea.IMPORT_EXPORT) {
			return;
		}
		
		currentContextArea = ContextArea.IMPORT_EXPORT;

		Parent view = FXMLLoader.load(
				getClass().getResource("/fxml/import-export/ImportExport.fxml")
				);

		contextArea.getChildren().setAll(view);
	}

	public void showSettings() throws IOException {
		if (currentContextArea == ContextArea.SETTINGS) {
			return;
		}
		
		currentContextArea = ContextArea.SETTINGS;

		Parent view = FXMLLoader.load(
				getClass().getResource("/fxml/settings/Settings.fxml")
				);

		contextArea.getChildren().setAll(view);
	}

}
