package service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.Notification;
import model.NotificationType;
import model.Recipe;
import model.StaffMember;
import util.Utility;

/*
 * Author: Cailean Bernard
 * Contents: Manages read notifications vs. unread notifications and handles when
 * incoming notifications.
 */
public class NotificationService {

	// Notification-related
	private List<Notification> notifications;
	private List<NotificationListener> listeners;

	// Other
	private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);


	public NotificationService() {
		this.notifications = new ArrayList<>();
		this.listeners = new ArrayList<>();
	}

	public void addListener(NotificationListener nl) {
		listeners.add(nl);
	}

	public void removeListener(NotificationListener nl) {
		listeners.remove(nl);
	}

	public void addNotification(Notification n) {
		if (n == null) {
			logger.warn("Null notification passed to addNotification().");
			return;
		}

		if (notifications == null) {
			logger.warn("Notification list uninitialized: addNotification().");
			return;
		}

		if (!isNotificationValid(n)) {
			logger.warn("Notification does not have valid format/bad fields: addNotification().");
			return;
		}

		notifications.add(n);
		notifyListeners();
	}

	public void notifyListeners() {
		for (NotificationListener listener : listeners) {
			listener.notificationsChanged();
		}
	}

	public void removeNotification(Notification n) {
		if (n != null) {
			notifications.remove(n);
		} else {
			logger.warn("removeNotification(): Notification was null.");
		}
	}

	public void setNotificationInactive(Notification n) {
		if (n != null) {
			notifications.get(notifications.indexOf(n)).setInactive();;
		} else {
			logger.warn("setNotificationInactive(): Notification was null.");
		}
	}

	// XXX revisit this. Seems like this is pretty unnecessary
	public void setAllNotificationsInactive() {
		for (Notification n : notifications) {
			n.setInactive();
		}
	}
	
	public void setAllNotificationsSelected(boolean selected) {
		for (Notification n : notifications) {
			n.setSelected(selected);
		}
	}

	public int getNumActiveNotifications() {
		int num = 0;
		for (Notification n : notifications) {
			if (n.isActive()) {
				num++;
			}
		}
		return num;
	}

	// TODO
	/**
	 * Valid Notifications should always have a timestamp, a sender, a type, and 
	 * a recipe to which they are associated. Other fields are optional.
	 * 
	 * @param n - The notification to verify.
	 */
	public boolean isNotificationValid(Notification n) {
		boolean isValid = true;
		LocalDateTime timeSent = n.getTimeSent();
		StaffMember sender = n.getSender();
		NotificationType notifType = n.getNotificationType();
		Recipe recipe = n.getRecipe();

		// Check for null fields and return early if so
		if (timeSent == null || sender == null || notifType == null || recipe == null) {
			return false;
		}

		if (timeSent.isBefore(LocalDateTime.MIN)
				|| timeSent.isAfter(LocalDateTime.MAX)) {
			isValid = false;
		}
		
		if (sender.getName() == null || sender.getName().isEmpty()) {
			isValid = false;
		}
		
		if (!(notifType == NotificationType.ADD || notifType == NotificationType.EDIT)) {
			isValid = false;
		}
		
		if (recipe.getTitle() == null || recipe.getTitle().isEmpty()) {
			isValid = false;
		}

		return isValid;
	}

	public void sort(int mode, boolean direction) {
		Utility.sortNotifications(notifications, mode, direction);
	}

	public List<Notification> getNotifications() {
		return notifications;
	}

}
