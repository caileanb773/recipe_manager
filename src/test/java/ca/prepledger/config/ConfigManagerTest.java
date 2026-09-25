package ca.prepledger.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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

	    assertEquals(expected, configManager.getConfigAndFilePath());
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
	
	@Test
	void testSave() throws IOException {
	    ConfigManager manager = new ConfigManager();

	    AppConfig config = new AppConfig();

	    manager.save(config);

	    Path path = Path.of(manager.getConfigAndFilePath());

	    assertTrue(Files.exists(path));

	    List<String> lines = Files.readAllLines(path);

	    assertEquals("// do not modify this file unless you know what you are doing", lines.get(0));
	    assertEquals("language=ENGLISH", lines.get(1));
	    assertEquals("recipedisplaytype=GRID", lines.get(2));
	    assertEquals("theme=LIGHT", lines.get(3));
	    assertEquals("aretooltipson=true", lines.get(4));
	}
	
}
