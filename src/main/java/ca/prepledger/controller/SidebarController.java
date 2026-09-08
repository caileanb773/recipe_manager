package ca.prepledger.controller;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SidebarController {

	@FXML
	private Button recipesContextBtn;

	@FXML
	private Button costingContextBtn;

	@FXML
	private Button settingsContextBtn;
	
	@FXML
	private Button notificationsContextBtn;
	
	@FXML
	private VBox buttonVBox;
	
	private List<Button> uiButtons;
	
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
			setActiveButtonStyling("Recipes");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	public void onCostingContextBtnClick() {
		try {
			appShellController.showCosting();
			setActiveButtonStyling("Costing");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	public void onSettingsContextBtnClick() {
		try {
			appShellController.showSettings();
			setActiveButtonStyling("Settings");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@FXML
	public void onNotificationsContextBtnClick() {
		try {
			appShellController.showNotifications();
			setActiveButtonStyling("Notifications");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setActiveButtonStyling(String buttonLabel) {
		List<Node> buttonHBoxes = buttonVBox.getChildren();
		
		for (Node hBoxNode : buttonHBoxes) {
			HBox hBox = (HBox)hBoxNode;
			
			List<Node> hBoxElements = hBox.getChildren();
			
			for (Node n : hBoxElements) {
				if (n.getTypeSelector().equalsIgnoreCase("Button")) {
					Button b = (Button)n;
					
					// Give it the active styling CSS class
					if (b.getText().equalsIgnoreCase(buttonLabel)) {
						b.getStyleClass().add("active");
					} else {
						// Non-matching buttons are reset
						b.getStyleClass().remove("active");
					}
				}
			}
		}
	}

}
