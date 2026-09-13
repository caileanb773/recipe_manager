package ca.prepledger.controller;

import java.io.IOException;
import java.util.List;

import ca.prepledger.navigation.ContextArea;
import ca.prepledger.navigation.Navigable;
import ca.prepledger.navigation.NavigationHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class SidebarController implements Navigable {

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
		
	//private AppShellController appShellController;
	
	private NavigationHandler navigationHandler;


	/////////////////////
	//
	// Methods
	//
	/////////////////////
	
//	public void setAppShellController(AppShellController appShellController) {
//		this.appShellController = appShellController;
//	}

	@FXML
	public void onRecipesContextBtnClick() {
		try {
			navigationHandler.navigateTo(ContextArea.RECIPES);
			setActiveButtonStyling("Recipes");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	public void onCostingContextBtnClick() {
		try {
			navigationHandler.navigateTo(ContextArea.COSTING);
			setActiveButtonStyling("Costing");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	public void onSettingsContextBtnClick() {
		try {
			navigationHandler.navigateTo(ContextArea.SETTINGS);
			setActiveButtonStyling("Settings");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@FXML
	public void onNotificationsContextBtnClick() {
		try {
			navigationHandler.navigateTo(ContextArea.NOTIFICATIONS);
			setActiveButtonStyling("Notifications");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@FXML
	public void onPrepListsContextBtnClick() {
		try {
			navigationHandler.navigateTo(ContextArea.PREP_LISTS);
			setActiveButtonStyling("Prep Lists");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setActiveButtonStyling(String buttonLabel) {
		List<Node> buttons = buttonVBox.getChildren();
		
		for (Node buttonNode : buttons) {
			Button b = (Button)buttonNode;
			
			// Give it the active styling CSS class
			if (b.getText().equalsIgnoreCase(buttonLabel)) {
				b.getStyleClass().add("active");
			} else {
				// Non-matching buttons are reset
				b.getStyleClass().remove("active");
			}
		}
	}

	@Override
	public void setNavigationHandler(NavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}

}
