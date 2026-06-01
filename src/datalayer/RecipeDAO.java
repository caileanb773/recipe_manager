package datalayer;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.Properties;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import definitions.Fraction;
import definitions.Ingredient;
import definitions.Recipe;
import definitions.Unit;

/*
 * Author: Cailean Bernard
 * Contents: DAO (Digital Access Object) for JDBC connections.
 */
public class RecipeDAO {

	// Fields
	private static String DB_HOST;
	private static String DB_USER;
	private static String DB_PASSWORD;
	private static final Logger logger = LoggerFactory.getLogger(RecipeDAO.class);
	private static boolean isConnected = false;


	/**
	 * This static block is run when the RecipeDao object is created and configures
	 * the network settings so the database can be reached. Will attempt to load
	 * the credentials from db.properties. If that fails, will attempt to create
	 * a default configuration file and load from that file. This will be attempted
	 * 10 times before the application gives up and starts in offline mode.
	 */
	static {
		boolean loadSucceeded = false;
		
		try {
			loadDefaultPropertiesFile();
			loadSucceeded = true;
		} catch (FileNotFoundException e) {
			logger.warn("RecipeDAO static block:"
					+ "Could not find db.properties. Creating default file.", e);
		} catch (Exception e) {
			logger.error("RecipeDAO static block: Failed to load database config", e);
		}

		if (!loadSucceeded) {
			createDefaultPropertiesFile();

			try {
				loadDefaultPropertiesFile();
				loadSucceeded = true;
			} catch (FileNotFoundException e) {
				logger.warn("RecipeDAO static block: Could not find default "
						+ "properties file. Will start in offline mode.", e);
			} catch (Exception e) {
				logger.error("RecipeDAO static block: Failed to load database config", e);
			}

			if (!arePropertiesValid()) {
				isConnected = false;
			}
		}
	}
	
	// XXX IMPORTANT: This needs to be AFTER the static block.
	private static final String URL = "jdbc:postgresql://" + DB_HOST + ":5432/recipes";

	/**
	 * In the event db.properties could not be found, create a default one. This
	 * file will use the environment variables set during installation instead of
	 * hard-coded values that could be exposed.
	 */
	private static void createDefaultPropertiesFile() {
		logger.info("Creating default db.properties.");

		try (BufferedWriter writer = new BufferedWriter(new FileWriter("resources/db.properties"))) {
			writer.write("db.host=127.0.0.1"); // Localhost is temporary
			writer.newLine();
			writer.write("db.port=5432");
			writer.newLine();
			writer.write("db.name=recipes");
			writer.newLine();
			writer.write("db.user=your_username");
			writer.newLine();
			writer.write("db.password=your_password");
		} catch (IOException e) {
			logger.error("CreateDefaultPropertiesFile():"
					+ "IO Exception encountered while writing db.properties", e);
		}
	}

	/**
	 * Loads the credentials needed to connect to the database.
	 */
	private static void loadDefaultPropertiesFile() throws FileNotFoundException, Exception {
		Properties props = new Properties();
		try (InputStream is = RecipeDAO.class.getClassLoader().getResourceAsStream("db.properties")) {
			props.load(is);
			DB_HOST = props.getProperty("db.host");
			DB_USER = props.getProperty("db.user");
			DB_PASSWORD = props.getProperty("db.password");
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException();
		} catch (Exception e) {
			throw new Exception();
		}
	}

	/**
	 * Validates that the credentials loaded by db.properties look like real
	 * credentials.
	 * 
	 * TODO
	 */
	private static boolean arePropertiesValid() {
		boolean isValid = false;

		Properties props = new Properties();
		try (InputStream is = RecipeDAO.class.getClassLoader().getResourceAsStream("db.properties")) {
			props.load(is);
			String host = props.getProperty("db.host");
			String port = props.getProperty("db.port");
			String name = props.getProperty("db.name");
			String user = props.getProperty("db.user");
			String pass = props.getProperty("db.password");

			if (host == null || host.isEmpty() 
					|| port == null || port.isEmpty()
					|| name == null || name.isEmpty()
					|| user == null || user.isEmpty()
					|| pass == null || pass.isEmpty()) {
				isValid = false;
			} else {
				isValid = true;
			}			
		} catch (FileNotFoundException e) {
			logger.warn("ValidateProperties(): Could not find 'db.properties'.", e);
			isValid = false;
		} catch (Exception e) {
			logger.error("ValidateProperties(): Error loading db.properties.", e);
			isValid = false;
			//throw new RuntimeException("An exception occurred: ", e);
		}

		return isValid;
	}

	private Connection connect() throws SQLException {
		return DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
	}

