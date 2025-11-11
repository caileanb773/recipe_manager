package view;

import static definitions.Constants.LIGHT_GRADIENT_BOTTOM;
import static definitions.Constants.LIGHT_GRADIENT_TOP;
import static definitions.Constants.LIGHT_THEME_BG_COL;
import static definitions.Constants.LIGHT_THEME_RECIPE_BTN_COL;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javax.swing.JPanel;
import definitions.Notification;

public class NotificationScreen extends JPanel {
	

	// Constant

	
	// Other
	private ArrayList<Notification> notifications;
	private ResourceBundle bundle;
	private ActionListener listener;
	private Color topGradient;
	private Color botGradient;
	private Color rcpBtnColor;
	private Color rcpBtnFontCol;
	private Color panelBgCol;
	
	public NotificationScreen(ResourceBundle bundle) {
		this.bundle = bundle;
		
		// Default theme
		topGradient = LIGHT_GRADIENT_TOP;
		botGradient = LIGHT_GRADIENT_BOTTOM;
		rcpBtnColor = LIGHT_THEME_RECIPE_BTN_COL;
		rcpBtnFontCol = Color.black;
		panelBgCol = LIGHT_THEME_BG_COL;
		
		// Swing init
		
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g.create();
		int w = getWidth();
		int h = getHeight();

		g2d.setPaint(new GradientPaint(0, 0, topGradient, 0, h, botGradient));
		g2d.fillRect(0, 0, w, h);
		g2d.dispose();
	}
	
}
