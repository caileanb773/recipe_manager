package definitions;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import init.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Author: Cailean Bernard
 * Contents: Constants that are needed in more than one class.
 */
public class Constants {

	////////////////////////////////////////////////////////////////////////////
	// 
	// V I E W  C O N S T A N T S
	// 
	// Constants that are used in the View. Subsections may have constants that
	// are used only in that particular subsection.
	//
	////////////////////////////////////////////////////////////////////////////
	public static final Border	SOFT_LOWERED_BORDER = BorderFactory.createSoftBevelBorder(BevelBorder.LOWERED);
	public static final Border	SOFT_RAISED_BORDER = BorderFactory.createSoftBevelBorder(BevelBorder.RAISED);
	public static final int		CARET_START = 0;
	public static final int 	EMAIL_IDX = 0;
	public static final int 	ADD_MODE = 0;
	public static final int 	EDIT_MODE = 1;
	public static final int 	SCROLL_SPEED = 19;
	public static final Font 	titleFont = new Font("Serif", Font.BOLD, 17);
	public static final Font 	textFont = new Font("Segoe UI", Font.PLAIN, 15);
	public static final int 	UNSCALED = 0;
	public static final int 	SCALED = 1;
	
	// AddRecipeDialog
	public static final int 	AMT_IDX = 0;
	public static final int 	UNIT_IDX = 1;
	public static final int		NAME_IDX = 2;
	public static final int 	DEFAULT_LENGTH = 3;
	
	// LoginScreen
	public static final int 	VALID = 1;
	public static final int 	PW_IDX = 1;
	public static final int 	NONEXISTENT_EMAIL = 0;
	public static final int 	INCORRECT_PASSWORD = -1;
	
	// RegisterScreen
	public static final int 	MIN_PW_LEN = 12;
	public static final String 	ASCII_SPECIAL_CHARS = "!@#$%^&*()_+-=`~|\\[]{};':\",./<>?";
	
	// NotificationPanel
	public static final int SORT_TIME = 0;
	public static final int SORT_SENDER = 1;
	public static final int SORT_TYPE = 2;
	public static final int SORT_RCPNAME = 3;
	public static final boolean	ASCENDING = true;
	public static final boolean	DESCENDING = false;
	
	// Theme-specific constants
	public static final Theme[] VALID_THEMES 				= { Theme.LIGHT, Theme.DARK };
	public static final Color 	LIGHT_BG_COL 				= new Color(240,240,240);
	public static final Color 	LIGHT_RECIPE_BTN_COL 		= new Color(217,217,217);
	public static final Color	LIGHT_ACTIVE_NOTIF_COL		= new Color(240,240,240);
	public static final Color	LIGHT_INACTIVE_NOTIF_COL	= new Color(180,180,180);
	public static final Color 	LIGHT_GRADIENT_TOP 			= new Color(170,170,170);
	public static final Color 	LIGHT_GRADIENT_BOTTOM 		= new Color(225,225,225);
	public static final Color  	LIGHT_FG_COL	 			= new Color(200,200,200);
	public static final Color 	DARK_BG_COL 				= new Color(46,46,46);
	public static final Color 	DARK_RECIPE_BTN_COL 		= new Color(83,83,83);
	public static final Color	DARK_ACTIVE_NOTIF_COL		= new Color(105,105,105);
	public static final Color	DARK_INACTIVE_NOTIF_COL		= new Color(60,60,60);
	public static final Color 	DARK_GRADIENT_TOP 			= new Color(69,69,69);
	public static final Color 	DARK_GRADIENT_BOTTOM 		= new Color(31,31,31);
	public static final Color 	DARK_FG_COL 				= new Color(90,90,90);
	
	
	////////////////////////////////////////////////////////////////////////////
	// 
	// C O N T R O L L E R  C O N S T A N T S
	// 
	// Constants that are used in the Controller. Subsections may have constants
	// that are used only in that particular subsection.
	//
	////////////////////////////////////////////////////////////////////////////
	public static final boolean ONLINE = true;
	public static final boolean OFFLINE = false;
	public static final int CMD_IDX = 0;
	public static final int DATA_IDX = 1;
	public static final int CMD_WITH_DATA = 1;
	
	////////////////////////////////////////////////////////////////////////////
	// 
	// M O D E L  C O N S T A N T S
	// 
	// Constants that are used in the Model. Subsections may have constants that
	// are used only in that particular subsection.
	//
	////////////////////////////////////////////////////////////////////////////
	public static final String 	RECIPE_SECT_DELIM = "§§";
	public static final String	ING_TAG_DELIM = "¤";
	public static final String	INGREDIENT_SECT_DELIM = "█";
	public static final Font 	RECIPE_TXT_FONT;
	public static final int 	TAGS_IDX = 3;
	public static final int 	LENGTH_WITH_TAGS = 4;


	////////////////////////////////////////////////////////////////////////////
	// 
	// O T H E R  C O N S T A N T S
	//
	////////////////////////////////////////////////////////////////////////////
	private static final Logger logger = LoggerFactory.getLogger(Constants.class);
	public static final String 	ILLEGAL_EMAIL_CHARS = "()[]:;<>%,\\%&*+=?{}|";
	
	static {
		Font font = null;
		try {
			InputStream fontStream = Main.class.getClassLoader().getResourceAsStream("font/Ubuntu/Ubuntu-Regular.ttf"); 
			if (fontStream != null) {
				font = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(Font.PLAIN, 18);
			} else {
				logger.warn("Recipe List font file not found.");
				font = new Font("SansSerif", Font.BOLD, 18);
			}
		} catch (FontFormatException | IOException e) {
			logger.warn("FontFormatException/IOException: Error loading Recipe List font: {}", e.getMessage());
			font = new Font("SansSerif", Font.BOLD, 18);
		}
		RECIPE_TXT_FONT = font;
	}

	/**
	 * Private constructor prevents instantiation of this class.
	 */
	private Constants() {}
}