package util;

/*
 * Author: Cailean Bernard
 * Contents: Helper methods needed in more than one class.
 */

public class Utility {
	
	public static float getAmountAsFloat(String amount) {
		try {
			if (amount.contains("/")) {
				String[] parts = amount.split("/");
				return Float.parseFloat(parts[0]) / Float.parseFloat(parts[1]);
			}
			return Float.parseFloat(amount);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return -1;
		}

	}

}
