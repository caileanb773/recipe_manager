package ca.prepledger.controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class SidebarController {

	@FXML
	private Button recipesContextBtn;

	@FXML
	private Button impExpContextBtn;

	@FXML
	private Button settingsContextBtn;
	
	private AppShellController appShellController;


	/////////////////////
	//
	// Methods
	//
	/////////////////////
	
	public void setAppShellController(AppShellController appShellController) {
		this.appShellController = appShellController;
	}

	@FXML
	public void onRecipesContextBtnClick() {
		try {
			appShellController.showRecipes();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	public void onImpExpContextBtnClick() {
		try {
			appShellController.showImportExport();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	public void onSettingsContextBtnClick() {
		try {
			appShellController.showSettings();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



}
