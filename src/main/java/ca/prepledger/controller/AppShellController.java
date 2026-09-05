package ca.prepledger.controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class AppShellController {

	@FXML
	private Button recipesContextBtn;

	@FXML
	private Button impExpContextBtn;

	@FXML
	private Button settingsContextBtn;

	@FXML
	private StackPane contextArea;

	private enum ContextArea {
		RECIPES, SETTINGS, IMPORT_EXPORT
	};

	private ContextArea currentContextArea = ContextArea.RECIPES;


	/////////////////////
	//
	// Methods
	//
	/////////////////////

	@FXML
	public void onRecipesContextBtnClick() {
		try {
			showRecipes();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	public void onImpExpContextBtnClick() {
		try {
			showImportExport();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	public void onSettingsContextBtnClick() {
		try {
			showSettings();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void showRecipes() throws IOException {
		if (currentContextArea == ContextArea.RECIPES) {
			return;
		}

		currentContextArea = ContextArea.RECIPES;

		Parent view = FXMLLoader.load(
				getClass().getResource("/fxml/recipes/RecipeList.fxml")
				);

		contextArea.getChildren().setAll(view);
	}

	private void showImportExport() throws IOException {
		if (currentContextArea == ContextArea.IMPORT_EXPORT) {
			return;
		}

		currentContextArea = ContextArea.IMPORT_EXPORT;

		Parent view = FXMLLoader.load(
				getClass().getResource("/fxml/import-export/ImportExport.fxml")
				);

		contextArea.getChildren().setAll(view);
	}

	private void showSettings() throws IOException {
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
