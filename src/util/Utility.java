package util;

import javax.swing.JPanel;

import org.apache.commons.validator.routines.EmailValidator;

import definitions.Constants;

/*
 * Author: Cailean Bernard
 * Contents: Helper methods needed in more than one class.
 */

public class Utility {

	public static float getAmountAsFloat(String amount) throws NumberFormatException {
		try {
			if (amount.contains("/")) {
				String[] parts = amount.split("/");
				return Float.parseFloat(parts[0]) / Float.parseFloat(parts[1]);
			}
			return Float.parseFloat(amount);
		} catch (NumberFormatException e) {
			return -1;
		}

	}

	public static boolean isEmailValid(String email) {
		EmailValidator validator = EmailValidator.getInstance();

		for (char c : Constants.ILLEGAL_EMAIL_CHARS.toCharArray()) {
			if (email.indexOf(c) >= 0) {
				return false;
			}
		}

		return validator.isValid(email);
	}
	
	public static void revalidateAndRepaint(JPanel panel) {
		panel.revalidate();
		panel.repaint();
	}

}
