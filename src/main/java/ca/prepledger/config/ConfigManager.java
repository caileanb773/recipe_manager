package ca.prepledger.config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/*
 * Author: Cailean Bernard
 * Contents: Manages loading and saving of Application Configuration. By default,
 * config files will be saved in %APPDATA%/PrepLedger/settings.cfg.
 */

public class ConfigManager {

	// TODO this is Windows only, will need to be factored into a helper method
	// for proper cross-platform support
	private final String APP_DATA = System.getenv("LOCALAPPDATA");
	private final String PREPLEDGER = "/PrepLedger/";
	private final String FILE_NAME = "settings.cfg";
	private final String CONFIG_PATH = APP_DATA + PREPLEDGER + FILE_NAME;


	public AppConfig load() throws IOException, FileNotFoundException {
		// If any settings fail to load, these will be the defaults
		AppLanguage language = AppLanguage.ENGLISH;
		RecipeDisplayType recipeDisplayType = RecipeDisplayType.GRID;
		Theme theme = Theme.LIGHT;
		boolean areToolTipsOn = true;

		// Read the file
		try (BufferedReader reader = new BufferedReader(
				new FileReader(CONFIG_PATH))) {

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

				// TODO still need to check here that the values are valid (later)
				
				switch (key) {
				case "language":
					language = AppLanguage.valueOf(value);
					break;
				case "recipedisplaytype":
					recipeDisplayType = RecipeDisplayType.valueOf(value);
					break;
				case "theme":
					theme = Theme.valueOf(value);
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

	public void save(AppConfig config) {

	}

}
