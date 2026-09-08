package ca.prepledger.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class RecipeListController {
	
	@FXML
	private Button gridViewBtn;
	
	@FXML
	private Button listViewBtn;
	
	@FXML
	private ScrollPane recipeScrollPane;
	
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
		showGridView();
	}
	
	@FXML
	public void onGridViewBtnClicked() {
		showGridView();
	}
	
	@FXML
	public void onListViewBtnClicked() {
		showListView();
	}
	
	private void showGridView() {
		GridPane grid = new GridPane();
			
		grid.setStyle(
			    "-fx-border-color: red;" +
			    "-fx-border-width: 3px;"
			);

		grid.setGridLinesVisible(true);
		grid.add(new Label("Grid1"), 0, 0);
		grid.add(new Label("Grid2"), 1, 0);

		recipeScrollPane.setContent(grid);
		recipeScrollPane.setFitToHeight(true);
		recipeScrollPane.setFitToWidth(true);
	}
	
	private void showListView() {
		VBox list = new VBox();
				
		list.setStyle(
			    "-fx-border-color: blue;" +
			    "-fx-border-width: 3px;"
			);
		
		list.getChildren().add(new Label("List"));
		recipeScrollPane.setContent(list);
		recipeScrollPane.setFitToHeight(true);
		recipeScrollPane.setFitToWidth(true);
	}

}
