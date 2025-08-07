package util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import definitions.Constants;

/*
 * Author: Cailean Bernard
 * Contents: Contains constants both mutable and immutable that are needed in
 * more than one class.
 */

public class Config {

	private static ResourceBundle translatable;
	private static Locale language;

	public Config() {
		initConfig();
	}

	public ResourceBundle getTranslatable() {
		return translatable;
	}

	public Locale getLanguage() {
		return language;
	}

	public void setTranslatable(ResourceBundle t) {
		translatable = t;
	}

	public void setLocale(Locale l) {
		language = l;
	}

	public void initConfig() {
		String lang = null;

		// TODO implement creation of default ini file if one is not found
		try (BufferedReader reader = new BufferedReader(new FileReader("settings.ini"))) {
			String line = null;

			/* XXX for now, this is fine. but when there's more than one setting,
			 * will need to read the entire ini file and then parse line by line
			 * separately*/

			while ((line = reader.readLine()) != null) {
				String[] lineInfo = line.split("=");
				lang = lineInfo[Constants.VALUE_IDX]; 
			}

		} catch (FileNotFoundException e) {
			System.out.println("Initialization file not found for Config.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		language = new Locale(lang);
		translatable = ResourceBundle.getBundle("resources.MessagesBundle", language);
	}

}

