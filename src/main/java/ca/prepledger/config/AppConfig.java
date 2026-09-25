package ca.prepledger.config;

/*
 * Author: Cailean Bernard
 * Contents: Contains the configurable fields of the application. The Config
 * Manager will load a configuration from a .cfg file and return an AppConfig,
 * or will read the state of AppConfig and save it to a .cfg file.
 */

public class AppConfig {

	private AppLanguage language;

	private RecipeDisplayType recipeDisplayType;

	private Theme theme;

	private boolean areTooltipsOn;


	public AppConfig() {

	}

	public AppConfig(
			AppLanguage language,
			RecipeDisplayType recipeDisplayType,
			Theme theme,
			boolean areTooltipsOn) {
		this.language = language;
		this.recipeDisplayType = recipeDisplayType;
		this.theme = theme;
		this.areTooltipsOn = areTooltipsOn;
	}


	// Getters & Setters
	public AppLanguage getLanguage() {
		return language;
	}

	public void setLanguage(AppLanguage language) {
		this.language = language;
	}

	public RecipeDisplayType getRecipeDisplayType() {
		return recipeDisplayType;
	}

	public void setRecipeDisplayType(RecipeDisplayType recipeDisplayType) {
		this.recipeDisplayType = recipeDisplayType;
	}

	public Theme getTheme() {
		return theme;
	}

	public void setTheme(Theme theme) {
		this.theme = theme;
	}

	public boolean areTooltipsOn() {
		return areTooltipsOn;
	}

	public void setTooltipsOn(boolean areTooltipsOn) {
		this.areTooltipsOn = areTooltipsOn;
	}

}
