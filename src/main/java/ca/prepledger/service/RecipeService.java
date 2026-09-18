package ca.prepledger.service;

import java.util.ArrayList;

import ca.prepledger.model.Recipe;

public class RecipeService {
	
	// private RecipeRepository repository;
	
	// XXX to be removed
	private ArrayList<Recipe> dummyRecipeList;
	
	
	// Methods
	
	// XXX
	public ArrayList<Recipe> getAllRecipes() {
		System.out.println("getAllRecipes() here");
		return dummyRecipeList;
	}

}
