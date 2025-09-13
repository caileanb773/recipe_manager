package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

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
	private static String lastEmail;
	private final String[] configs = { "language", "lastEmail" };

	// Local constants
	private final static int VALUE_IDX = 1;
	private final static int KEY_IDX = 0;

	public Config() {
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
		String lang = null;

		// TODO implement creation of default ini file if one is not found
		try (BufferedReader reader = new BufferedReader(new FileReader("resources/config.ini"))) {
			String line = null;
			reader.readLine(); // skip the "do not edit" warning comment

			while ((line = reader.readLine()) != null) {
				String[] lineInfo = line.split("=");
				switch (lineInfo[KEY_IDX]) {
				case "language":
					lang = lineInfo[VALUE_IDX]; 
					break;
				case "lastEmail":
					lastEmail = lineInfo[VALUE_IDX];
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

	public void saveConfig() {
		System.out.println("Saving settings...");
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("resources/config.ini"))) {
			writer.write("# do not edit this unless you know what you are doing\n");

			for (String cfg : configs) {
				switch (cfg) {
				case "language":
					System.out.println("Saving language as " + locale);
					writer.write("language=" + locale + "\n");
					break;
				case "lastEmail":
					if (!LoginScreen.isRemembering()) { // if not remembering email, reset
						lastEmail = null;
					}
					System.out.println("Saving lastEmail as " + lastEmail);
					writer.write("lastEmail=" + lastEmail + "\n");
					break;
				default:
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createDefaultConfig() {
		System.out.println("Creating config.ini...");

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
	
	public static String getLastEmail() {
		return lastEmail;
	}
	
	public static void setLastEmail(String email) {
		lastEmail = email;
	}

}

