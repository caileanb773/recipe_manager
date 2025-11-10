package definitions;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

import init.Main;

/*
 * Author: Cailean Bernard
 * Contents: Constants that are needed in more than one class.
 */
public class Constants {

	public static final int 	BUTTON_HEIGHT = 30;
	public static final int 	BUTTON_WIDTH = 40;
	public static final int 	NUM_SHOWN_BUTTONS = 11;
	public static final int 	EMAIL_IDX = 0;
	public static final int 	PW_IDX = 1;
	public static final int 	AMT_IDX = 0;
	public static final int 	UNIT_IDX = 1;
	public static final int		NAME_IDX = 2;
	public static final int 	TAGS_IDX = 3;
	public static final int 	VALUE_IDX = 1;
	public static final int 	LENGTH_WITH_TAGS = 4;
	public static final int 	DEFAULT_LENGTH = 3;
	public static final int 	ADD_MODE = 0;
	public static final int 	EDIT_MODE = 1;
	public static final int 	SCROLL_SPEED = 19;
	public static final int 	MIN_PW_LEN = 12;
	public static final int 	VALID = 1;
	public static final int 	NONEXISTENT_EMAIL = 0;
	public static final int 	INCORRECT_PASSWORD = -1;
	public static final int 	ERROR = -2;
	public static final boolean SUCCESS = true;
	public static final boolean FAIL = false;
	public static final boolean ONLINE = true;
	public static final boolean OFFLINE = false;
	public static final String 	ASCII_SPECIAL_CHARS = "!@#$%^&*()_+-=`~|\\[]{};':\",./<>?";
	public static final String 	ILLEGAL_EMAIL_CHARS = "()[]:;<>%,\\%&*+=?{}|";
	public static final String 	RECIPE_SECT_DELIM = "§§";
	public static final String	ING_TAG_DELIM = "¤";
	public static final String	INGREDIENT_SECT_DELIM = "█";
	public static final Font 	titleFont = new Font("Serif", Font.BOLD, 16);
	public static final Font 	buttonFont = new Font("Serif", Font.BOLD, 16);
	public static final Font 	textFont = new Font("Segoe UI", Font.PLAIN, 15);
	public static final Font 	recipeTxtFont;
	
	// Theme-specific constants
	public static final Theme[] VALID_THEMES = { Theme.LIGHT, Theme.DARK };
	public static final Color 	LIGHT_THEME_BG_COL = new Color(240,240,240);
	public static final Color 	LIGHT_THEME_RECIPE_BTN_COL = new Color(217,217,217);
	public static final Color 	LIGHT_GRADIENT_TOP = new Color(170,170,170);
	public static final Color 	LIGHT_GRADIENT_BOTTOM = new Color(225,225,225);
	public static final Color 	DARK_THEME_BG_COL = new Color(46,46,46);
	public static final Color 	DARK_THEME_RECIPE_BTN_COL = new Color(83,83,83);
	public static final Color 	DARK_GRADIENT_TOP = new Color(69,69,69);
	public static final Color 	DARK_GRADIENT_BOTTOM = new Color(31,31,31);

	static {
		Font font = null;
		try {
			InputStream fontStream = Main.class.getClassLoader().getResourceAsStream("Montserrat-VariableFont_wght.ttf");
			if (fontStream != null) {
				font = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(Font.BOLD, 15);
			} else {
				System.err.println("Font file not found");
				font = new Font("SansSerif", Font.BOLD, 15);
			}
		} catch (FontFormatException | IOException e) {
			System.err.println("Error loading font: " + e.getMessage());
			font = new Font("SansSerif", Font.BOLD, 15);
		}
		recipeTxtFont = font;
	}

	/**
	 * Private constructor prevents instantiation of this class.
	 */
	private Constants() {}
}