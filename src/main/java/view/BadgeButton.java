package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JButton;

/**
 * Author: Cailean Bernard
 * Contents: BadgeButton extends JButton and will draw a red circular graphic over 
 * the button at a provided orientation. A number will be drawn inside the graphic
 * defined by the user.
 */
@SuppressWarnings("serial")
public class BadgeButton extends JButton {

	private int minVal; // Min value before badge is displayed (usually 0)
	private int maxVal; // Max value before badge stops incrementing and displays a "+". ie., "99+"
	private int currVal;
	private int badgeSize;
	private Orientation orientation; // Where on the button the badge is displayed
	private final Color badgeRed = new Color(212, 38, 38);
	private final Color numColor = new Color(240, 240 ,240);

	public enum Orientation { 
		TOP_LEFT,
		TOP_RIGHT,
		BOTTOM_LEFT,
		BOTTOM_RIGHT
	}

	BadgeButton(String text, int min, int max, Orientation orient) {
		super(text);
		minVal = min;
		maxVal = max;
		orientation = orient;
		currVal = min;
	}

	@Override
	protected void paintComponent(Graphics g) {		
		super.paintComponent(g);

		// Don't need to draw the badge if there are are no notifications
		if (currVal <= minVal) {
			return;
		}

		// Draw the badge
		int buttonWidth = getWidth();
		int buttonHeight = getHeight();
		badgeSize = buttonWidth / 7;
		int padding = 1; // from the Button's bounding edges
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

		// Draw the badge at anchorX and anchorY
		drawBadge((Graphics2D) g, anchorX, anchorY, badgeSize);
	}

	private void drawBadge(Graphics2D g2, int x, int y, int size) {
		Graphics2D g = (Graphics2D) g2.create();

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		try {
			// Set the text
			String text;
			if (currVal > maxVal) {
				text = maxVal + "+";
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
			int textY = y + (size + textHeight) / 2 - 2;

			g.drawString(text, textX, textY);
		} finally {
			g.dispose();
		}
	}

	public void increment() {
		currVal++;
		clampVal();
		refreshBadge();
	}

	public void decrement() {
		currVal--;
		clampVal();
		refreshBadge();
	}

	public void setBadgeNum(int num) {
		currVal = num;
		clampVal();
		refreshBadge();
	}

	private void clampVal() {
		if (currVal > maxVal) {
			currVal = maxVal;
		} else if (currVal < minVal) {
			currVal = minVal;
		}
	}

	/* XXX Note for future me: eventually, getting a notification may occur
	asynchronously (i.e., not on the EDT. This repaint() call is fine for now,
	but in production when notifications come through on other threads (via net-
	working) then this will need to handle that */ 
	private void refreshBadge() {
		repaint();
	}

	int getBadgeNum() {
		return currVal;
	}

}
