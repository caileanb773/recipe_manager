package ca.prepledger.controller;

import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.prepledger.model.Recipe;
import ca.prepledger.navigation.ContextArea;
import ca.prepledger.navigation.Navigable;
import ca.prepledger.navigation.NavigationHandler;
import ca.prepledger.service.RecipeAPIService;
import ca.prepledger.service.RecipeService;
import ca.prepledger.session.UserSession;
import ca.prepledger.util.PrepLedgerDialog;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

/**
 * Controller class which handles input from the view and delegates actions.
 * 
 * @author Cailean Bernard
 * @since v0.1.0
 */
public class RecipeGridCardController implements Navigable {

	@FXML private ImageView recipeImage;
	@FXML private Label recipeName;
	@FXML private FlowPane tagsPane;
	@FXML private Button recipeOptionsButton;

	private Recipe recipe;
	private RecipeService recipeService;
	private Runnable onRecipeDeleted;
	private NavigationHandler navigationHandler;
	private UserSession currentSession;
	private RecipeAPIService recipeApiService;
	private static final Logger logger = LoggerFactory.getLogger(RecipeGridCardController.class);


	public void setRecipe(Recipe recipe) {
		this.recipe = recipe;

		// Set the fields of the card
		recipeName.setText(recipe.getTitle());
		ObservableList<Node> tagsList = tagsPane.getChildren();
		recipe.getTags().stream()
		.limit(3)
		.forEach(tag -> {
			Label tagLabel = new Label(tag);
			tagLabel.setMaxWidth(80);
			tagLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
			tagLabel.setTooltip(new Tooltip(tag));
			tagLabel.getStyleClass().add("sub-header");
			tagsList.add(tagLabel);
		});

		// Set only default image for now until custom images are supported
		Image tempImage = getDefaultImage();
		
		if (tempImage != null) {
			recipeImage.setImage(tempImage);
		} else {			
			logger.error("setRecipe(): Recipe image is null.");
		}
	}

	@FXML
	private void onRecipeOptionsButtonClicked() {
		openContextMenu();
	}

	@FXML
	private void onRecipeCardClicked() {		
		try {
			navigationHandler.navigateTo(ContextArea.VIEW_RECIPE, recipe);
		} catch (IOException e) {
			logger.error("onRecipeCardClicked(): IOException encountered: {}", e);
		}
	}

	private void openContextMenu() {
		ContextMenu menu = new ContextMenu();

		MenuItem editItem = new MenuItem("Edit");
		MenuItem deleteItem = new MenuItem("Delete");

		editItem.setOnAction(e -> {
			attemptEditRecipe();
		});

		deleteItem.setOnAction(e -> {
			attemptDeleteRecipe();
		});

		menu.getItems().addAll(editItem, deleteItem);
		menu.show(recipeOptionsButton, Side.BOTTOM, 0, 0);
	}

	public void attemptEditRecipe() {
		try {
			navigationHandler.navigateTo(ContextArea.EDIT_RECIPE, recipe);
		} catch (IOException e) {
			logger.error("attemptEditRecipe(): IOException encountered: {}", e);
		}
	}

	public void attemptDeleteRecipe() {
		PrepLedgerDialog.Choice choice = PrepLedgerDialog.confirmDeletion(recipe);

		if (choice == PrepLedgerDialog.Choice.CANCEL) {
			logger.info("attemptDeleteRecipe(): Cancelling delete of recipe: "
					+ recipe.getTitle());
			return;
		}
		
		// fail early if recipe has no ID to pass to backend
		if (recipe.getId() == null) {
		    logger.warn("Cannot delete recipe: missing backend ID.");
		    PrepLedgerDialog.showRecipeBackendActionFailedDialog(
		            "Missing backend ID.",
		            "delete");
		    return;
		}

		Task<Void> task = new Task<>() {
		    @Override
		    protected Void call() throws Exception {
		        recipeApiService.deleteRecipe(
		                currentSession, recipe.getId());
		        return null;
		    }
		};

		// only delete from local memory if this thread succeeded
		task.setOnSucceeded(event -> {
		    recipeService.removeRecipe(recipe);
		    onRecipeDeleted.run();
		});

		task.setOnFailed(event -> {
		    logger.error("Failed to delete recipe from backend.",
		            task.getException());
		    PrepLedgerDialog.showRecipeBackendActionFailedDialog(
		            task.getException().getMessage(),
		            "delete");
		});

		Thread thread = new Thread(task, "recipe-delete");
		thread.setDaemon(true);
		thread.start();
	}

	private Image getDefaultImage() {
		URL url = getClass().getResource("/img/temp/missing-image.png");
		
		if (url == null) {
			logger.error("getDefaultImage(): couldn't locate 'missing-image.png'.");
			return null;
		}
		
		Image image = new Image(url.toExternalForm());
		return image;
	}

	public Recipe getRecipe() {
		return this.recipe;
	}

	public void setRecipeService(RecipeService recipeService) {
		this.recipeService = recipeService;
	}

	public void setOnRecipeDeleted(Runnable onRecipeDeleted) {
		this.onRecipeDeleted = onRecipeDeleted;
	}

	@Override
	public void setNavigationHandler(NavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}
	
	public void setSession(UserSession currentSession) {
		this.currentSession = currentSession;
	}

	public void setRecipeApiService(RecipeAPIService recipeApiService) {
		this.recipeApiService = recipeApiService;
	}

}