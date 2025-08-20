package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import definitions.Ingredient;
import definitions.Recipe;
import definitions.Unit;

/*
 * Author: Cailean Bernard
 * Contents: DAO (Digital Access Object) for JDBC connections.
 */

public class RecipeDAO {

	private static final String URL = "jdbc:sqlite:recipes.db";

	private Connection connect() throws SQLException {
		return DriverManager.getConnection(URL);
	}


	// Create the tables
	public void init() {
		String recipesTable = "CREATE TABLE IF NOT EXISTS recipes ("
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "title TEXT NOT NULL,"
				+ "directions TEXT,"
				+ "tags TEXT"
				+ ");";

		String ingredientsTable = "CREATE TABLE IF NOT EXISTS ingredients ("
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "recipe_id INTEGER NOT NULL,"
				+ "amount TEXT,"
				+ "unit TEXT,"
				+ "name NOT NULL,"
				+ "FOREIGN KEY(recipe_id) REFERENCES recipes(id) ON DELETE CASCADE"
				+ ")";

		try (Connection conn = connect();
				Statement stmt = conn.createStatement()) {
			stmt.execute(recipesTable);
			stmt.execute(ingredientsTable);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int insertPartialRecipe(String title, String directions, String tags) {
		String sql = "INSERT INTO recipes(title, directions, tags) VALUES(?, ?, ?)";

		try (Connection conn = connect();
				PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			pstmt.setString(1, title);
			pstmt.setString(2, directions);
			pstmt.setString(3, tags);
			pstmt.executeUpdate();

			try (ResultSet rs = pstmt.getGeneratedKeys()) {
				if (rs.next()) {
					return rs.getInt(1); // return generated recipe id
				}
			}

		} catch (SQLException e) {
			System.out.println("Something went wrong in insertPartialRecipe()");
			e.printStackTrace();
		}
		return -1;

	}

	public int insertRecipe(Recipe recipe) {
		int recipeId = insertPartialRecipe(recipe.getTitle(),
				recipe.getDirections(),
				String.join(",", recipe.getTags()));

		if (recipeId == -1) {
			System.out.println("Recipe with id " + recipeId + " not found.");
			return -1;
		}

		String sql = "INSERT INTO ingredients(recipe_id, amount, unit, name) VALUES (?, ?, ?, ?)";

		try (Connection conn = connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			for (Ingredient ing : recipe.getIngredients()) {
				pstmt.setInt(1, recipeId);
				pstmt.setString(2, ing.getAmount());
				pstmt.setString(3, ing.getUnit().toString());
				pstmt.setString(4, ing.getName());
				pstmt.addBatch();
			}

			pstmt.executeBatch();
		} catch (SQLException e) {
			System.out.println("Something went wrong in insertRecipe()");
			e.printStackTrace();
		}
		return recipeId;
	}

	public void updateRecipe(String title, String directions, String tags, String id) {
		String sql = "UPDATE recipes SET "
				+ "title = ?, "
				+ "directions = ?, "
				+ "tags = ? "
				+ "WHERE id = ?";

		try (Connection conn = connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, title);
			pstmt.setString(2, directions);
			pstmt.setString(3, tags);
			pstmt.setInt(4, Integer.parseInt(id));

			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Update failed: " + e.getMessage());
		}
	}

	public void updateRecipe(String title, String directions, String tags, int id) {
		String sql = "UPDATE recipes SET "
				+ "title = ?, "
				+ "directions = ?, "
				+ "tags = ? "
				+ "WHERE id = ?";

		try (Connection conn = connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, title);
			pstmt.setString(2, directions);
			pstmt.setString(3, tags);
			pstmt.setInt(4, id);

			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Update operation failed: " + e.getMessage());
		}
	}

	public void updateRecipe(Recipe recipe) {
	    System.out.println("Updating recipe in database...");
	    String rcpSql = "UPDATE recipes SET title = ?, directions = ?, tags = ? WHERE id = ?";
	    String delIngSql = "DELETE FROM ingredients WHERE recipe_id = ?";
	    String insIngSql = "INSERT INTO ingredients (recipe_id, amount, unit, name) VALUES (?, ?, ?, ?)";

	    try (Connection conn = connect()) {
	        conn.setAutoCommit(false); // begin transaction

	        // --- Update recipe row ---
	        try (PreparedStatement pstmt = conn.prepareStatement(rcpSql)) {
	            pstmt.setString(1, recipe.getTitle());
	            pstmt.setString(2, recipe.getDirections());
	            pstmt.setString(3, recipe.stringifyTags());
	            pstmt.setInt(4, recipe.getId());

	            int rows = pstmt.executeUpdate();
	            if (rows == 0) {
	                System.out.println("No recipe found with ID " + recipe.getId());
	                conn.rollback();
	                return;
	            }
	        }

	        // Delete old ingredients
	        try (PreparedStatement pstmt = conn.prepareStatement(delIngSql)) {
	            pstmt.setInt(1, recipe.getId());
	            pstmt.executeUpdate();
	        }

	        // Insert new ingredients
	        try (PreparedStatement pstmt = conn.prepareStatement(insIngSql)) {
	            for (Ingredient ing : recipe.getIngredients()) {
	                pstmt.setInt(1, recipe.getId());
	                pstmt.setString(2, ing.getAmount());
	                pstmt.setString(3, ing.getUnit().toString());
	                pstmt.setString(4, ing.getName());
	                pstmt.addBatch();
	            }
	            pstmt.executeBatch();
	        }

	        conn.commit();
	        System.out.println("Recipe updated successfully.");

	    } catch (SQLException e) {
	        System.err.println("Update operation failed: " + e.getMessage());
	        e.printStackTrace();
	    }
	}


