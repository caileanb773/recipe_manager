package ca.prepledger.util;

import java.util.Optional;

import ca.prepledger.model.Recipe;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class RecipeDialog {

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
		
		confirmDelete.getDialogPane().getStylesheets().add(
				getClass().getResource("/css/components/alert.css").toExternalForm()
				);
		confirmDelete.setTitle("Confirm Delete");
		Optional<ButtonType> response = confirmDelete.showAndWait();
		
		if (response.isPresent() && response.get() == ButtonType.YES) {
			return Choice.CONFIRM;
		} else {
			return Choice.CANCEL;
		}
	}

}
