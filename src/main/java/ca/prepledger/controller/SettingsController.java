package ca.prepledger.controller;

import ca.prepledger.config.AppConfig;
import ca.prepledger.config.AppLanguage;
import ca.prepledger.config.Configurable;
import ca.prepledger.config.RecipeDisplayType;
import ca.prepledger.config.Theme;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;

public class SettingsController implements Configurable {

	@FXML
	private ComboBox<String> languageComboBox;

	@FXML
	private ComboBox<String> recipeViewComboBox;

	@FXML
	private RadioButton themeLightRadioButton;

	@FXML
	private RadioButton themeDarkRadioButton;

	@FXML
	private CheckBox toolTipsCheckbox;

	private AppConfig appConfig;


	public void setSettingsFieldsFromConfig() {
		languageComboBox.setValue(capitalize(appConfig.getLanguage().toString()));
		recipeViewComboBox.setValue(capitalize(appConfig.getRecipeDisplayType().toString()));
		if (appConfig.getTheme().equals(Theme.LIGHT)) {
			selectLightThemeRadioButton();
			deselectDarkThemeRadioButton();
		} else {
			selectDarkThemeRadioButton();
			deselectLightThemeRadioButton();
		}

		toolTipsCheckbox.setSelected(appConfig.areTooltipsOn() ? true : false);
	}

	private String capitalize(String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}

		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
	}

	private void deselectLightThemeRadioButton() {
		themeLightRadioButton.setSelected(false);
	}

	private void deselectDarkThemeRadioButton() {
		themeDarkRadioButton.setSelected(false);
	}

	private void selectLightThemeRadioButton() {
		themeLightRadioButton.setSelected(true);
	}

	private void selectDarkThemeRadioButton() {
		themeDarkRadioButton.setSelected(true);
	}
	
	public Theme getSelectedTheme() {
		if (themeDarkRadioButton.isSelected()) {
			return Theme.DARK;
		} else if (themeLightRadioButton.isSelected()) {
			return Theme.LIGHT;
		}
		
		// TODO log warning
		return Theme.LIGHT;
	}

	@FXML
	private void initialize() {
		setLanguageComboBoxItems();
		setViewStyleComboBoxItems();
	}

	@FXML
	private void onLanguageSelected() {

	}

	@FXML
	private void onRecipeViewStyleSelected() {

	}

	@FXML
	private void onDarkRadioButtonClicked() {
		deselectLightThemeRadioButton();
	}

	@FXML
	private void onLightRadioButtonClicked() {
		deselectDarkThemeRadioButton();
	}

	@FXML
	private void onTooltipsCheckboxClicked() {

	}

	@FXML
	private void setLanguageComboBoxItems() {
		ObservableList<String> items = languageComboBox.getItems();
		items.add("English");
		items.add("French");
	}

	@FXML
	private void setViewStyleComboBoxItems() {
		ObservableList<String> items = recipeViewComboBox.getItems();
		items.add("Grid");
		items.add("List");
	}

	@FXML
	private void onSaveButtonClicked() {
		saveSettings();
	}
	
	public void saveSettings() {
		AppLanguage language = AppLanguage.valueOf(
				languageComboBox.getValue().toUpperCase());
		RecipeDisplayType recipeDisplayType = RecipeDisplayType.valueOf(
				recipeViewComboBox.getValue().toUpperCase());
		Theme theme = getSelectedTheme();
		boolean areToolTipsEnabled = toolTipsCheckbox.isSelected();
		
		appConfig.setLanguage(language);
		appConfig.setRecipeDisplayType(recipeDisplayType);
		appConfig.setTheme(theme);
		appConfig.setTooltipsOn(areToolTipsEnabled);
	}

	@Override
	public void setAppConfig(AppConfig appConfig) {
		this.appConfig = appConfig;
	}

}
