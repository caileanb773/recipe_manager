package ca.prepledger.controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;

public class RecipeListController {
	
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
	
	enum RecipeViewMode {
		GRID,
		LIST
	}
	
	
	/////////////////////
	//
	// Methods
	//
	/////////////////////
	
	@FXML
	private void initialize() {
		viewingMode = RecipeViewMode.GRID;
		showGridView();
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
		addDummyRecipe();
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
	
	// XXX Temporary testing method
	private void addDummyRecipe() {
	    try {
	        FXMLLoader loader = new FXMLLoader(
	            getClass().getResource("/fxml/recipes/RecipeCard.fxml")
	        );

	        Node card = loader.load();
	        
	        RecipeCardController controller = loader.getController();
	        controller.setRecipe(null);

	        int column = recipeCount % 3;
	        int row = recipeCount / 3;

	        gridView.add(card, column, row);

	        recipeCount++;

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
}
