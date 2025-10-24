package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import definitions.Constants;
import definitions.Fraction;
import definitions.Ingredient;
import definitions.Recipe;
import definitions.Unit;
import util.ProgressListener;

/*
 * Author: Cailean Bernard
 * Contents: Internal storage for the List of Recipe objects that is loaded when
 * the application is launched. The model is modified whenever recipes are added,
 * removed.
 */

public class RecipeMgrModel {

	private List<Recipe> recipes;
	private ProgressListener progressListener;

	// Constants
	private static final int INGREDIENT_AMT_IDX = 0;
	private static final int INGREDIENT_UNIT_IDX = 1;
	private static final int INGREDIENT_NAME_IDX = 2;
	private static final int RECIPE_NAME_IDX = 0;
	private static final int RECIPE_INSTRUCTIONS_IDX = 2;


	public RecipeMgrModel(List<Recipe> r) {
		if (!r.isEmpty()) {
			recipes = r;
		}
		else {
			System.err.println("RecipeBook instantiated with empty List<Recipe>.");
		}
	}

	public RecipeMgrModel() {
		recipes = new ArrayList<Recipe>();
	}
	
	public void initialize(boolean online) {
		reportProgress(0, "Loading model...");
		if (online) {
			// wait for recipes from controller
		} else {
			initModelOffline("backup.rcp");
		}
		
		reportProgress(5, "Model loaded.");
	}

	public void initModelOffline(String recipeFilePath) {
		if (recipeFilePath.isEmpty()) {
			System.err.println("Empty path passed to initModelOffline().");
			return;
		}

		if (recipes == null) {
			System.err.println("Recipes was not initialized before initModelOffline().");
			return;
		}

		try {
			recipes = importRecipeList(recipeFilePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setProgressListener(ProgressListener progressListener) {
		if (progressListener != null) {
			this.progressListener = progressListener;
		}
	}
	
	public void reportProgress(int percent, String msg) {
		if (progressListener != null) {
			progressListener.onProgress(percent, msg);
		}
	}

	// XXX this feels hacky
	private Ingredient parseIngredientFromStrArr(String[] strArr) throws NumberFormatException {
		String amount = strArr[INGREDIENT_AMT_IDX];
		Unit unit = Unit.valueOf(strArr[INGREDIENT_UNIT_IDX].toUpperCase());
		String name = strArr[INGREDIENT_NAME_IDX].replace("_", " ");
		Fraction fracAmt = null;
		BigDecimal decAmt;
		int intAmt;
		boolean malformed = false;

		if (Fraction.isFraction(amount)) {
			fracAmt = Fraction.parseFraction(amount);
			if (fracAmt.getDen() <= 0 || fracAmt.getNum() <= 0) {
				malformed = true;
			}
		} else if (Fraction.isDecimal(amount)) {
			decAmt = new BigDecimal(amount);
			if (decAmt.compareTo(BigDecimal.ZERO) == -1) {
				malformed = true;
			} else {
				fracAmt = new Fraction(decAmt);
			}
		} else {
			intAmt = Integer.parseInt(amount);
			if (intAmt <= 0) {
				malformed = true;
			} else {
				fracAmt = new Fraction(intAmt, 0, 1);
			}
		}		

		try {
			if (malformed || unit == null || name.isEmpty()) {
				System.err.println("Malformed ingredient encountered during parsing.");
				return null;
			}
		} catch (NumberFormatException e) {
			System.err.println("Error parsing amount as float: " + amount);
			return null;
		}

		return new Ingredient(fracAmt, unit, name);
	}

	public void exportRecipeList(String exportPath) throws IOException, SecurityException {
		if (recipes.isEmpty() || recipes == null) {
			System.out.println("Cancelling export: empty recipe list.");
			return;
		} else if (exportPath.isEmpty()) {
			System.err.println("Invalid export path: blank path.");
			return;
		}

		System.out.println("Saving recipe list locally");
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(exportPath))) {
			for (Recipe r : recipes) {
				writer.write(r.formatRecipeForExport());
				writer.write("\n");
			}
		} catch (IOException | SecurityException e) {
			e.printStackTrace();
		}
	}

	public List<Recipe> importRecipeList(String path) 
			throws FileNotFoundException, IOException, 
			NumberFormatException, ArrayIndexOutOfBoundsException {
		
		List<Recipe> newRecipes = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
			String fileLine;
			String name;
			String[] ingredientsArr;
			String instructions;
			List<Ingredient> ingredientsList;

			// each line in the file is a separate recipe, read recipes until EOF
			while ((fileLine = reader.readLine()) != null) {
				String[] recipeStrArr = fileLine.split(Constants.RECIPE_SECT_DELIM);

				// Recipe 'length' is 3 if it doesn't have tags, 4 if it does
				if (recipeStrArr.length < 3 || recipeStrArr.length > 4) {
					throw new ArrayIndexOutOfBoundsException();
				}

				name = recipeStrArr[RECIPE_NAME_IDX].replace("_", " ");
				ingredientsArr = recipeStrArr[INGREDIENT_UNIT_IDX].split(Constants.ING_TAG_DELIM);
				instructions = recipeStrArr[RECIPE_INSTRUCTIONS_IDX].replace("\\n", "\n");
				int ingredientsArrLen = ingredientsArr.length;

				if (ingredientsArrLen > 0) {
					ingredientsList = new ArrayList<>();

					for (int i = 0; i < ingredientsArrLen; i++) {
						String[] unparsedIngredientStrArr = ingredientsArr[i].split(Constants.INGREDIENT_SECT_DELIM);
						Ingredient newIng = parseIngredientFromStrArr(unparsedIngredientStrArr);

						if (newIng == null) {
							System.err.println("Malformed recipe encountered during parsing.");
							return null;
						} else {
							ingredientsList.add(newIng);
						}
					}
				} else {
					System.err.println("IngredientsArrLen was less than 0 in parseStrArrFromPath().");
					return null;
				}

				System.out.println("Adding new recipe: " + name);

				if (recipeStrArr.length == Constants.LENGTH_WITH_TAGS) {
					String[] tags = recipeStrArr[Constants.TAGS_IDX].split(Constants.ING_TAG_DELIM);
					newRecipes.add(new Recipe(name, ingredientsList, instructions, tags));
				} else {
					newRecipes.add(new Recipe(name, ingredientsList, instructions));
				}

			}

		} catch (FileNotFoundException e) {
			System.err.println("parseStrArrFromPath() could not find file located at provided path: ");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("parseStrArrFromPath() encountered an IO exception: ");
			e.printStackTrace();
		}

		if (newRecipes.isEmpty()) {
			System.err.println("No recipes detected in import file.");
		}

		recipes.addAll(newRecipes);
		return newRecipes;
	}

	public void addRecipe(Recipe newRecipe) {
		if (newRecipe != null) {
			recipes.add(newRecipe);
		} else {
			System.err.println("Attempted to add a null recipe to list.");
		}
	}

	public void removeRecipe(Recipe oldRecipe) {
		if (oldRecipe != null && recipes.contains(oldRecipe)) {
			recipes.remove(oldRecipe);
		} else {
			System.err.println("Attemped to remove null recipe from list");
		}
	}

	public List<Recipe> getRecipes() {
		return recipes;
	}

	public void setRecipes(List<Recipe> recipes) {
		this.recipes = recipes;
	}

}
