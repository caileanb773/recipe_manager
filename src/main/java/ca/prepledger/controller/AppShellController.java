package ca.prepledger.controller;

import javafx.fxml.FXML;
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
		showRecipes();
	}
	
	@FXML
	public void onImpExpContextBtnClick() {
		showImportExport();
	}
	
	@FXML
	public void onSettingsContextBtnClick() {
		showSettings();
	}
	
	private void showRecipes() {
		if (currentContextArea == ContextArea.RECIPES) {
			return;
		}
		
		currentContextArea = ContextArea.RECIPES;
		
		System.out.println("Showing recipes");
	}
	
	private void showImportExport() {
		if (currentContextArea == ContextArea.IMPORT_EXPORT) {
			return;
		}
		
		currentContextArea = ContextArea.IMPORT_EXPORT;

		System.out.println("Showing Import/Export");
	}
	
	private void showSettings() {
		if (currentContextArea == ContextArea.SETTINGS) {
			return;
		}
		
		currentContextArea = ContextArea.SETTINGS;
		
		System.out.println("Showing Settings");
	}

}
