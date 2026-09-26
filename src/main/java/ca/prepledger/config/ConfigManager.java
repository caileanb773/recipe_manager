package ca.prepledger.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/*
 * Author: Cailean Bernard
 * Contents: Manages loading and saving of Application Configuration. By default,
 * config files will be saved in %APPDATA%/PrepLedger/settings.cfg.
 */

public class ConfigManager {

	// TODO this is Windows only, will need to be factored into a helper method
	// for proper cross-platform support
	private final String APP_DATA = System.getenv("LOCALAPPDATA");
	private final String PREPLEDGER = "\\PrepLedger\\";
	private final String FILE_NAME = "settings.cfg";
	private final String CONFIG_PATH = APP_DATA + PREPLEDGER;
	private final String WARNING = "// do not modify this file unless you know what you are doing";


	public AppConfig load() throws IOException, FileNotFoundException {
		// If any settings fail to load, these will be the defaults
		AppLanguage language = AppLanguage.ENGLISH;
		RecipeDisplayType recipeDisplayType = RecipeDisplayType.GRID;
		Theme theme = Theme.LIGHT;
		boolean areToolTipsOn = true;
				
		// Check if 'settings.cfg' exists
		Path configPath = Path.of(CONFIG_PATH + FILE_NAME);
		
		if (!configDirExists(configPath)) {
						
			// If 'settings.cfg' doesn't exist, the folder in %APPDATA% might not either
			Path dirPath = Path.of(CONFIG_PATH);

			if (!Files.exists(dirPath)) {
				createDefaultDirectory(configPath);
			}
			
			// In either case, early return a default Config
			return new AppConfig();
		}
		
		// Read the file
		try (BufferedReader reader = new BufferedReader(
				new FileReader(CONFIG_PATH + FILE_NAME))) {

			String line;

			while ((line = reader.readLine()) != null) {
				if (line.isBlank() || line.startsWith("//")) {
					// Skip comments, blank lines
					continue;
				}

				String[] lineInfo = line.split("=", 2);

				if (lineInfo.length != 2) {
					// TODO logger warn of malformed config line.
					continue;
				}

				String key = lineInfo[0].trim().toLowerCase();
				String value = lineInfo[1].trim().toUpperCase();

				// Unknown keys will just cause this method to fallback to defaults above
				
				switch (key) {
				case "language":
					try {
						language = AppLanguage.valueOf(value);
					} catch (IllegalArgumentException e) {
						// TODO log warning
						language = AppLanguage.ENGLISH;
					}
					break;
				case "recipedisplaytype":
					try {
						recipeDisplayType = RecipeDisplayType.valueOf(value);
					} catch (IllegalArgumentException e) {
						// TODO log warning
						recipeDisplayType = RecipeDisplayType.GRID;
					}
					break;
				case "theme":
					try {
						theme = Theme.valueOf(value);
					} catch (IllegalArgumentException e) {
						// TODO log warning
						theme = Theme.LIGHT;
					}
					break;
				case "aretooltipson":
					areToolTipsOn = Boolean.parseBoolean(value);
					break;
				default:
					// TODO log unknown config key
				}
			}
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("Could not find settings.cfg.");
		} catch (IOException e) {
			throw new IOException("IOException encountered while reading settings.cfg");
		}

		return new AppConfig(language, recipeDisplayType, theme, areToolTipsOn);
	}

	public void save(AppConfig config) throws IOException {
		if (config == null) {
			// TODO log null config in save()
			return;
		}
		
		// Create config directory if not exists
		Path configPath = Path.of(CONFIG_PATH);
		
		if (!configDirExists(configPath)) {
			createDefaultDirectory(configPath);
		}
		
		// Write config to file
		try (BufferedWriter writer = new BufferedWriter(
				new FileWriter(CONFIG_PATH + FILE_NAME))) {
			
			// Write comment warning users not to fiddle with the config
			writer.write(WARNING);
			writer.newLine();
			
			// Write settings
			writer.write("language=" + config.getLanguage().toString());
			writer.newLine();
			writer.write("recipedisplaytype=" + config.getRecipeDisplayType().toString());
			writer.newLine();
			writer.write("theme=" + config.getTheme().toString());
			writer.newLine();
			writer.write("aretooltipson=" + String.valueOf(config.areTooltipsOn()));
			
		} catch (IOException e) {
			throw new IOException("IOException during ConfigManager.save().");
		}
	}
	
	private boolean configDirExists(Path path) {		
		return Files.exists(path);
	}
	
	private void createDefaultDirectory(Path path) throws IOException {
		Files.createDirectories(path);
	}
	
	public String getConfigPath() {
		return CONFIG_PATH;
	}
	
	public String getConfigAndFilePath() {
		return CONFIG_PATH + FILE_NAME;
	}

}
