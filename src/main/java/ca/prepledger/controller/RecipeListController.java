package ca.prepledger.controller;

import java.io.IOException;
import java.util.List;

import ca.prepledger.model.Recipe;
import ca.prepledger.navigation.ContextArea;
import ca.prepledger.navigation.Navigable;
import ca.prepledger.navigation.NavigationHandler;
import ca.prepledger.service.RecipeService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;

public class RecipeListController implements Navigable {

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
	private TilePane listView;

	private RecipeViewMode viewingMode;

	private int recipeCount = 0;

	private enum RecipeViewMode {
		GRID,
		LIST
	}

	private NavigationHandler navigationHandler;

	// This class never instantiates this, it is only passed a ref. from AppShellCtrlr
	private RecipeService recipeService;


	/////////////////////
	//
	// Methods
	//
	/////////////////////

	@FXML
	private void initialize() {
		viewingMode = RecipeViewMode.GRID;
		showGridView();
		gridViewBtn.getStyleClass().add("active");
		listViewBtn.getStyleClass().remove("active");
	}

	@FXML
	public void onGridViewBtnClicked() {
		if (viewingMode == RecipeViewMode.GRID) {
			return;
		}

		viewingMode = RecipeViewMode.GRID;
		gridViewBtn.getStyleClass().add("active");
		listViewBtn.getStyleClass().remove("active");
		showGridView();
	}

	@FXML
	public void onListViewBtnClicked() {
		if (viewingMode == RecipeViewMode.LIST) {
			return;
		}

		viewingMode = RecipeViewMode.LIST;
		listViewBtn.getStyleClass().add("active");
		gridViewBtn.getStyleClass().remove("active");
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
	private void addDummyRecipe(Recipe recipe) {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/fxml/recipes/RecipeCard.fxml")
					);

			Node card = loader.load();

			RecipeCardController controller = loader.getController();
			controller.setRecipe(recipe);

			// Dependency injection for Recipe Service
			controller.setRecipeService(recipeService);
			controller.setOnRecipeDeleted(this::refreshDisplayedRecipes);
			controller.setNavigationHandler(navigationHandler);
			
			int column = recipeCount % 3;
			int row = recipeCount / 3;

			// XXX specifically adding only to gridview here
			gridView.add(card, column, row);

			recipeCount++;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// XXX
	public void refreshDisplayedRecipes() {
		removeAllDisplayedRecipes();
		
		// XXX fetch all recipes depending on online status, presumably
		List<Recipe> recipes = recipeService.getAllRecipes();

		if (recipes != null && recipes.size() >= 1) {
			for (Recipe recipe : recipes) {
				addDummyRecipe(recipe);
			}
		} else {
			System.out.println("recipe list null/empty");
		}
	}

	private void removeAllDisplayedRecipes() {
		gridView.getChildren().clear();
		listView.getChildren().clear();
	}

	public void setRecipeService(RecipeService recipeService) {
		this.recipeService = recipeService;
	}

	public void setNavigationHandler(NavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}
}