	public void removeRecipe(int id) {
		String sql = "DELETE FROM recipes WHERE id = ?";

		try (Connection conn = connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, id);

			int affectedRows = pstmt.executeUpdate();

			if (affectedRows == 0) {
				System.out.println("No recipe found with id " + id);
			}
		} catch (SQLException e) {
			System.err.println("Remove operation failed: " + e.getMessage());
		}
	}

	public void removeRecipe(String id) {
		String sql = "DELETE FROM recipes WHERE id = ?";

		try (Connection conn = connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, Integer.parseInt(id));

			int affectedRows = pstmt.executeUpdate();

			if (affectedRows == 0) {
				System.out.println("No recipe found with id " + id);
			}
		} catch (SQLException e) {
			System.err.println("Remove operation failed: " + e.getMessage());
		}
	}

	public Recipe fetchRecipe(int id) {
		Recipe recipe = null;
		String sqlRecipe = "SELECT * FROM recipes WHERE id = ?";
		String sqlIngredient = "SELECT * FROM ingredients WHERE recipe_id = ?";

		try (Connection conn = connect();
				PreparedStatement pstmtRcp = conn.prepareStatement(sqlRecipe);
				PreparedStatement pstmtIng = conn.prepareStatement(sqlIngredient)) {
			pstmtRcp.setInt(1, id);
			ResultSet selectedRs = pstmtRcp.executeQuery();

			if (selectedRs.next()) {
				String title = selectedRs.getString("title");
				String directions = selectedRs.getString("directions");
				String tagsStr = selectedRs.getString("tags");
				List<String> tags = tagsStr != null
						? Arrays.asList(tagsStr.split(","))
								: new ArrayList<String>();

				pstmtIng.setInt(1, id);
				ResultSet rsIng = pstmtIng.executeQuery();
				List<Ingredient> ingredients = new ArrayList<>();

				while (rsIng.next()) {
					String amount = rsIng.getString("amount");
					String unit = rsIng.getString("unit");
					String name = rsIng.getString("name");
					ingredients.add(new Ingredient(amount, Unit.valueOf(unit), name));
				}

				recipe = new Recipe(id, title, ingredients, directions, tags);
			}
		} catch (SQLException e) {
			System.err.println("Select operation failed: " + e.getMessage());
		}

		return recipe;
	}

	public void clearRecipes() {
		String sql = "DELETE FROM recipes";

		try (Connection conn = connect();
				Statement stmt = conn.createStatement()) {
			stmt.execute(sql);
		} catch (SQLException e) {
			System.out.println("Could not drop table.");
			e.printStackTrace();
		}
		System.out.println("Table dropped.");
	}

	public List<Recipe> selectAllRecipesAsList() {
		System.out.println("Fetching recipes from database...");
		String rcpSql = "SELECT * FROM recipes";
		String ingSql = "SELECT * FROM ingredients WHERE recipe_id = ?";
		List<Recipe> recipes = new ArrayList<>();

		try (Connection conn = connect();
				Statement stmt = conn.createStatement();
				ResultSet rcpRes = stmt.executeQuery(rcpSql)) {

			while (rcpRes.next()) {
				int id = rcpRes.getInt("id");
				String title = rcpRes.getString("title");
				String directions = rcpRes.getString("directions");

				String tagsStr = rcpRes.getString("tags");
				List<String> tags = tagsStr != null && !tagsStr.isEmpty()
						? Arrays.asList(tagsStr.split("\\s*,\\s*"))
								: new ArrayList<>();

				List<Ingredient> ingredients = new ArrayList<>();
				try (PreparedStatement pstmt = conn.prepareStatement(ingSql)) {
					pstmt.setInt(1, id);
					try (ResultSet ingRes = pstmt.executeQuery()) {
						while (ingRes.next()) {
							String amount = ingRes.getString("amount");
							String unitStr = ingRes.getString("unit");
							String name = ingRes.getString("name");

							Unit unit;
							try {
								unit = Unit.valueOf(unitStr);
							} catch (IllegalArgumentException | NullPointerException e) {
								unit = Unit.NO_UNIT;
							}

							ingredients.add(new Ingredient(amount, unit, name));
						}
					}
				}

				Recipe recipe = new Recipe(id, title, ingredients, directions, tags);
				recipes.add(recipe);
				System.out.println("Recipe " + recipe.getTitle() + " has ID: " + recipe.getId());
			}

		} catch (SQLException e) {
			System.out.println("selectAllRecipesAsList() failed.");
			e.printStackTrace();
		}

		return recipes;
	}


	// TODO: add methods for fetching, updating, and removing recipes and ingredients

}
