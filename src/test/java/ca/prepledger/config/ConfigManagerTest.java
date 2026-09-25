package ca.prepledger.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

/*
 * Author: Cailean Bernard
 * Contents: 
 */

public class ConfigManagerTest {
	
	@Test
	void testConfigPath() {
	    ConfigManager configManager = new ConfigManager();

	    String expected = System.getenv("LOCALAPPDATA")
	            + "\\PrepLedger\\settings.cfg";
	    
	    System.out.println("Path: " + expected);

	    assertEquals(expected, configManager.getConfigPath());
	}
	
	@Test
	void testConfigValuesLoaded() {
		ConfigManager configManager = new ConfigManager();
		AppConfig config = null;
		
		try {
			config = configManager.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertEquals(config.getLanguage(), AppLanguage.ENGLISH);
		assertEquals(config.getRecipeDisplayType(), RecipeDisplayType.GRID);
		assertEquals(config.getTheme(), Theme.LIGHT);
		assertEquals(config.areTooltipsOn(), true);
	}

}
