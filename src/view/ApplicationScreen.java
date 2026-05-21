package view;

import java.util.Locale;
import definitions.Theme;

/**
 * The collection of methods that each screen of the application must implement.
 * Each JPanel in the CardLayout of the AppFrame class is considered a separate
 * "screen" of the application and therefore implements this interface.
 */
public interface ApplicationScreen {
	
	/**
	 * Updates the locale/bundle that the application relies on for translations.
	 */
	void updateBundle(Locale locale);
	
	/**
	 * Refreshes any translatable elements within the screen.
	 */
	void refreshTranslatable();
	
	/**
	 * Changes the visual theme of the screen.
	 */
	void changeTheme(Theme theme);

}
