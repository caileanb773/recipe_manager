package datalayer;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import definitions.Fraction;
import definitions.Ingredient;
import definitions.Recipe;
import definitions.Unit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Author: Cailean Bernard
 * Contents: DAO (Digital Access Object) for JDBC connections.
 */

public class RecipeDAO {

	private static final String URL = "jdbc:sqlite:recipes.db";
	private static final Logger logger = LoggerFactory.getLogger(RecipeDAO.class);

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
			logger.warn("SQL Exception in insertPartialRecipe(): {}", e.getMessage());
		}
		return -1;

	}

	public int insertRecipe(Recipe recipe) {
		int recipeId = insertPartialRecipe(recipe.getTitle(),
				recipe.getDirections(),
				String.join(",", recipe.getTags()));

		if (recipeId == -1) {
			logger.error("Recipe with id {} not found.", recipeId);
			return -1;
		}

		String sql = "INSERT INTO ingredients(recipe_id, amount, unit, name) VALUES (?, ?, ?, ?)";

		try (Connection conn = connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			for (Ingredient ing : recipe.getIngredients()) {
				pstmt.setInt(1, recipeId);
				pstmt.setString(2, ing.getAmount().toString());
				pstmt.setString(3, ing.getUnit().toString());
				pstmt.setString(4, ing.getName());
				pstmt.addBatch();
			}

			pstmt.executeBatch();
		} catch (SQLException e) {
			logger.warn("SQL Exception in insertRecipe(): {}", e.getMessage());
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
			logger.error("SQL Exception - Update failed: {}", e.getMessage());
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
			logger.error("SQL Exception - Update operation failed: {}", e.getMessage());
		}
	}

	public void updateRecipe(Recipe recipe) {
		logger.info("Updating {} in database.", recipe.getTitle());
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
					logger.warn("No recipe found with ID ", recipe.getId());
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
					pstmt.setString(2, ing.getAmount().toString());
					pstmt.setString(3, ing.getUnit().toString());
					pstmt.setString(4, ing.getName());
					pstmt.addBatch();
				}
				pstmt.executeBatch();
			}

			conn.commit();
			logger.info("Recipe updated successfully.");

		} catch (SQLException e) {
			logger.error("SQL Exception - Update operation failed: {}", e.getMessage());
		}
	}


	public void removeRecipe(int id) {
		String sql = "DELETE FROM recipes WHERE id = ?";

		try (Connection conn = connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, id);

			int affectedRows = pstmt.executeUpdate();

			if (affectedRows == 0) {
				logger.warn("No recipe found with id {}.", id);
			}
		} catch (SQLException e) {
			logger.error("SQL Exception - Remove operation failed: {}", e.getMessage());
		}
	}

	public void removeRecipe(String id) {
		String sql = "DELETE FROM recipes WHERE id = ?";

		try (Connection conn = connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, Integer.parseInt(id));

			int affectedRows = pstmt.executeUpdate();

			if (affectedRows == 0) {
				logger.warn("No recipe found with id {}.", id);
			}
		} catch (SQLException e) {
			logger.error("SQL Exception - Remove operation failed: {}", e.getMessage());
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
					String amtStr = rsIng.getString("amount");					
					Fraction amount;

					if (Fraction.isFraction(amtStr)) {
						amount = Fraction.parseFraction(amtStr);
					} else {
						amount = new Fraction(Integer.parseInt(amtStr), 1);
					}

					String unit = rsIng.getString("unit");
					String name = rsIng.getString("name");
					ingredients.add(new Ingredient(amount, Unit.valueOf(unit), name));
				}

				recipe = new Recipe(id, title, ingredients, directions, tags);
			}
		} catch (SQLException e) {
			logger.error("SQL Exception - Select operation failed: ", e.getMessage());
		}

		return recipe;
	}

	public void clearRecipes() {
		String sql = "DELETE FROM recipes";

		try (Connection conn = connect();
				Statement stmt = conn.createStatement()) {
			stmt.execute(sql);
		} catch (SQLException e) {
			logger.error("SQL Exception - Could not drop table: {}.", e.getMessage());
		}
		logger.info("Table dropped.");
	}

	public List<Recipe> selectAllRecipesAsList() {
		logger.info("Fetching recipes from database.");
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
							String amtStr = ingRes.getString("amount");
							Fraction amount;

							if (Fraction.isFraction(amtStr)) {
								amount = Fraction.parseFraction(amtStr);

							} else if (Fraction.isDecimal(amtStr)) {
								amount = new Fraction(new BigDecimal(amtStr));
							} else {
								amount = new Fraction(Integer.parseInt(amtStr), 1);
							}

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
			}

		} catch (SQLException e) {
			logger.error("SQL Exception - selectAllRecipesAsList() failed: {}", e.getMessage());
		}

		return recipes;
	}

}
