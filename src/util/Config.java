package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Configuration class for loading and storing last used settings.
 * 
 * @author Cailean Bernard
 * @since 2025-08-08
 */
public class Config {

	private static ResourceBundle bundle;
	private static Locale locale;
	private final String[] configs = { "language" };
	
	// Local constants
	private final static int VALUE_IDX = 1;
	private final static int LANG_IDX = 0;

	public Config() {
		initConfig();
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
	 * Fetches any past configurations that were saved when the app was last
	 * closed. This is just language for now, but could later include accessibility
	 * settings or other miscellaneous settings stored in 'settings.ini'.
	 */
	public void initConfig() {
		String lang = null;

		// TODO implement creation of default ini file if one is not found
		try (BufferedReader reader = new BufferedReader(new FileReader("config.ini"))) {
			String line = null;
			reader.readLine(); // skip the "do not edit" warning comment

			/* XXX for now, this is fine. but when there's more than one setting,
			 * will need to read the entire ini file and then parse line by line
			 * separately*/
			while ((line = reader.readLine()) != null) {
				String[] lineInfo = line.split("=");
				lang = lineInfo[VALUE_IDX]; 
			}

		} catch (FileNotFoundException e) {
			System.out.println("Initialization file not found for Config.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		locale = new Locale(lang);
		bundle = ResourceBundle.getBundle("resources.MessagesBundle", locale);
	}
	
	public void saveConfig() {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("config.ini"))) {
			writer.write("# do not edit this unless you know what you are doing\n");
			writer.write("language=" + locale + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// save this for when there's more than one setting
	public void loadSettings() {
		try (BufferedReader reader = new BufferedReader(new FileReader("config.ini"))) {
			StringBuilder sb = new StringBuilder();
			String line = null;
			String fileContents = null;
			String language = null; // only setting to init, currently
			
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			
			fileContents = sb.toString();
			System.out.println(fileContents);
			String[] fileContentsArr = fileContents.split("\n");
			language = fileContentsArr[LANG_IDX].split("=")[VALUE_IDX];
			System.out.println(language);
			
		} catch (FileNotFoundException e) {
			System.err.println("Could not find 'settings.ini'.");
			initSettings();
		} catch (IOException e) {
			e.printStackTrace();	
		}
	}

	public void initSettings() {
		System.out.println("Creating default settings file...");
	}

}