	// Create the tables
	public void initialize() throws SQLException, PSQLException {
		String recipesTable = "CREATE TABLE IF NOT EXISTS recipes ("
				+ "id SERIAL PRIMARY KEY,"
				+ "title TEXT NOT NULL,"
				+ "directions TEXT,"
				+ "tags TEXT"
				+ ");";

		String ingredientsTable = "CREATE TABLE IF NOT EXISTS ingredients ("
				+ "id SERIAL PRIMARY KEY,"
				+ "recipe_id INTEGER NOT NULL,"
				+ "amount TEXT,"
				+ "unit TEXT,"
				+ "name TEXT NOT NULL,"
				+ "FOREIGN KEY(recipe_id) REFERENCES recipes(id) ON DELETE CASCADE"
				+ ");";

		try (Connection conn = connect();
				Statement stmt = conn.createStatement()) {

			stmt.execute(recipesTable);
			stmt.execute(ingredientsTable);
			logger.info("Successfully connected to database.");
		} catch (PSQLException e) {
			throw e;
		} catch (SQLException e) {
			throw e;
		}
	}



	public int insertRecipe(Recipe recipe) {
		String insertRecipeSql = "INSERT INTO recipes(title, directions, tags) VALUES(?, ?, ?)";
		String insertIngredientSql = "INSERT INTO ingredients(recipe_id, amount, unit, name) VALUES (?, ?, ?, ?)";

		int recipeId = -1;

		try (Connection conn = connect()) {
			conn.setAutoCommit(false);   // Start transaction

			// Insert recipe
			try (PreparedStatement pstmt = conn.prepareStatement(insertRecipeSql, Statement.RETURN_GENERATED_KEYS)) {
				pstmt.setString(1, recipe.getTitle());
				pstmt.setString(2, recipe.getDirections());
				pstmt.setString(3, recipe.stringifyTags() != null ? recipe.stringifyTags() : "");

				pstmt.executeUpdate();

				try (ResultSet rs = pstmt.getGeneratedKeys()) {
					if (rs.next()) {
						recipeId = rs.getInt(1);
					}
				}
			}

			if (recipeId == -1) {
				conn.rollback();
				logger.error("Failed to generate recipe ID");
				return -1;
			}

			// Insert ingredients
			try (PreparedStatement pstmt = conn.prepareStatement(insertIngredientSql)) {
				for (Ingredient ing : recipe.getIngredients()) {
					pstmt.setInt(1, recipeId);
					pstmt.setString(2, ing.getAmount().toString());
					pstmt.setString(3, ing.getUnit().toString());
					pstmt.setString(4, ing.getName());
					pstmt.addBatch();
				}
				pstmt.executeBatch();
			}

			// Commit both recipe and ingredients
			conn.commit();
			logger.info("Recipe '{}' inserted successfully with ID {}", recipe.getTitle(), recipeId);
		} catch (PSQLException e) {
			logger.error("InsertRecipe(): Couldn't establish connection to db", e);
			return -1;
		} catch (SQLException e) {
			logger.error("Failed to insert recipe: {}: {}", recipe.getTitle(), e);
			return -1;
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
		} catch (PSQLException e) {
			logger.error("UpdateRecipe(String,String,String,String): Couldn't establish connection to db", e);
		} catch (SQLException e) {
			logger.error("UpdateRecipe(String,String,String,String): Update failed", e);
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
		} catch (PSQLException e) {
			logger.error("UpdateRecipe(String,String,String,int): Couldn't establish connection to db", e);
		} catch (SQLException e) {
			logger.error("UpdateRecipe(String,String,String,int): Update operation failed", e);
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
		} catch (PSQLException e) {
			logger.error("UpdateRecipe(Recipe): Couldn't establish connection to db", e);
		} catch (SQLException e) {
			logger.error("UpdateRecipe(Recipe): Update operation failed", e);
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
		} catch (PSQLException e) {
			logger.error("RemoveRecipe(int): Couldn't establish connection to db", e);
		} catch (SQLException e) {
			logger.error("RemoveRecipe(int): Remove operation failed", e);
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
		} catch (PSQLException e) {
			logger.error("RemoveRecipe(String): Couldn't establish connection to db", e);
		} catch (SQLException e) {
			logger.error("RemoveRecipe(String): Remove operation failed", e);
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
		} catch (PSQLException e) {
			logger.error("FetchRecipe(): Couldn't establish connection to db", e);
			return null;
		} catch (SQLException e) {
			logger.error("FetchRecipe(): Select operation failed", e);
			return null;
		}

		return recipe;
	}

	public void clearRecipes() {
		String sql = "DELETE FROM recipes";
		try (Connection conn = connect();
				Statement stmt = conn.createStatement()) {

			int rows = stmt.executeUpdate(sql);
			logger.info("Cleared {} recipes from database.", rows);
		} catch (PSQLException e) {
			logger.error("ClearRecipes(): Couldn't establish connection to db", e);
		} catch (SQLException e) {
			logger.error("ClearRecipes(): Failed to clear recipes", e);
		}
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
		} catch (PSQLException e) {
			logger.error("SelectAllRecipesAsList(): Couldn't establish connection to db", e);
			return null;
		} catch (SQLException e) {
			logger.error("SelectAllRecipesAsList(): failed", e);
			return null;
		}

		return recipes;
	}
	
	public boolean isConnected() {
		return isConnected;
	}
	
	public void setConnected(boolean connected) {
		isConnected = connected;
	}

}
