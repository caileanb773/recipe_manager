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
		Alert alert = new Alert(
				AlertType.CONFIRMATION,
				"Are you sure you want to delete '" + recipe.getTitle() + "'?",
				ButtonType.YES,
				ButtonType.CANCEL
				);

		alert.getDialogPane().getStylesheets().addAll(
				getClass().getResource("/css/components/alert.css").toExternalForm()
				);
		
		Button yesBtn = (Button)alert.getDialogPane()
				.lookupButton(ButtonType.YES);
		yesBtn.getStyleClass().remove("default");
		yesBtn.getStyleClass().add("danger-button");
		
		Button cancelBtn = (Button)alert.getDialogPane()
				.lookupButton(ButtonType.CANCEL);
		cancelBtn.getStyleClass().remove("default");
		cancelBtn.getStyleClass().add("default-button");
		
		alert.setTitle("Confirm Delete");
		Optional<ButtonType> response = alert.showAndWait();

		if (response.isPresent() && response.get() == ButtonType.YES) {
			return Choice.CONFIRM;
		} else {
			return Choice.CANCEL;
		}
	}
	
	public Choice confirmLogout() {
		Alert alert = new Alert(
				AlertType.CONFIRMATION,
				"Are you sure you want to log out?",
				ButtonType.YES,
				ButtonType.CANCEL
				);

		alert.getDialogPane().getStylesheets().addAll(
				getClass().getResource("/css/components/alert.css").toExternalForm()
				);
		
		Button yesBtn = (Button)alert.getDialogPane()
				.lookupButton(ButtonType.YES);
		yesBtn.getStyleClass().remove("default");
		yesBtn.getStyleClass().add("danger-button");
		
		Button cancelBtn = (Button)alert.getDialogPane()
				.lookupButton(ButtonType.CANCEL);
		cancelBtn.getStyleClass().remove("default");
		cancelBtn.getStyleClass().add("default-button");
		
		alert.setTitle("Confirm Logout");
		Optional<ButtonType> response = alert.showAndWait();

		if (response.isPresent() && response.get() == ButtonType.YES) {
			return Choice.CONFIRM;
		} else {
			return Choice.CANCEL;
		}
	}
	
	public void showForgotPasswordDialog(String text) {
		Alert alert = new Alert(
				AlertType.WARNING,
				text
				);

		alert.getDialogPane().getStylesheets().addAll(
				getClass().getResource("/css/components/alert.css").toExternalForm()
				);
		
		alert.setTitle("Forgot Password");
		alert.showAndWait();
	}
	
	public void showInvalidRecipeDialog(String text) {
		Alert alert = new Alert(
				AlertType.WARNING,
				text
				);

		alert.getDialogPane().getStylesheets().addAll(
				getClass().getResource("/css/components/alert.css").toExternalForm()
				);
		
		alert.setTitle("Error");
		alert.setHeaderText("Invalid recipe.");
		alert.showAndWait();
	}
	
	public void showRecipeBackendActionFailedDialog(
			String exceptionMsg,
			String action) {
		
		Alert alert = new Alert(
				AlertType.ERROR,
				"Please try again.");
		
		alert.getDialogPane().getStylesheets().addAll(
				getClass().getResource("/css/components/alert.css").toExternalForm()
				);
		
		alert.setContentText("Could not " + action + " recipe for the following reason: \n\n"
				+ exceptionMsg);
		alert.setTitle("Error");
		alert.showAndWait();
	}

}
