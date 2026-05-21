package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JButton;

/**
 * BadgeButton extends JButton and will draw a red pill-shaped graphic over the
 * button on the 
 * 
 * Author: Cailean Bernard
 * */
@SuppressWarnings("serial")
public class BadgeButton extends JButton {

	/* When (in this case) there is a new notification, call increment() on the 
	 * button itself. When the user deletes/reads a notification, call increment()
	 * again on the badge.
	 * 
	 * When the user navigates back to the recipe screen, call refresh() to show
	 * the correct number of notifications 
	 */

	private int minVal; // Min value before badge is displayed (usually 0)
	private int maxVal; // Max value before badge stops incrementing and displays a "+". ie., "99+"
	private int currVal;
	private String displayStr;
	private int badgeSize;
	private Orientation orientation; // Where on the button the badge is displayed
	//private static final Logger logger = LoggerFactory.getLogger(BadgeButton.class); XXX remove if unused
	private final Color badgeRed = new Color(212, 38, 38);
	private final Color numColor = new Color(240, 240 ,240);

	public enum Orientation { 
		TOP_LEFT,
		TOP_RIGHT,
		BOTTOM_LEFT,
		BOTTOM_RIGHT
	}

	BadgeButton(String text, int min, int max, Orientation orient, int size) {
		super(text);
		minVal = min;
		maxVal = max;
		orientation = orient;
		currVal = min;
		badgeSize = size;
	}

	@Override
	protected void paintComponent(Graphics g) {		
		super.paintComponent(g);

		// Don't need to draw the badge if there are are no notifications
		if (currVal < minVal) {
			return;
		}

		// Draw the badge
		int buttonWidth = getWidth();
		int buttonHeight = getHeight();
		badgeSize = buttonWidth / 3;
		int padding = 4; // from the Button's bounding edges
		int anchorX = 0;
		int anchorY = 0;

		switch (orientation) {
		case TOP_LEFT:
			anchorX = padding;
			anchorY = padding;
			break;
		case TOP_RIGHT:
			anchorX = buttonWidth - badgeSize - padding;
			anchorY = padding;
			break;
		case BOTTOM_LEFT:
			anchorX = padding;
			anchorY = buttonHeight - badgeSize - padding;
			break;
		case BOTTOM_RIGHT:
			anchorX = buttonWidth - badgeSize - padding;
			anchorY = buttonHeight - badgeSize - padding;
			break;
		default:
			// Top-right by default
			anchorX = buttonWidth - badgeSize - padding;
			anchorY = padding;
		}

		if (anchorX <= 0) {
			anchorX = 0;
		}

		if (anchorY <= 0) {
			anchorY = 0;
		}

		// Draw the badge at anchorX and anchorY3
		drawBadge((Graphics2D) g, anchorX, anchorY, badgeSize);
	}

	void drawBadge(Graphics2D g2, int x, int y, int size) {
		Graphics2D g = (Graphics2D) g2.create();

		try {
			// Set the text
			String text;
			if (currVal > 99) {
				text = "99+";
			} else {
				text = Integer.toString(currVal);
			}

			// Paint circle
			g.setColor(badgeRed);
			g.fillOval(x, y, size, size);

			// Text
			Font font = getFont().deriveFont(Font.BOLD, size * 0.6f);
			g.setFont(font);
			g.setColor(numColor);

			FontMetrics metrics = g.getFontMetrics();

			int textWidth = metrics.stringWidth(text);
			int textHeight = metrics.getAscent();

			int textX = x + (size - textWidth) / 2;
			int textY = y + (size - textHeight) / 2;

			g.drawString(text, textX, textY);
		} finally {
			g.dispose();
		}
	}

	void refreshBadge() {

	}

	void increment() {

	}

	void decrement() {

	}

	void setBadgeNum() {

	}

	int getBadgeNum() {
		return 0;
	}

}
