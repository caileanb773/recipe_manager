package util;

import static definitions.Constants.ASCENDING;
import static definitions.Constants.DESCENDING;
import static definitions.Constants.SORT_RCPNAME;
import static definitions.Constants.SORT_SENDER;
import static definitions.Constants.SORT_TIME;
import static definitions.Constants.SORT_TYPE;
import java.util.ArrayList;
import java.util.Comparator;
import javax.swing.JPanel;
import org.apache.commons.validator.routines.EmailValidator;
import definitions.Constants;
import definitions.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Author: Cailean Bernard
 * Contents: Helper methods needed in more than one class.
 */
public class Utility {

	private static final Logger logger = LoggerFactory.getLogger(Utility.class);
	
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

	public static void sortNotifications(
			ArrayList<Notification> list,
			int mode, 
			boolean direction) {

		if (list == null || list.isEmpty()) {
			logger.warn("Can't sort a null/empty list: sortNotifications().");
			return;			
		}

		switch (mode) {
		case SORT_TIME:
			sortByTime(list, direction);
			break;
		case SORT_SENDER:
			sortBySender(list, direction);
			break;
		case SORT_TYPE:
			sortByType(list);
			break;
		case SORT_RCPNAME:
			sortByRcpName(list);
			break;
		}
	}

	@Deprecated
	private static void bubbleSort(ArrayList<Notification> list, boolean direction) {
		if (list == null || list.isEmpty()) {
			logger.warn("Cannot sort a null/empty list: bubbleSort().");
			return;
		}

		int n = list.size();
		boolean elmtSwapped;

		do {

			elmtSwapped = false;
			for (int i = 0; i < n - 1; i++) {
				int j = i + 1;
				int element1 = 0;
				int element2 = 0;

				if (direction == DESCENDING) {
					if (element1 < element2) {
						elmtSwapped = true;
						Notification temp = list.get(i);
						list.set(i, list.get(j));
						list.set(j, temp);
					}
				} else if (direction == ASCENDING) {
					if (element1 > element2) {
						elmtSwapped = true;
						Notification temp = list.get(i);
						list.set(i, list.get(j));
						list.set(j, temp);
					}
				}

			}
		} while (elmtSwapped);
	}

	private static void sortByTime(ArrayList<Notification> list, boolean direction) {
		if (direction == DESCENDING) {
			list.sort(Comparator.comparing(Notification::getTimeSent));
		} else if (direction == ASCENDING) {
			list.sort(Comparator.comparing(Notification::getTimeSent).reversed());
		}

	}

	private static void sortBySender(ArrayList<Notification> list, boolean direction) {

	    Comparator<Notification> cmp =
	        Comparator.comparing(
	            n -> n.getSender().getName(),
	            String.CASE_INSENSITIVE_ORDER
	        );

	    if (direction == ASCENDING) {
	        list.sort(cmp);
	    } else {
	        list.sort(cmp.reversed());
	    }
	}



	private static void sortByType(ArrayList<Notification> list) {

	}

	private static void sortByRcpName(ArrayList<Notification> list) {

	}

}
