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

	public static Choice confirmDeletion(Recipe recipe) {
		Alert alert = new Alert(
				AlertType.CONFIRMATION,
				"Are you sure you want to delete '" + recipe.getTitle() + "'?",
				ButtonType.YES,
				ButtonType.CANCEL
				);

		alert.getDialogPane().getStylesheets().addAll(
				PrepLedgerDialog.class.getResource("/css/theme.css").toExternalForm(),
				PrepLedgerDialog.class.getResource("/css/components/alert.css").toExternalForm());
		
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
	
	public static Choice confirmLogout() {
		Alert alert = new Alert(
				AlertType.CONFIRMATION,
				"Are you sure you want to log out?",
				ButtonType.YES,
				ButtonType.CANCEL
				);

		alert.getDialogPane().getStylesheets().addAll(
				PrepLedgerDialog.class.getResource("/css/theme.css").toExternalForm(),
				PrepLedgerDialog.class.getResource("/css/components/alert.css").toExternalForm());
		
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
	
	public static void showForgotPasswordDialog(String text) {
		Alert alert = new Alert(
				AlertType.WARNING,
				text
				);

		alert.getDialogPane().getStylesheets().addAll(
		        PrepLedgerDialog.class.getResource("/css/theme.css").toExternalForm(),
		        PrepLedgerDialog.class.getResource("/css/components/alert.css").toExternalForm());
		
		alert.setTitle("Forgot Password");
		alert.showAndWait();
	}
	
	public static void showInvalidRecipeDialog(String text) {
		Alert alert = new Alert(
				AlertType.WARNING,
				text
				);

		alert.getDialogPane().getStylesheets().addAll(
				PrepLedgerDialog.class.getResource("/css/theme.css").toExternalForm(),
				PrepLedgerDialog.class.getResource("/css/components/alert.css").toExternalForm());
		
		alert.setTitle("Error");
		alert.setHeaderText("Invalid recipe.");
		alert.showAndWait();
	}
	
	public static void showRecipeBackendActionFailedDialog(
			String exceptionMsg,
			String action) {
		
		Alert alert = new Alert(
				AlertType.ERROR,
				"Please try again.");
		
		alert.getDialogPane().getStylesheets().addAll(
				PrepLedgerDialog.class.getResource("/css/theme.css").toExternalForm(),
				PrepLedgerDialog.class.getResource("/css/components/alert.css").toExternalForm());
		
		alert.setContentText("Could not " + action + " recipe for the following reason: \n\n"
				+ exceptionMsg);
		alert.setTitle("Error");
		alert.showAndWait();
	}
	
	public static void showLoginError(String reason) {
		Alert alert = new Alert(
				AlertType.WARNING,
				reason);
		
		alert.getDialogPane().getStylesheets().addAll(
				PrepLedgerDialog.class.getResource("/css/theme.css").toExternalForm(),
				PrepLedgerDialog.class.getResource("/css/components/alert.css").toExternalForm());
		
		alert.setHeaderText("Could not log in");
		alert.setTitle("Warning");
		alert.showAndWait();
	}
	
	public static boolean showVerificationRequired() {
	    Alert alert = new Alert(
	            AlertType.WARNING,
	            "Please check your email for a verification link."
	    );

		alert.getDialogPane().getStylesheets().addAll(
				PrepLedgerDialog.class.getResource("/css/theme.css").toExternalForm(),
				PrepLedgerDialog.class.getResource("/css/components/alert.css").toExternalForm());

	    alert.setHeaderText("Email verification required");
	    alert.setTitle("Account verification");

	    ButtonType resendButton = new ButtonType("Resend email");
	    ButtonType cancelButton = ButtonType.CANCEL;

	    alert.getButtonTypes().setAll(resendButton, cancelButton);

	    return alert.showAndWait().orElse(cancelButton) == resendButton;
	}
	
	public static void showRegisterError(String reason) {
		Alert alert = new Alert(
				AlertType.WARNING,
				reason);
		
		alert.getDialogPane().getStylesheets().addAll(
				PrepLedgerDialog.class.getResource("/css/theme.css").toExternalForm(),
				PrepLedgerDialog.class.getResource("/css/components/alert.css").toExternalForm());
		
		alert.setHeaderText("Could not register");
		alert.setTitle("Warning");
		alert.showAndWait();
	}
	
	public static void showGenericInfo(String info) {
		Alert alert = new Alert(
				AlertType.INFORMATION,
				info);
		
		alert.getDialogPane().getStylesheets().addAll(
				PrepLedgerDialog.class.getResource("/css/theme.css").toExternalForm(),
				PrepLedgerDialog.class.getResource("/css/components/alert.css").toExternalForm());
		
		alert.setTitle("Attention");
		alert.showAndWait();
	}
	
	public static void showRegisterSuccess(String information) {
		Alert alert = new Alert(
				AlertType.INFORMATION,
				information);
		
		alert.getDialogPane().getStylesheets().addAll(
				PrepLedgerDialog.class.getResource("/css/theme.css").toExternalForm(),
				PrepLedgerDialog.class.getResource("/css/components/alert.css").toExternalForm());
		
		alert.setHeaderText("User registered");
		alert.setTitle("Success");
		alert.showAndWait();
	}
	
	public static void showRecipeScalingHelpDialog() {
		Alert help = new Alert(
				Alert.AlertType.INFORMATION,
				"Adjust the quantities in your recipe without changing the original.\n\n"
				+ "• Scale factor: Set the amount to increase or decrease the recipe. "
				+ "The default is 0.5x.\n"
				+ "• Increase (+): Add the scale factor to the current batch size.\n"
				+ "• Decrease (-): Subtract the scale factor from the current batch "
				+ "size.\n"
				+ "• Batch Size: Displays the recipe's current multiplier (e.g., "
				+ "2x represents twice the original recipe).\n"
				+ "• Reset: Return the scale factor and batch size to their defaults."
				);
		
		help.getDialogPane().setPrefWidth(500);
		help.setTitle("Recipe Scaling Help");
		help.setHeaderText("How Recipe Scaling Works");
		help.getDialogPane().getStylesheets().addAll(
				PrepLedgerDialog.class.getResource("/css/theme.css").toExternalForm(),
				PrepLedgerDialog.class.getResource("/css/components/alert.css").toExternalForm()
				);
		help.getDialogPane().setPrefWidth(450);
		help.showAndWait();
	}
	
	/**
	 * Alerts the user that a JSON-specific error occurred during import.
	 * 
	 * This is a catch-all dialog that tells the user "there is a problem with the
	 * JSON file you tried to import" and is non-specific; i.e., it does not identify
	 * which recipe caused the import to fail; this is because the specific JSON
	 * selector for the recipe title could be corrupt.
	 */
	public static void showCorruptJSONImportErrorDialog() {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Import Error");
		alert.setHeaderText("Could not complete import operation.");
		alert.setContentText("Could not parse recipes from JSON file; file is "
				+ "corrupt. Please only import JSON files that were exported using "
				+ "PrepLedger and do not manually edit JSON files.");
		alert.getDialogPane().getStylesheets().addAll(
				PrepLedgerDialog.class.getResource("/css/theme.css").toExternalForm(),
				PrepLedgerDialog.class.getResource("/css/components/alert.css").toExternalForm());
		
		alert.showAndWait();
	}
	
	public static void showImportOperationError(String contentString) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Import Error");
		alert.setHeaderText("Could not complete import operation.");
		alert.setContentText(contentString);
		alert.getDialogPane().getStylesheets().addAll(
				PrepLedgerDialog.class.getResource("/css/theme.css").toExternalForm(),
				PrepLedgerDialog.class.getResource("/css/components/alert.css").toExternalForm());
		
		alert.showAndWait();
	}

	public static void displayPostImportDialog(int numImported) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Import Complete");
		alert.setHeaderText("Recipes imported successfully");
		alert.setContentText(numImported + " recipes were imported.");
		alert.getDialogPane().getStylesheets().addAll(
				PrepLedgerDialog.class.getResource("/css/theme.css").toExternalForm(),
				PrepLedgerDialog.class.getResource("/css/components/alert.css").toExternalForm());
		
		alert.showAndWait();
	}
	
	public static void showExportOperationError() {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Export Error");
		alert.setHeaderText("Could not complete export operation.");
		alert.setContentText("No recipes to export!");
		alert.getDialogPane().getStylesheets().addAll(
				PrepLedgerDialog.class.getResource("/css/theme.css").toExternalForm(),
				PrepLedgerDialog.class.getResource("/css/components/alert.css").toExternalForm());
		
		alert.showAndWait();
	}

}
