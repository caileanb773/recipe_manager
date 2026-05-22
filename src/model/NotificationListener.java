package model;

/**
 * Author: Cailean Bernard
 * Contents: NotificationListener alerts subscribers of its events when the state
 * of the notification service has changed (a new notification has arrived, or
 * a notification has been marked as read, etc.)
 */
public interface NotificationListener {
	
	void notificationsChanged();

}
