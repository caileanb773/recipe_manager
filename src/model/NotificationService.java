package model;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import definitions.Notification;
import util.Listenable;
import util.Utility;

/*
 * Author: Cailean Bernard
 * Contents: Manages read notifications vs. unread notifications and handles when
 * incoming notifications.
 */
public class NotificationService implements Listenable {
	
	// Notification-related
	private ArrayList<Notification> notifications;
	
	// Other
	private ActionListener listener;
	private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

	
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
	}
	
	public void removeNotification() {
		
	}
	
	public void markNotificationRead() {
		
	}
	
	public void markNotificationUnread() {
		
	}
	
	public boolean isNotificationValid(Notification n) {
		// TODO method stub
		return true;
	}
	
	public void sort(int mode, boolean direction) {
		Utility.sortNotifications(notifications, mode, direction);
	}
	
	public ArrayList<Notification> getNotifications() {
		return notifications;
	}
	
	@Override
	public void registerController(ActionListener listener) {
		listener = listener;
	}
	
}
