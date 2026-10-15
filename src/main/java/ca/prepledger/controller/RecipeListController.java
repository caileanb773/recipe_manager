package ca.prepledger.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
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
	@FXML private TextField recipeSearchField;
	@FXML private Button filterResetBtn;
	@FXML private ComboBox<String> recipeSort;

	// Injectables
	private NavigationHandler navigationHandler;
	private RecipeService recipeService;
	private AppConfig appConfig;

	// Flags
	private boolean gridCardsInitialized = false;
	private boolean listCardsInitialized = false;

	// Misc
	private List<Recipe> cachedRecipes;
	private Map<Recipe, Node> cachedGridCards;
	private Map<Recipe, Node> cachedListCards;
	private RecipeDisplayType viewingMode;
	private int recipeCount = 0;
	private static final Logger logger = LoggerFactory.getLogger(RecipeListController.class);


	@FXML
	private void initialize() {
		cachedRecipes = new ArrayList<>();
		cachedGridCards = new HashMap<>();
		cachedListCards = new HashMap<>();
		
		recipeSort.getItems().addAll("Name: A-Z", "Name: Z-A");
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
			logger.error("onAddRecipeButtonClicked(): IOException encountered: {}", e);
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

			int index = cachedGridCards.size();

			int column = index % 3;
			int row = index / 3;

			cachedGridCards.put(recipe, card);
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

			cachedListCards.put(recipe, card);
			listView.getChildren().add(card);

		} catch (IOException e) {
			logger.error("onRecipeCardClicked(): IOException encountered: {}", e);
		}
	}

	public void refreshDisplayedRecipes() {		
		// XXX fetch latest recipe data (dependent on online status)
		cachedRecipes = recipeService.getAllRecipes();

		// invalidate the card caches and reset flags
		cachedListCards.clear();
		cachedGridCards.clear();
		gridCardsInitialized = false;
		listCardsInitialized = false;

		// clear existing nodes from list and grid containers
		removeAllDisplayedRecipes();

		// re-render recipe cards from cached list of recipes
		renderCachedRecipes();
	}

	private void renderCachedRecipes() {
		if (cachedRecipes != null) {
			recipeCount = cachedRecipes.size();

			if (viewingMode == RecipeDisplayType.GRID) {
				if (!gridCardsInitialized) {
					for (Recipe recipe : cachedRecipes) {
						addRecipeGridCardToGridDisplay(recipe);												
					}

					gridCardsInitialized = true;
				}				
			} else {
				if (!listCardsInitialized) {
					for (Recipe recipe : cachedRecipes) {			
						addRecipeListCardToListDisplay(recipe);							
					}		

					listCardsInitialized = true;					
				}
			}

			recipeCountLabel.setText(recipeCount + " recipes");
		} else {
			logger.info("refreshDisplayedRecipes(): Recipe list is null.");
		}
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

	public void initializeSearchField() {
		recipeSearchField.textProperty().addListener(
				(observable, pref, last) -> filterRecipes(recipeSearchField.getText())
				);
	}

	private void filterRecipes(String text) {
		String query = text.toLowerCase().trim();
		
		if (!query.isEmpty()) {
			for (Recipe recipe : cachedRecipes) {
				boolean matches = recipe.getTitle().toLowerCase().contains(query)
						|| recipe.getTags().stream().anyMatch(
								tag -> tag.toLowerCase().contains(query)
								);
				
				Node gridCard = cachedGridCards.get(recipe);
				Node listCard = cachedListCards.get(recipe);
				
				if (gridCard != null) {
					gridCard.setVisible(matches);
					gridCard.setManaged(matches);
				}
				
				if (listCard != null) {
					listCard.setVisible(matches);
					listCard.setManaged(matches);
				}			
			}
			
			rearrangeGridCards();
		}
	}
	
	private void rearrangeGridCards() {
		int idx = 0;
		
		for (Recipe recipe : cachedRecipes) {
			Node card = cachedGridCards.get(recipe);
			
			if (card != null && card.isVisible()) {
				int col = idx % 3;
				int row = idx / 3;
				
				GridPane.setColumnIndex(card, col);
				GridPane.setRowIndex(card, row);
				
				idx++;
			}
		}
	}
	
	@FXML
	private void onFilterResetBtnClicked() {
		resetSearchFilter();
	}
	
	private void resetSearchFilter() {
		recipeSearchField.clear();
		refreshDisplayedRecipes();
	}
}
