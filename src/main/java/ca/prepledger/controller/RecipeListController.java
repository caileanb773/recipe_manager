package ca.prepledger.controller;

import java.io.IOException;
import java.util.List;

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

	@FXML
	private Button gridViewBtn;

	@FXML
	private Button listViewBtn;

	@FXML
	private Button addRecipeBtn;

	@FXML
	private ScrollPane recipeScrollPane;

	@FXML
	private GridPane gridView;

	@FXML
	private VBox listView;
	
	@FXML
	private Label recipeCountLabel;
	
	private int recipeCount = 0;

	private RecipeDisplayType viewingMode;

	private NavigationHandler navigationHandler;

	// This class never instantiates this, it is only passed a ref. from AppShellCtrlr
	private RecipeService recipeService;
	
	private AppConfig appConfig;


	/////////////////////
	//
	// Methods
	//
	/////////////////////

	@FXML
	private void initialize() {
	
	}
	
	public void initializeViewingMode() {
		viewingMode = appConfig.getRecipeDisplayType();
		
		if (viewingMode == null) {
			// TODO log error
			System.out.println("Viewing mode null on initializeViewingMode()");
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
	}
	
	private void toggleListViewMode() {
		viewingMode = RecipeDisplayType.LIST;
		setListViewBtnActive();
		setGridViewBtnInactive();
		showListView();
	}

	@FXML
	public void onAddRecipeButtonClicked() {
		//addDummyRecipe();
		try {
			navigationHandler.navigateTo(ContextArea.ADD_RECIPE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	// XXX Temporary testing method, to be replaced with method that fetches recipes from repo
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

			// XXX specifically adding only to gridview here
			gridView.add(card, column, row);

		} catch (IOException e) {
			e.printStackTrace();
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

			// XXX specifically adding only to listview here
			listView.getChildren().add(card);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// XXX this can be optimized to only remove/refresh recipes for the displayed view mode
	public void refreshDisplayedRecipes() {
		removeAllDisplayedRecipes();
		
		// XXX fetch all recipes depending on online status, presumably
		List<Recipe> recipes = recipeService.getAllRecipes();

		if (recipes != null && recipes.size() >= 1) {
			for (Recipe recipe : recipes) {
				addRecipeGridCardToGridDisplay(recipe);
				addRecipeListCardToListDisplay(recipe);
				recipeCount++;
			}
		} else {
			System.out.println("recipe list null/empty");
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
