package ca.prepledger.util;

import java.util.Optional;

import ca.prepledger.model.Recipe;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

public class PrepLedgerDialog {

	public static enum Choice {
		CONFIRM, CANCEL
	}

	public Choice confirmDeletion(Recipe recipe) {
		Alert confirmDelete = new Alert(
				AlertType.CONFIRMATION,
				"Are you sure you want to delete '" + recipe.getTitle() + "'?",
				ButtonType.YES,
				ButtonType.CANCEL
				);

		confirmDelete.getDialogPane().getStylesheets().addAll(
				getClass().getResource("/css/components/alert.css").toExternalForm()
				);
		
		Button yesBtn = (Button)confirmDelete.getDialogPane()
				.lookupButton(ButtonType.YES);
		yesBtn.getStyleClass().remove("default");
		yesBtn.getStyleClass().add("danger-button");
		
		Button cancelBtn = (Button)confirmDelete.getDialogPane()
				.lookupButton(ButtonType.CANCEL);
		cancelBtn.getStyleClass().remove("default");
		cancelBtn.getStyleClass().add("default-button");
		
		confirmDelete.setTitle("Confirm Delete");
		Optional<ButtonType> response = confirmDelete.showAndWait();

		if (response.isPresent() && response.get() == ButtonType.YES) {
			return Choice.CONFIRM;
		} else {
			return Choice.CANCEL;
		}
	}
	
	public Choice confirmLogout() {
		Alert confirmLogout = new Alert(
				AlertType.CONFIRMATION,
				"Are you sure you want to log out?",
				ButtonType.YES,
				ButtonType.CANCEL
				);

		confirmLogout.getDialogPane().getStylesheets().addAll(
				getClass().getResource("/css/components/alert.css").toExternalForm()
				);
		
		Button yesBtn = (Button)confirmLogout.getDialogPane()
				.lookupButton(ButtonType.YES);
		yesBtn.getStyleClass().remove("default");
		yesBtn.getStyleClass().add("danger-button");
		
		Button cancelBtn = (Button)confirmLogout.getDialogPane()
				.lookupButton(ButtonType.CANCEL);
		cancelBtn.getStyleClass().remove("default");
		cancelBtn.getStyleClass().add("default-button");
		
		confirmLogout.setTitle("Confirm Logout");
		Optional<ButtonType> response = confirmLogout.showAndWait();

		if (response.isPresent() && response.get() == ButtonType.YES) {
			return Choice.CONFIRM;
		} else {
			return Choice.CANCEL;
		}
	}

}
