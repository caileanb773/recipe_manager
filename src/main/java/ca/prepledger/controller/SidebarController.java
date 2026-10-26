package ca.prepledger.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.prepledger.navigation.ContextArea;
import ca.prepledger.navigation.Navigable;
import ca.prepledger.navigation.NavigationHandler;
import ca.prepledger.session.UserSession;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class SidebarController implements Navigable {

	@FXML private Button recipesContextBtn;
	@FXML private Button costingContextBtn;
	@FXML private Button settingsContextBtn;
	@FXML private Button notificationsContextBtn;	
	@FXML private Button impExpContextBtn;	
	@FXML private VBox buttonVBox;
	@FXML private Label profileName;
	@FXML private Label connectionStatus;
	@FXML private ImageView connectionStatusImage;

	private NavigationHandler navigationHandler;
	private List<Node> buttons;
	private static final Logger logger = LoggerFactory.getLogger(SidebarController.class);


	/////////////////////
	//
	// Methods
	//
	/////////////////////

	public void cacheButtons() {
		buttons = buttonVBox.getChildren();
	}

	@FXML
	public void onRecipesContextBtnClick() {
		try {
			navigationHandler.navigateTo(ContextArea.RECIPES);
		} catch (IOException e) {
			logger.error("onRecipesContextBtnClick(): IOException encountered: {}", e);
		}
	}

	@FXML
	public void onCostingContextBtnClick() {
		try {
			navigationHandler.navigateTo(ContextArea.COSTING);
		} catch (IOException e) {
			logger.error("onCostingContextBtnClick(): IOException encountered: {}", e);
		}
	}

	@FXML
	public void onSettingsContextBtnClick() {
		try {
			navigationHandler.navigateTo(ContextArea.SETTINGS);
		} catch (IOException e) {
			logger.error("onSettingsContextBtnClick(): IOException encountered: {}", e);
		}
	}

	@FXML
	public void onNotificationsContextBtnClick() {
		try {
			navigationHandler.navigateTo(ContextArea.NOTIFICATIONS);
		} catch (IOException e) {
			logger.error("onNotificationsContextBtnClick(): IOException encountered: {}", e);
		}
	}

	@FXML
	public void onPrepListsContextBtnClick() {
		try {
			navigationHandler.navigateTo(ContextArea.PREP_LISTS);
		} catch (IOException e) {
			logger.error("onPrepListsContextBtnClick(): IOException encountered: {}", e);
		}
	}

	@FXML
	public void onImportExportContextBtnClick() {
		try {
			navigationHandler.navigateTo(ContextArea.IMPORT_EXPORT);
		} catch (IOException e) {
			logger.error("onImportExportContextBtnClick(): IOException encountered: {}", e);
		}
	}

	public void setInitialScreenNavButtonSelected() {
		setActiveButtonStyling("Recipes");
	}

	public void setActiveButtonStyling(String buttonLabel) {
		
		/* A little hacky. Could optimize this whole operation with:
		 * Map<ContextArea, Button> and just use Map.get() (which is a constant-
		 * time operation on average) */
		switch (buttonLabel) {
		case "import_export":
			buttonLabel = "Import/Export";
			break;
		case "prep_lists":
			buttonLabel = "Prep Lists";
			break;
		case "edit_recipe":
		case "view_recipe":
			buttonLabel = "Recipes";
			break;
		default:
			break;
		}

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
	
	public void setUserProfileDetails(UserSession currentSession) {
		String email = currentSession.getEmail();
		profileName.setText(email);
		profileName.setTooltip(new Tooltip(email));
	}

	@Override
	public void setNavigationHandler(NavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}

}
