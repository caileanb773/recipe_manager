package ca.prepledger.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.prepledger.config.AppConfig;
import ca.prepledger.config.Configurable;
import ca.prepledger.config.RecipeDisplayType;
import ca.prepledger.model.Recipe;
import ca.prepledger.navigation.ContextArea;
import ca.prepledger.navigation.Navigable;
import ca.prepledger.navigation.NavigationHandler;
import ca.prepledger.service.RecipeService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class RecipeListController implements Navigable, Configurable {

	// FXML Elements
	@FXML private Button gridViewBtn;
	@FXML private Button listViewBtn;
	@FXML private Button addRecipeBtn;
	@FXML private ScrollPane recipeScrollPane;
	@FXML private GridPane gridView;
	@FXML private VBox listView;
	@FXML private Label recipeCountLabel;
	
	// Injectables
	private NavigationHandler navigationHandler;
	private RecipeService recipeService;
	private AppConfig appConfig;
		
	// Misc
	private List<Recipe> cachedRecipes;
	private RecipeDisplayType viewingMode;
	private int recipeCount = 0;
	private static final Logger logger = LoggerFactory.getLogger(RecipeListController.class);


	@FXML
	private void initialize() {
		cachedRecipes = new ArrayList<>();
	}
	
	public void initializeViewingMode() {
		viewingMode = appConfig.getRecipeDisplayType();
		
		if (viewingMode == null) {
			logger.error("initializeViewingMode(): ViewingMode is null, defaulting to GRID.");
			viewingMode = RecipeDisplayType.GRID;
		}
		
		if (viewingMode == RecipeDisplayType.GRID) {
			toggleGridViewMode();
		} else {
			toggleListViewMode();
		}
	}

	@FXML
	public void onGridViewBtnClicked() {
		if (viewingMode == RecipeDisplayType.GRID) {
			return;
		}

		toggleGridViewMode();
	}
	
	@FXML
	public void onListViewBtnClicked() {
		if (viewingMode == RecipeDisplayType.LIST) {
			return;
		}

		toggleListViewMode();
	}
	
	private void setGridViewBtnActive() {
		gridViewBtn.getStyleClass().add("active");
	}
	
	private void setGridViewBtnInactive() {
		gridViewBtn.getStyleClass().remove("active");
	}
	
	private void setListViewBtnActive() {
		listViewBtn.getStyleClass().add("active");
	}
	
	private void setListViewBtnInactive() {
		listViewBtn.getStyleClass().remove("active");
	}
	
	private void toggleGridViewMode() {
		viewingMode = RecipeDisplayType.GRID;
		setGridViewBtnActive();
		setListViewBtnInactive();
		showGridView();
		renderCachedRecipes();
	}
	
	private void toggleListViewMode() {
		viewingMode = RecipeDisplayType.LIST;
		setListViewBtnActive();
		setGridViewBtnInactive();
		showListView();
		renderCachedRecipes();
	}

	@FXML
	public void onAddRecipeButtonClicked() {
		try {
			navigationHandler.navigateTo(ContextArea.ADD_RECIPE);
		} catch (IOException e) {
			logger.error("onRecipeCardClicked(): IOException encountered: {}", e);
		}
	}

	private void showGridView() {
		gridView.setVisible(true);
		gridView.setManaged(true);
		listView.setVisible(false);
		listView.setManaged(false);
	}

	private void showListView() {
		listView.setVisible(true);
		listView.setManaged(true);
		gridView.setVisible(false);
		gridView.setManaged(false);
	}

	private void addRecipeGridCardToGridDisplay(Recipe recipe) {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/fxml/recipes/RecipeGridCard.fxml")
					);

			Node card = loader.load();

			RecipeGridCardController controller = loader.getController();
			controller.setRecipe(recipe);

			// Dependency injection for Recipe Service
			controller.setRecipeService(recipeService);
			controller.setOnRecipeDeleted(this::refreshDisplayedRecipes);
			controller.setNavigationHandler(navigationHandler);
			
			int column = recipeCount % 3;
			int row = recipeCount / 3;

			gridView.add(card, column, row);

		} catch (IOException e) {
			logger.error("onRecipeCardClicked(): IOException encountered: {}", e);
		}
	}
	
	private void addRecipeListCardToListDisplay(Recipe recipe) {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/fxml/recipes/RecipeListCard.fxml")
					);

			Node card = loader.load();

			RecipeListCardController controller = loader.getController();
			controller.setRecipe(recipe);

			// Dependency injection for Recipe Service
			controller.setRecipeService(recipeService);
			controller.setOnRecipeDeleted(this::refreshDisplayedRecipes);
			controller.setNavigationHandler(navigationHandler);

			listView.getChildren().add(card);

		} catch (IOException e) {
			logger.error("onRecipeCardClicked(): IOException encountered: {}", e);
		}
	}

	public void refreshDisplayedRecipes() {		
		// XXX fetch all recipes depending on online status, presumably
		cachedRecipes = recipeService.getAllRecipes();
		renderCachedRecipes();
	}
	
	private void renderCachedRecipes() {
		removeAllDisplayedRecipes();
		
		if (cachedRecipes != null) {
			for (Recipe recipe : cachedRecipes) {
				
				if (viewingMode == RecipeDisplayType.GRID) {
					addRecipeGridCardToGridDisplay(recipe);						
				} else {
					addRecipeListCardToListDisplay(recipe);						
				}
				
				recipeCount++;
			}		
		} else {
			logger.info("refreshDisplayedRecipes(): Recipe list is null.");
		}

		recipeCountLabel.setText(recipeCount + " recipes");
	}

	private void removeAllDisplayedRecipes() {
		recipeCount = 0;
		gridView.getChildren().clear();
		listView.getChildren().clear();
	}

	public void setRecipeService(RecipeService recipeService) {
		this.recipeService = recipeService;
	}

	public void setNavigationHandler(NavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}

	@Override
	public void setAppConfig(AppConfig appConfig) {
		this.appConfig = appConfig;
	}
}
