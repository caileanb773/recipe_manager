package model;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import definitions.Notification;
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
	
	// TODO
	public void removeNotification() {
		
	}
	
	// TODO
	public void markNotificationRead() {
		
	}
	
	// TODO
	public void markNotificationUnread() {
		
	}
	
	public boolean isNotificationValid(Notification n) {
		// TODO method stub
		return true;
	}
	
	public void sort(int mode, boolean direction) {
		Utility.sortNotifications(notifications, mode, direction);
	}
	
	public int getNumUnreadNotifications() {
		int num = 0;
		for (Notification n : notifications) {
			if (!n.isRead()) {
				num++;
			}
		}
		return num;
	}
	
	public List<Notification> getNotifications() {
		return notifications;
	}
	
}
