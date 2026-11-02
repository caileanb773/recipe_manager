package ca.prepledger.config;

import java.util.function.Consumer;

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
	private boolean rememberMe;
	private String email;
	private Consumer<Theme> themeChangeListener;

	
	/**
	 * Default constructor sets default preferences. These can be changed by the
	 * user via 'settings' and will be overridden when the ConfigManager loads a 
	 * configuration from the config file.
	 * 
	 * @since v0.1.0
	 */
	public AppConfig() {
		this.language = AppLanguage.ENGLISH;
		this.recipeDisplayType = RecipeDisplayType.GRID;
		this.theme = Theme.LIGHT;
		this.areTooltipsOn = true;
		this.rememberMe = false;
		this.email = "";
	}

	/**
	 * Parameterized constructor allows custom configuration to be loaded via the
	 * config manager.
	 * 
	 * @since v0.1.0
	 */
	public AppConfig(
			AppLanguage language,
			RecipeDisplayType recipeDisplayType,
			Theme theme,
			boolean areTooltipsOn,
			boolean rememberMe,
			String email) {
		this.language = language;
		this.recipeDisplayType = recipeDisplayType;
		this.theme = theme;
		this.areTooltipsOn = areTooltipsOn;
		this.rememberMe = rememberMe;
		this.email = email;
	}
	
	public void setThemeChangeListener(Consumer<Theme> listener) {
	    this.themeChangeListener = listener;
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
		
	    if (themeChangeListener != null) {
	        themeChangeListener.accept(theme);
	    }
	}

	public boolean areTooltipsOn() {
		return areTooltipsOn;
	}

	public void setTooltipsOn(boolean areTooltipsOn) {
		this.areTooltipsOn = areTooltipsOn;
	}
	
	public boolean isRememberingMe() {
		return rememberMe;
	}
	
	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

}
