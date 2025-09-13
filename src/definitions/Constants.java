package definitions;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

import controller.Main;

/*
 * Author: Cailean Bernard
 * Contents: Constants that are needed in more than one class.
 */

public class Constants {
	
	public static final int BUTTON_HEIGHT = 30;
	public static final int BUTTON_WIDTH = 40;
	public static final int NUM_SHOWN_BUTTONS = 11;
	public static final int AMT_IDX = 0;
	public static final int UNIT_IDX = 1;
	public static final int NAME_IDX = 2;
	public static final int TAGS_IDX = 3;
	public static final int LENGTH_WITH_TAGS = 4;
	public static final int DEFAULT_LENGTH = 3;
	public static final int ADD_MODE = 0;
	public static final int EDIT_MODE = 1;
	public static final int VALUE_IDX = 1;
	public static final int SCROLL_SPEED = 19;
	public static final String RECIPE_SECT_DELIM = "§§";
	public static final String ING_TAG_DELIM = "¤";
	public static final String INGREDIENT_SECT_DELIM = " ";
	public static final Font titleFont = new Font("Serif", Font.BOLD, 16);
	public static final Font buttonFont = new Font("Serif", Font.BOLD, 16);
	public static final Font textFont = new Font("Serif", Font.PLAIN, 15);
	public static final Color bgGray = new Color(105,105,105);
	public static final Color rcpBtnGray = new Color(217,217,217);
	public static final Font recipeTxtFont;
	
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
	
	
	private Constants() {}
}
