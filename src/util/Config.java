package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import definitions.Constants;
import definitions.Theme;
import view.AppFrame;
import view.LoginScreen;

/**
 * Configuration class for loading and storing last used configurations.
 * 
 * @author Cailean Bernard
 * @since 2025-08-08
 */
public class Config {

	private static ResourceBundle bundle;
	private static Locale locale;
	private static Theme theme;
	private static String lastEmail;
	private static boolean autoBackup;
	private final String[] configs = { "language", "lastEmail", "autoBackup", "theme" };
	private int loadTimeout;

	// Local constants
	private final static int VALUE_IDX = 1;
	private final static int KEY_IDX = 0;
	private final int VALID_LEN = 2;

	public Config() {
		loadTimeout = 0;
		loadConfig();
	}

	public ResourceBundle getResourceBundle() {
		return bundle;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setResourceBundle(String base, Locale lang) {
		bundle = ResourceBundle.getBundle(base, lang);
	}

	public void setLocale(Locale l) {
		locale = l;
	}

	/**
	 * Fetches any past configurations that were saved when the app was last closed.
	 */
	public void loadConfig() {
		System.out.println("Loading settings");
		loadTimeout++;
		
		if (loadTimeout >= 10) {
			System.err.println("Could not load settings; too many attempts.");
			loadFailsafeConfig();
			return;
		}
		
		String lang = null;
		String value;
		Theme temp;

		try (BufferedReader reader = new BufferedReader(new FileReader("resources/config.ini"))) {
			String line = null;
			reader.readLine(); // skip the "do not edit" warning comment

			while ((line = reader.readLine()) != null) {
				String[] lineInfo = line.split("=");
				
				// XXX refactor this in the future to find the offending value
				if (lineInfo.length != VALID_LEN) {
					createDefaultConfig();
					loadConfig();
				}
				
				value = lineInfo[VALUE_IDX].trim();
				
				if (value.isEmpty()) {
					createDefaultConfig();
					loadConfig();
				}
				 
				switch (lineInfo[KEY_IDX]) {
				case "language":
					lang = value;
					break;
				case "lastEmail":
					lastEmail = value;
					break;
				case "autoBackup":
					autoBackup = Boolean.parseBoolean(value);
					break;
				case "theme":
					temp = Theme.valueOf(value);
					
					if (isValidTheme(temp)) {
						theme = temp;
					} else {
						theme = Theme.LIGHT;
					}
					
					break;
				default:
					break;
				}
			}

		} catch (FileNotFoundException e) {
			System.out.println("Initialization file not found for Config.");
			createDefaultConfig();
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}

		locale = new Locale(lang);
		bundle = ResourceBundle.getBundle("MessagesBundle", locale);
	}
	
	public boolean isValidTheme(Theme theme) {
		for (Theme t : Constants.VALID_THEMES) {
			if (theme == t) {
				return true;
			}
		}
		return false;
	}
	
	private void loadFailsafeConfig() {
		locale = new Locale("en");
		bundle = ResourceBundle.getBundle("MessagesBundle", locale);
		lastEmail = null;
		autoBackup = true;
		theme = Theme.LIGHT;
	}

	/**
	 * Saves the current configuration to the config.ini file.
	 */
	public void saveConfig() {
		System.out.println("Saving settings");
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("resources/config.ini"))) {
			writer.write("# do not edit this unless you know what you are doing\n");

			for (String cfg : configs) {
				switch (cfg) {
				case "language":
					System.out.println("Saving language as " + locale);
					writer.write("language=" + locale);
					break;
				case "lastEmail":
					if (!LoginScreen.isRemembering()) { // if not remembering email, reset
						lastEmail = null;
					}
					System.out.println("Saving lastEmail as " + lastEmail);
					writer.write("lastEmail=" + lastEmail);
					break;
				case "autoBackup":
					System.out.println("Autobackup: " + AppFrame.isBackingUp());
					writer.write("autoBackup=");
					writer.write(AppFrame.isBackingUp() ? "true" : "false");
					break;
				case "theme":
					System.out.println("Saving theme: " + theme);
					writer.write("theme=" + theme.toString().toUpperCase());
					break;
				default:
					break;
				}
				writer.write("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createDefaultConfig() {
		System.out.println("Creating config.ini");

		try (BufferedWriter writer = new BufferedWriter(new FileWriter("resources/config.ini"))) {
			writer.write("# do not edit this unless you know what you are doing\n");

			for (String cfg : configs) {
				writer.write(cfg + "=");
				switch (cfg) {
				case "language":
					writer.write("en");
					break;
				case "lastEmail":
					writer.write("null");
					break;
				case "autoBackup":
					writer.write("true");
					break;
				case "theme":
					writer.write("LIGHT");
				default:
					break;
				}
				writer.write("\n");
			}
		} catch (IOException e) {
			System.err.println("IO Exception encountered while writing config.ini");
			e.printStackTrace();
		}
		
		// XXX this could lead to an infinite loop. There's probably a better way
		loadConfig();
	}
	
	public boolean isAutoBackup() {
		return autoBackup;
	}
	
	public Theme getTheme() {
		return theme;
	}
	 
	public void setTheme(Theme theme) {
		Config.theme = theme;
	}
	
	public static String getLastEmail() {
		return lastEmail;
	}
	
	public static void setLastEmail(String email) {
		lastEmail = email;
	}

}

