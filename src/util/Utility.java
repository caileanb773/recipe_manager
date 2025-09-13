package util;

import org.apache.commons.validator.routines.EmailValidator;

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
		return validator.isValid(email);
	}

}
